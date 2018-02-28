package sen.khyber.web.subway.client.status;

import sen.khyber.io.IO;
import sen.khyber.unsafe.buffers.UnsafeBuffer;
import sen.khyber.unsafe.buffers.UnsafeSerializable;
import sen.khyber.util.Indent;
import sen.khyber.util.Iterate;
import sen.khyber.util.Retrier;
import sen.khyber.util.Retrier.RetrierBuilders;
import sen.khyber.util.StringBuilderAppendable;
import sen.khyber.util.function.IntBinaryPredicate;
import sen.khyber.web.client.WebClient;
import sen.khyber.web.client.WebResponse;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jetbrains.annotations.Contract;
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
    
    @Contract("null -> null; !null -> !null")
    static @Nullable Instant toInstant(final @Nullable LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.atZone(ZONE).toInstant();
    }
    
    @Contract("null -> null; !null -> !null")
    static @Nullable LocalDateTime toLocalDateTime(final @Nullable Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.atZone(ZONE).toLocalDateTime();
    }
    
    private static final int DEFAULT_INTERVAL_SECONDS = 60;
    
    private final @NotNull MTALine<?>[][] linesMap;
    private final @NotNull MTALineUpdateProcessor[][] updateProcessorsMap;
    private final @NotNull MTAUser[][][] usersMap;
    
    {
        linesMap = new MTALine[MTAType.numTypes()][];
        updateProcessorsMap = new MTALineUpdateProcessor[linesMap.length][];
        usersMap = new MTAUser[linesMap.length][][];
    }
    
    private @NotNull LocalDateTime timeStamp = LocalDateTime.now();
    
    private boolean showOnlyDelayedLines = false;
    
    private boolean debug = true; // TODO change later
    
    private final int intervalSeconds;
    
    private final Object updateLock = new Object();
    
    private final WebClient client = WebClient.get(); // could change
    
    private final @NotNull Timer timer = new Timer(true);
    private final Object runLock = new Object();
    private volatile boolean isRunning = false;
    private volatile @Nullable RunMTASystemStatusTask task = null;
    
    // TODO use builder
    
    private final @NotNull MTAUserDB userDB; // TODO
    
    private final @NotNull Path dir; // TODO
    
    private void setupLinesAndUpdateProcessors() throws IOException {
        int totalNumLines = 0;
        for (final MTAType type : MTAType.values()) {
            totalNumLines += type.numLines();
            final MTALine<?>[] lines = new MTALine[type.numLines()];
            final MTALineUpdateProcessor[] updateProcessors =
                    new MTALineUpdateProcessor[lines.length];
            for (final MTALine<?> line : type.lines()) {
                line.initLineStatus();
                final int j = line.ordinal();
                lines[j] = line;
                updateProcessors[j] = new MTALineUpdateProcessor(line, dir);
            }
            final int i = type.ordinal();
            linesMap[i] = lines;
            updateProcessorsMap[i] = updateProcessors;
            usersMap[i] = new MTAUser[lines.length][];
        }
    }
    
    public MTASystemStatus(final int intervalSeconds) throws IOException {
        this.intervalSeconds = intervalSeconds;
        setupLinesAndUpdateProcessors();
    }
    
    public MTASystemStatus() throws IOException {
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
    
    private @NotNull MTALineStatus[][] rawCurrentStatuses() {
        final MTALineStatus[][] currentStatuses = new MTALineStatus[linesMap.length][];
        for (int i = 0; i < linesMap.length; i++) {
            currentStatuses[i] = new MTALineStatus[linesMap[i].length];
            for (int j = 0; j < linesMap[i].length; j++) {
                currentStatuses[i][j] = linesMap[i][j].lineStatus().get();
            }
        }
        return currentStatuses;
    }
    
    public final @NotNull MTACurrentStatus currentStatus() {
        synchronized (updateLock) {
            return new MTACurrentStatus(rawCurrentStatuses(), timeStamp);
        }
    }
    
    private static void updateUsers(final @NotNull List<MTAUser> users,
            final @NotNull List<Pair<MTAUser, IOException>> failed) {
        users.parallelStream()
                .forEach(user -> {
                    try {
                        user.update();
                    } catch (final IOException e) {
                        failed.add(Pair.of(user, e));
                    }
                });
    }
    
    private static final RetrierBuilders<MTAUser> userRetrierBuilders = Retrier.builders();
    
    private void updateNoSync(final @NotNull MTALineStatus[][] newStatuses) throws IOException {
        // TODO make this asynchronous
        // TODO the status updates are immutable and asynchronous
        // TODO the user updates are asynchronous but mutable, 
        // TODO     so must complete after status updates and before next update round
        
        final List<int[]> indicesToUpdate = new ArrayList<>(); // process all updates at once
        
        // update statuses and get current users
        for (int i = 0; i < newStatuses.length; i++) {
            for (int j = 0; j < newStatuses[i].length; j++) {
                final MTALineUpdateProcessor updateProcessor = updateProcessorsMap[i][j];
                if (linesMap[i][j].lineStatus().set(newStatuses[i][j])) {
                    indicesToUpdate.add(new int[] {i, j});
                }
                usersMap[i][j] = updateProcessor.users();
            }
        }
        
        final MTAUser[] usersToUpdate = indicesToUpdate
                .parallelStream()
                .flatMap(indices -> {
                    final int i = indices[0];
                    final int j = indices[1];
                    MTALineStatus status = newStatuses[i][j];
                    MTAUser[] users = updateProcessorsMap[i][j].users();
                    for (MTAUser user : users) {
                        user.prepareUpdate(status);
                    }
                    return Arrays.stream(users);
                })
                .toArray(MTAUser[]::new);
        
        final double maxTimePercent = 0.5;
        final int secondsSleep = 1;
        final long start = System.currentTimeMillis();
        userRetrierBuilders.exceptionalIO(MTASystemStatus::updateUsers)
                .sleepLengthMillis(secondsSleep * 1000)
                .maxAttempts((int) (intervalSeconds / (double) secondsSleep))
                .stopTrying(IntBinaryPredicate.fromSupplier(() ->
                        System.currentTimeMillis() - start
                                > intervalSeconds * 1000 * maxTimePercent
                ))
                .build()
                .keepTrying(usersToUpdate)
                .forEach(System.out::println); // TODO better error handling
        // TODO use Retrier and updateUsers method
        
        // fetch new users for next time
        final Instant nextUpdateTime = toInstant(timeStamp).plusSeconds(intervalSeconds);
        final MTAUserUpdate[][] userUpdates = userDB.fetchNewUsers(usersMap, nextUpdateTime);
        for (int i = 0; i < newStatuses.length; i++) {
            for (int j = 0; j < newStatuses[i].length; j++) {
                updateProcessorsMap[i][j].update(userUpdates[i][j]);
            }
        }
    }
    
    private void update(final @NotNull MTALineStatus[][] newStatuses) throws IOException {
        synchronized (updateLock) {
            updateNoSync(newStatuses);
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
    
    // TODO add updateLock
    
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
    
    private void deserialize(final Supplier<MTALine<?>> deserializer) throws IOException {
        // preload deserialization to not interfere with update cycle
        final MTALine<?>[][] newLinesMap = linesMap.clone();
        for (int i = 0; i < newLinesMap.length; i++) {
            newLinesMap[i] = linesMap[i].clone();
            for (int j = 0; j < newLinesMap[i].length; j++) {
                newLinesMap[i][j] = deserializer.get();
            }
        }
        
        // do actual swap synchronized, 
        // but since it's only an array copy, 
        // it's fast enough that it won't delay the update cycle at all
        synchronized (updateLock) {
            // ok b/c intrinsic
            //noinspection CallToNativeMethodWhileLocked
            System.arraycopy(newLinesMap, 0, linesMap, 0, newLinesMap.length);
        }
    }
    
    public final void deserialize(final @NotNull ByteBuffer in)
            throws IOException {
        deserialize(() -> MTALine.deserialize(in));
    }
    
    public final void deserialize(final @NotNull UnsafeBuffer in)
            throws IOException {
        deserialize(() -> MTALine.deserialize(in));
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