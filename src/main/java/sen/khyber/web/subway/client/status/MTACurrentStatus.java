package sen.khyber.web.subway.client.status;

import sen.khyber.util.StringBuilderAppendable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/25/2018.
 *
 * @author Khyber Sen
 */
@RequiredArgsConstructor
@Accessors(fluent = true)
@Getter
public final class MTACurrentStatus implements StringBuilderAppendable {
    
    private final @NotNull MTALineStatus[][] statuses;
    private final @NotNull LocalDateTime timeStamp;
    
    public final boolean equals(final @NotNull MTACurrentStatus mtaCurrentStatus) {
        return timeStamp.equals(mtaCurrentStatus.timeStamp);
    }
    
    @Override
    public final boolean equals(final Object obj) {
        return obj != null
                && (obj == this || getClass() == obj.getClass()
                && equals((MTACurrentStatus) obj));
    }
    
    @Override
    public final int hashCode() {
        return timeStamp.hashCode();
    }
    
    @Override
    public final @NotNull StringBuilder append(@NotNull final StringBuilder sb) {
        MTADateTimes.dateTimeFormatter.format(timeStamp);
        // TODO
        return null;
    }
    
    @Override
    public final @NotNull String toString() {
        return defaultToString();
    }
    
    // TODO add serialization
    
}