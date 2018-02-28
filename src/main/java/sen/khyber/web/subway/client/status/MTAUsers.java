package sen.khyber.web.subway.client.status;

import sen.khyber.util.StringBuilderAppendable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/25/2018.
 *
 * @author Khyber Sen
 */
public final class MTAUsers implements MTAUpdateProcessor<IOException>, StringBuilderAppendable {
    
    private final @NotNull MTALine<?> line;
    
    private final @NotNull Map<Long, MTAUser> users;
    
    public MTAUsers(final @NotNull MTALine<?> line) {
        Objects.requireNonNull(line);
        this.line = line;
        users = new HashMap<>();
    }
    
    public final @NotNull MTAUser[] users() {
        return users.values().toArray(new MTAUser[users.size()]);
    }
    
    public final void update(final @NotNull MTAUserUpdate userUpdate) {
        for (final long uid : userUpdate.uidsToRemove()) {
            users.remove(uid);
        }
        for (final MTAUser user : userUpdate.newUsers()) {
            users.put(user.id(), user);
        }
    }
    
    @Override
    public final void update(final @NotNull MTALineStatus status) {
        // TODO
    }
    
    public final void doUpdate() {
        
    }
    
    @Override
    public final @NotNull StringBuilder appendSelf(final @NotNull StringBuilder sb) {
        final int size = users.size();
        users.values().forEach(user -> {
            //noinspection ResultOfMethodCallIgnored
            user.append(sb);
            sb.append(", ");
        });
        sb.delete(sb.length() - ", ".length(), sb.length());
        return sb;
    }
    
    @Override
    public final @NotNull StringBuilder append(@NotNull final StringBuilder sb) {
        sb.append("MTAUsers[");
        appendSelf(sb);
        return sb.append(']');
    }
    
    @Override
    public final @NotNull String toString() {
        return defaultToString();
    }
    
}