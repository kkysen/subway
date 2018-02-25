package sen.khyber.web.subway.client.status;

import sen.khyber.io.IO;
import sen.khyber.unsafe.buffers.UnsafeBuffer;
import sen.khyber.unsafe.buffers.UnsafeSerializable;
import sen.khyber.util.Indent;
import sen.khyber.util.Iterate;
import sen.khyber.util.StringBuilderAppendable;
import sen.khyber.web.client.WebClient;
import sen.khyber.web.client.WebResponse;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static sen.khyber.unsafe.fields.ByteBufferUtils.put;

/**
 * Created by Khyber Sen on 2/16/2018.
 *
 * @author Khyber Sen
 */
public class MTASystemStatus
        implements StringBuilderAppendable, UnsafeSerializable, Runnable, Closeable {
    
    private static final ZoneId ZONE = ZoneId.of("America/New_York");
    
    static @Nullable Instant toInstant(final @Nullable LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.atZone(ZONE).toInstant();
    }
    
    static @Nullable LocalDateTime toLocalDateTime(final @Nullable Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.atZone(ZONE).toLocalDateTime();
    }
    
    private static final int DEFAULT_INTERVAL_SECONDS = 60;
    
    private final @NotNull MTALine<?>[][] linesMap = new MTALine[MTAType.numTypes()][];
    
    private int fillLinesMapAndCountTotalNumLines() {
        int totalNumLines = 0;
        for (final MTAType type : MTAType.values()) {
            totalNumLines += type.numLines();
            final MTALine<?>[] lines = new MTALine[type.numLines()];
            for (final MTALine<?> line : type.lines()) {
                lines[line.ordinal()] = line;
            }
            linesMap[type.ordinal()] = lines;
        }
        return totalNumLines;
    }
    
    private final int totalNumLines = fillLinesMapAndCountTotalNumLines();
    
    private @Nullable LocalDateTime timeStamp;
    
    private boolean showOnlyDelayedLines = false;
    
    private boolean debug = true; // TODO change later
    
    private final int intervalSeconds;
    
    private final WebClient client = WebClient.get(); // could change
    
    private final @NotNull Timer timer = new Timer(true);
    private final Object runLock = new Object();
    private volatile boolean isRunning = false;
    private volatile @Nullable RunMTASystemStatusTask task = null;
    
    public MTASystemStatus(final int intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
        for (final MTALine<?>[] lines : linesMap) {
            for (final MTALine<?> line : lines) {
                line.initLineStatus();
            }
        }
    }
    
    public MTASystemStatus() {
        this(DEFAULT_INTERVAL_SECONDS);
    }
    
    public final void showStatusText(final boolean showStatusText) {
        for (final MTALine<?>[] lines : linesMap) {
            for (final MTALine<?> line : lines) {
                line.lineStatus().showText(showStatusText);
            }
        }
    }
    
    public final void showStatusText() {
        showStatusText(true);
    }
    
    public final void showOnlyDelayedLines(final boolean showOnlyDelayedLines) {
        this.showOnlyDelayedLines = showOnlyDelayedLines;
    }
    
    public final void showOnlyDelayedLines() {
        showOnlyDelayedLines(true);
    }
    
    public final void debug(final boolean debug) {
        this.debug = debug;
    }
    
    public final void debug() {
        debug(true);
    }
    
    /**
     * TODO document
     *
     * @param newStatuses new statuses array
     *                    indexed by {@link MTAType#ordinal} and then {@link MTALine#ordinal}
     */
    private void update(final @NotNull MTALineStatus[][] newStatuses) {
        for (int i = 0; i < newStatuses.length; i++) {
            for (int j = 0; j < newStatuses[i].length; j++) {
                linesMap[i][j].lineStatus().set(newStatuses[i][j], this);
            }
        }
    }
    
    private static final DateTimeFormatter timeStampFormatter = new DateTimeFormatterBuilder()
            .append(MTADateTimes.dateFormatter)
            .appendLiteral(' ')
            .append(MTADateTimes.clockTimeFormatter)
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .appendLiteral(' ')
            .appendText(ChronoField.AMPM_OF_DAY)
            .toFormatter();
    
    // TODO catch exceptions
    private @NotNull MTALineStatus[][] parse(final @NotNull WebResponse response)
            throws IOException, DocumentException {
        //        System.out.println(response.string().substring(0, 1000));
        final SAXReader reader = new SAXReader();
        final InputStream inputStream = response.inputStream();
        final Document document = reader.read(inputStream);
        final Element root = document.getRootElement();
        final String timeStamp = root.elementText("timestamp");
        Objects.requireNonNull(timeStamp); // TODO throw more specific exception
        final LocalDateTime dateTime =
                LocalDateTime.parse(timeStamp, timeStampFormatter); // TODO catch parse exceptions
        this.timeStamp = dateTime;
        final MTALineStatus[][] newStatuses = new MTALineStatus[MTAType.numTypes()][];
        for (final Element typeElement : Iterate.over(root::elementIterator)) {
            final String name = typeElement.getName();
            final MTAType type = MTAType.parse(name);
            if (type == null) {
                continue;
            }
            final MTALineStatus[] newStatusesForType = new MTALineStatus[type.numLines()];
            newStatuses[type.ordinal()] = newStatusesForType;
            for (final Element lineElement : Iterate.over(typeElement::elementIterator)) {
                final MTALineStatus status = MTALineStatus.parse(type, dateTime, lineElement);
                //                status.showText(false);
                //                System.out.println(status);
                newStatusesForType[status.lineOrdinal()] = status;
            }
        }
        return newStatuses;
    }
    
    // TODO catch exceptions
    private void update(final @NotNull WebResponse response) throws IOException, DocumentException {
        update(parse(response));
    }
    
    // TODO catch exceptions
    private void update() throws IOException, DocumentException {
        final WebResponse response = debug
                ? client.forPath(IO.Downloads.resolve("serviceStatus.xml"))
                : client.forUrl("http://web.mta.info/status/serviceStatus.txt");
        try (response) {
            update(response);
        }
    }
    
    private final class RunMTASystemStatusTask extends TimerTask {
        
        private void keepUpdating() {
            //noinspection InfiniteLoopStatement
            while (true) {
                //noinspection OverlyBroadCatchBlock
                try {
                    update();
                    // TODO
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        @Override
        public final void run() {
            try {
                keepUpdating();
            } catch (final Throwable e) {
                synchronized (runLock) {
                    e.printStackTrace();
                    task = null;
                    super.cancel();
                }
            }
        }
        
    }
    
    @Override
    public final void run() {
        if (isRunning) {
            return;
        }
        synchronized (runLock) {
            if (isRunning) {
                return;
            }
            if (task == null) {
                task = new RunMTASystemStatusTask();
            }
            //noinspection ConstantConditions
            timer.scheduleAtFixedRate(task, 0, intervalSeconds);
            isRunning = true;
        }
    }
    
    public final void cancel() {
        if (task == null) {
            return;
        }
        synchronized (runLock) {
            if (task == null) {
                return;
            }
            //noinspection ConstantConditions
            task.cancel();
            isRunning = false;
        }
    }
    
    @Override
    public final void close() {
        cancel();
        timer.cancel();
    }
    
    @Override
    public final @NotNull StringBuilder append(final @NotNull StringBuilder sb) {
        // save local copy to not allow mutatation in the middle of method
        final boolean showOnlyDelayedLines = this.showOnlyDelayedLines;
        
        sb.append("MTASystemStatus {");
        if (timeStamp == null) {
            return sb.append("Never Updated}");
        }
        
        final Indent indent = Indent.get();
        indent.indent();
        
        indent.append(sb)
                .append("timeStamp: ")
                .append(MTADateTimes.dateTimeFormatter.format(timeStamp))
                .append(',');
        
        indent.append(sb)
                .append("delayedLinesOnly: ")
                .append(showOnlyDelayedLines)
                .append(',');
        
        for (int i = 0; i < linesMap.length; i++) {
            indent.append(sb);
            final MTAType type = MTAType.get(i);
            sb.append(type.name());
            sb.append(" {");
            indent.indent();
            for (int j = 0; j < linesMap[i].length; j++) {
                final MTALine<?> line = type.line(j);
                if (showOnlyDelayedLines && line.lineStatus().get().status()
                        == MTAStatus.GOOD_SERVICE) {
                    continue;
                }
                indent.append(sb);
                line.appendSelf(sb);
            }
            indent.unindent();
            if (sb.charAt(sb.length() - 1) != '{') {
                // if last char is '{', just place '}' immediately after
                indent.append(sb);
            }
            sb.append("},");
        }
        indent.unindent();
        indent.append(sb);
        sb.append('}');
        return sb;
    }
    
    @Override
    public final @NotNull String toString() {
        return defaultToString();
    }
    
    @Override
    public final long serializedLongLength() {
        long length = Integer.BYTES;
        for (final MTALine<?>[] lines : linesMap) {
            for (final MTALine<?> line : lines) {
                length += line.serializedLongLength();
            }
        }
        return length;
    }
    
    @Override
    public final void serialize(final @NotNull ByteBuffer out) {
        out.putInt(intervalSeconds);
        for (final MTALine<?>[] lines : linesMap) {
            for (final MTALine<?> line : lines) {
                put(out, line);
            }
        }
    }
    
    @Override
    public final void serializeUnsafe(final @NotNull UnsafeBuffer out) {
        out.putInt(intervalSeconds);
        for (final MTALine<?>[] lines : linesMap) {
            for (final MTALine<?> line : lines) {
                out.put(line);
            }
        }
    }
    
    private static MTASystemStatus deserialize(final int intervalSeconds,
            final Supplier<MTALine<?>> deserializer) {
        //noinspection resource,IOResourceOpenedButNotSafelyClosed
        final MTASystemStatus mtaSystemStatus = new MTASystemStatus(intervalSeconds);
        final MTALine<?>[][] linesMap = mtaSystemStatus.linesMap;
        for (int i = 0; i < linesMap.length; i++) {
            for (int j = 0; j < linesMap[i].length; j++) {
                linesMap[i][j] = deserializer.get();
            }
        }
        return mtaSystemStatus;
    }
    
    public static final MTASystemStatus deserialize(final @NotNull ByteBuffer in) {
        return deserialize(in.getInt(), () -> MTALine.deserialize(in));
    }
    
    public static final MTASystemStatus deserialize(final @NotNull UnsafeBuffer in) {
        return deserialize(in.getInt(), () -> MTALine.deserialize(in));
    }
    
    public static void main(final String[] args) throws IOException, DocumentException {
        System.out.println(LocalDateTime.now().format(timeStampFormatter));
        try (final MTASystemStatus mtaSystemStatus = new MTASystemStatus()) {
            mtaSystemStatus.debug(false);
            mtaSystemStatus.showOnlyDelayedLines();
            mtaSystemStatus.update();
            System.out.println(mtaSystemStatus);
            System.out.println(mtaSystemStatus.serializedLongLength());
            mtaSystemStatus.serializeUnsafe(IO.Downloads.resolve("mta.txt"));
            mtaSystemStatus.serializeCompressedTruncated(IO.Downloads.resolve("mta.txt.lz4"));
        }
    }
    
}