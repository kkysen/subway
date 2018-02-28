package sen.khyber.web.subway.client.status;

import sen.khyber.util.StringBuilderAppendable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/25/2018.
 *
 * @author Khyber Sen
 */
@RequiredArgsConstructor
@Accessors(fluent = true)
@Getter
public final class MTAUserUpdate implements StringBuilderAppendable {
    
    private final @NotNull long[] uidsToRemove;
    private final @NotNull MTAUser[] newUsers;
    
    @Override
    public final @NotNull StringBuilder appendSelf(final @NotNull StringBuilder sb) {
        sb.append("uidsToRemove = ").append(Arrays.toString(uidsToRemove));
        sb.append(", ");
        sb.append("newUsers = ");
        for (final MTAUser user : newUsers) {
            user.append(sb).append(", ");
        }
        sb.delete(sb.length() - ", ".length(), sb.length());
        return sb;
    }
    
    @Override
    public final @NotNull StringBuilder append(@NotNull final StringBuilder sb) {
        sb.append("MTAUserUpdate[");
        //noinspection ResultOfMethodCallIgnored
        appendSelf(sb);
        return sb.append(']');
    }
    
    @Override
    public final @NotNull String toString() {
        return defaultToString();
    }
    
}