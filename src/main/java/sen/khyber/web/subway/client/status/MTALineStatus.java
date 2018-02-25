package sen.khyber.web.subway.client.status;

import sen.khyber.unsafe.buffers.UnsafeBuffer;
import sen.khyber.unsafe.buffers.UnsafeSerializable;
import sen.khyber.unsafe.fields.StringUtils;
import sen.khyber.util.Iterate;
import sen.khyber.util.ObjectUtils;
import sen.khyber.util.StringBuilderAppendable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Objects;

import org.dom4j.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import static sen.khyber.unsafe.fields.ByteBufferUtils.*;
import static sen.khyber.web.subway.client.status.MTADateTimes.clockTimeFormatter;
import static sen.khyber.web.subway.client.status.MTADateTimes.dateFormatter;
import static sen.khyber.web.subway.client.status.MTADateTimes.dateTimeFormatter;
import static sen.khyber.web.subway.client.status.MTADateTimes.timeFormatter;

/**
 * Created by Khyber Sen on 2/17/2018.
 *
 * @author Khyber Sen
 */
@Accessors(fluent = true)
public final class MTALineStatus implements StringBuilderAppendable, UnsafeSerializable {
    
    private static final DateTimeFormatter dateTimeFormatterParser = new DateTimeFormatterBuilder()
            .append(dateFormatter)
            .appendLiteral(' ')
            .append(clockTimeFormatter)
            .appendText(ChronoField.AMPM_OF_DAY)
            .toFormatter();
    
    private static final ZoneId ZONE = ZoneId.of("America/New_York");
    
    private final @Getter @NotNull MTAType type;
    private final @Getter int lineOrdinal;
    private final @Getter @NotNull MTAStatus status;
    private final @NotNull LocalDateTime startTime;
    private @Nullable LocalDateTime endTime;
    private final @Nullable String rawHtmlText;
    private final @Nullable Document htmlTextDoc;
    
    private @Setter boolean showText = false;
    
    private MTALineStatus(final @NotNull MTALine<?> line,
            final @NotNull MTAStatus status, final @NotNull LocalDateTime startTime,
            final @Nullable LocalDateTime endTime, final @Nullable String text) {
        ObjectUtils.requireNonNull(line, status, startTime);
        type = line.type();
        lineOrdinal = line.ordinal();
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        rawHtmlText = text;
        htmlTextDoc = text == null ? null : Jsoup.parseBodyFragment(text);
    }
    
    private MTALineStatus(final @NotNull MTALine<?> line,
            final @NotNull MTAStatus status, final @NotNull LocalDateTime startTime,
            final @Nullable String text) {
        this(line, status, startTime, null, text);
    }
    
    static MTALineStatus createDefault(final @NotNull MTALine<?> line) {
        Objects.requireNonNull(line);
        return new MTALineStatus(line, MTAStatus.GOOD_SERVICE, LocalDateTime.now(), "");
    }
    
    private static MTALineStatus parse(final @NotNull MTAType type,
            final @NotNull LocalDateTime backupDateTime,
            final @NotNull String name, final @NotNull String statusText,
            final @Nullable String dateTime, final @Nullable String text) {
        //        System.out.println(name);
        Objects.requireNonNull(type);
        ObjectUtils.requireNonNull(name, statusText);
        // TODO change NPEs to more specific exception
        final MTALine<?> line = type.parseLine(name);
        Objects.requireNonNull(line);
        //        System.out.println(statusText);
        final MTAStatus status = MTAStatus.parse(statusText);
        Objects.requireNonNull(status);
        final @NotNull LocalDateTime startTime;
        if (dateTime == null) {
            startTime = backupDateTime;
        } else {
            startTime = LocalDateTime
                    .parse(dateTime, dateTimeFormatterParser); // TODO catch parse exceptions
        }
        return new MTALineStatus(line, status, startTime, text);
    }
    
    // TODO move this to own util class?
    private static @Nullable
    String trimEmptyToNull(@Nullable String s) {
        if (s == null) {
            return null;
        }
        s = s.trim();
        return s.isEmpty() ? null : s;
    }
    
    static final MTALineStatus parse(final @NotNull MTAType type,
            final @NotNull LocalDateTime backupDateTime,
            final @NotNull Element lineStatus) {
        Objects.requireNonNull(type);
        ObjectUtils.requireNonNull(backupDateTime,
                lineStatus); // TODO change this exception to more specific type
        String name = null;
        String status = null;
        String text = null;
        String date = null;
        String time = null;
        for (final Element element : Iterate.over(lineStatus::elementIterator)) {
            final String elementText = element.getText();
            switch (element.getName()) {
                case "name":
                    name = elementText;
                    break;
                case "status":
                    status = elementText;
                    break;
                case "text":
                    text = elementText;
                    break;
                case "Date":
                    date = elementText;
                    break;
                case "Time":
                    time = elementText;
                    break;
                default:
                    // TODO throw more specific exception
                    throw new IllegalStateException("extra element: " + element.getName());
            }
        }
        
        name = trimEmptyToNull(name);
        status = trimEmptyToNull(status);
        text = trimEmptyToNull(text);
        date = trimEmptyToNull(date);
        time = trimEmptyToNull(time);
        
        ObjectUtils.requireNonNull(name, status);
        final String dateTime = date == null || time == null ? null : date + ' ' + time;
        //noinspection ConstantConditions
        return parse(type, backupDateTime, name, status, dateTime, text);
    }
    
