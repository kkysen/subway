package sen.khyber.web.subway.client.status;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * Created by Khyber Sen on 2/18/2018.
 *
 * @author Khyber Sen
 */
public final class MTADateTimes {
    
    private MTADateTimes() {}
    
    static final DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.MONTH_OF_YEAR)
            .appendLiteral('/')
            .appendValue(ChronoField.DAY_OF_MONTH, 2)
            .appendLiteral('/')
            .appendValue(ChronoField.YEAR)
            .toFormatter();
    
    static final DateTimeFormatter clockTimeFormatter = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.CLOCK_HOUR_OF_AMPM)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .toFormatter();
    
    static final DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder()
            .append(clockTimeFormatter)
            //            .appendLiteral(' ')
            .appendText(ChronoField.AMPM_OF_DAY)
            .toFormatter();
    
    // e.x. 4:56PM 2/17/2018
    static final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
            .append(timeFormatter)
            .appendLiteral(", ")
            .append(dateFormatter)
            .toFormatter();
    
}