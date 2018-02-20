package sen.khyber.web.subway.client.status;

import sen.khyber.util.Iterate;
import sen.khyber.util.ObjectUtils;
import sen.khyber.util.StringBuilderAppendable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Objects;

import org.dom4j.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;

import static sen.khyber.web.subway.client.status.MTADateTimes.clockTimeFormatter;
import static sen.khyber.web.subway.client.status.MTADateTimes.dateFormatter;
import static sen.khyber.web.subway.client.status.MTADateTimes.dateTimeFormatter;
import static sen.khyber.web.subway.client.status.MTADateTimes.timeFormatter;

/**
 * Created by Khyber Sen on 2/17/2018.
 *
 * @author Khyber Sen
 */
public final class MTALineStatus implements StringBuilderAppendable {
    
    private static final DateTimeFormatter dateTimeFormatterParser = new DateTimeFormatterBuilder()
            .append(dateFormatter)
            .appendLiteral(' ')
            .append(clockTimeFormatter)
            .appendText(ChronoField.AMPM_OF_DAY)
            .toFormatter();
    
    private final @NotNull MTAType type;
    private final int lineOrdinal;
    private final @NotNull MTAStatus status;
    private final @NotNull LocalDateTime startTime;
    private @Nullable LocalDateTime endTime;
    private final @Nullable String htmlText;
    private final @Nullable String text;
    
    private boolean showText = false;
    
    private MTALineStatus(final @NotNull MTALine<?> line,
            final @NotNull MTAStatus status, final @NotNull LocalDateTime startTime,
            final @Nullable String text) {
        ObjectUtils.requireNonNull(line, status, startTime);
        type = line.type();
        lineOrdinal = line.ordinal();
        this.status = status;
        this.startTime = startTime;
        endTime = null;
        htmlText = text;
        this.text = text == null ? null : Jsoup.parseBodyFragment(text).text();
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
    private static @Nullable String trimEmptyToNull(@Nullable String s) {
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
                    throw new IllegalStateException("extra element");
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
    
    public final void showText(final boolean showText) {
        this.showText = showText;
    }
    
    public final void showText() {
        showText(true);
    }
    
    public final MTAType type() {
        return type;
    }
    
    public final int lineOrdinal() {
        return lineOrdinal;
    }
    
    public final MTALine<?> line() {
        return type.line(lineOrdinal);
    }
    
    public final boolean isSameKind(final MTALineStatus lineStatus) {
        return type == lineStatus.type && lineOrdinal == lineStatus.lineOrdinal;
    }
    
    @SuppressWarnings("MethodOverloadsMethodOfSuperclass")
    public final boolean equals(final MTALineStatus lineStatus) {
        return isSameKind(lineStatus)
                && status == lineStatus.status
                && Objects.equals(text, lineStatus.text)
                && Objects.equals(htmlText, lineStatus.htmlText);
    }
    
    @Override
    public final boolean equals(final Object obj) {
        return obj != null
                && (obj == this || getClass() == obj.getClass()
                && equals((MTALineStatus) obj));
    }
    
    @Override
    public final int hashCode() {
        return Objects.hash(line(), status, startTime, htmlText);
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
        return start + end;
    }
    
    @Override
    public final @NotNull StringBuilder append(final @NotNull StringBuilder sb) {
        sb.append(type.name())
                .append("LineStatus[")
                .append(line().officialName())
                .append(": ")
                .append(status.officialName())
                .append(" at ")
                .append(formattedTime());
        if (showText) {
            sb.append(": ");
            sb.append(String.valueOf(text));
        }
        return sb.append(']');
    }
    
    @Override
    public final @NotNull String toString() {
        return defaultToString();
    }
    
}