    public final void showText() {
        showText(true);
    }
    
    public final MTALine<?> line() {
        return type.line(lineOrdinal);
    }
    
    public final @Nullable String text() {
        return rawHtmlText;
    }
    
    public final boolean isSameKind(final MTALineStatus lineStatus) {
        return type == lineStatus.type && lineOrdinal == lineStatus.lineOrdinal;
    }
    
    @SuppressWarnings("MethodOverloadsMethodOfSuperclass")
    public final boolean equals(final MTALineStatus lineStatus) {
        return isSameKind(lineStatus)
                && status == lineStatus.status
                && Objects.equals(htmlTextDoc, lineStatus.htmlTextDoc)
                && Objects.equals(rawHtmlText, lineStatus.rawHtmlText);
    }
    
    @Override
    public final boolean equals(final Object obj) {
        return obj != null
                && (obj == this || getClass() == obj.getClass()
                && equals((MTALineStatus) obj));
    }
    
    @Override
    public final int hashCode() {
        return Objects.hash(line(), status, startTime, rawHtmlText);
    }
    
    public final void setSameEndTime(final @Nullable MTALineStatus lineStatus) {
        if (lineStatus != null) {
            endTime = lineStatus.endTime;
        }
    }
    
    public final String formattedTime() {
        if (endTime == null) {
            return dateTimeFormatter.format(startTime);
        }
        final String start;
        final String end;
        if (startTime.toLocalDate().isEqual(endTime.toLocalDate())) {
            //noinspection IfMayBeConditional
            if (startTime.get(ChronoField.AMPM_OF_DAY) == endTime.get(ChronoField.AMPM_OF_DAY)) {
                start = clockTimeFormatter.format(startTime);
            } else {
                start = timeFormatter.format(startTime);
            }
            end = dateTimeFormatter.format(endTime);
        } else {
            start = dateTimeFormatter.format(startTime);
            end = dateTimeFormatter.format(endTime);
        }
        return start + ", " + end;
    }
    
    @Override
    public final @NotNull StringBuilder appendSelf(final @NotNull StringBuilder sb) {
        sb.append(status.officialName())
                .append(" at ")
                .append(formattedTime());
        if (showText) {
            sb.append(": ");
            sb.append(String.valueOf(htmlTextDoc));
        }
        return sb;
    }
    
    @Override
    public final @NotNull StringBuilder append(final @NotNull StringBuilder sb) {
        sb.append(type.name())
                .append("LineStatus[")
                .append(line().officialName())
                .append(": ");
        appendSelf(sb);
        return sb.append(']');
    }
    
    @Override
    public final @NotNull String toString() {
        return defaultToString();
    }
    
    private static @Nullable Instant toInstant(final @Nullable LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.atZone(ZONE).toInstant();
    }
    
    private static @Nullable LocalDateTime toLocalDateTime(final @Nullable Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.atZone(ZONE).toLocalDateTime();
    }
    
    @Override
    public final long serializedLongLength() {
        final long statusLength = status.serializedLongLength();
        final long timeLength = 2 * Long.BYTES;
        final long textLength;
        if (rawHtmlText == null) {
            textLength = Byte.BYTES;
        } else {
            textLength = Short.BYTES + StringUtils.numBytes(rawHtmlText);
        }
        return statusLength + timeLength + textLength;
    }
    
    @Override
    public final void serialize(final @NotNull ByteBuffer out) {
        status.serialize(out);
        //noinspection ConstantConditions
        putInstantMillis(out, toInstant(startTime));
        putNullableInstantMillis(out, toInstant(endTime));
        putNullableShortString(out, rawHtmlText);
    }
    
    @Override
    public final void serializeUnsafe(final @NotNull UnsafeBuffer out) {
        status.serializeUnsafe(out);
        //noinspection ConstantConditions
        out.putInstantMillis(toInstant(startTime));
        out.putNullableInstantMillis(toInstant(endTime));
        out.putNullableShortString(rawHtmlText);
    }
    
    public static MTALineStatus deserialize(final @NotNull ByteBuffer in,
            final @NotNull MTALine<?> line) {
        final MTAStatus status = MTAStatus.deserialize(in);
        final LocalDateTime startTime = toLocalDateTime(getInstantMillis(in));
        final LocalDateTime endTime = toLocalDateTime(getNullableInstantMillis(in));
        final String text = getNullableShortString(in);
        //noinspection ConstantConditions
        return new MTALineStatus(line, status, startTime, endTime, text);
    }
    
    public static MTALineStatus deserialize(final @NotNull UnsafeBuffer in,
            final @NotNull MTALine<?> line) {
        final MTAStatus status = MTAStatus.deserialize(in);
        final LocalDateTime startTime = toLocalDateTime(in.getInstantMillis());
        final LocalDateTime endTime = toLocalDateTime(in.getNullableInstantMillis());
        final String text = in.getNullableShortString();
        //noinspection ConstantConditions
        return new MTALineStatus(line, status, startTime, endTime, text);
    }
    
}