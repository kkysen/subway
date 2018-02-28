package sen.khyber.web.subway.client.status;

import sen.khyber.util.StringBuilderAppendable;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Khyber Sen on 2/25/2018.
 *
 * @author Khyber Sen
 */
@Accessors(fluent = true)
public final class MTAUser implements MTAUpdateProcessor<IOException>, StringBuilderAppendable {
    
    private final @Getter long id;
    
    private @Nullable MTALineStatus pendingStatus;
    
    // TODO
    
    @Override
    public int hashCode() {
        return (int) id;
    }
    
    public boolean equals(final @NotNull MTAUser user) {
        return id == user.id;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj != null
                && (obj == this || getClass() == obj.getClass()
                && equals((MTAUser) obj));
    }
    
    public final void prepareUpdate(final @NotNull MTALineStatus status) {
        pendingStatus = status;
    }
    
    public final void update() throws IOException {
        if (pendingStatus != null) {
            update(pendingStatus);
        }
    }
    
    @Override
    public final void update(final @NotNull MTALineStatus status) throws IOException {
        
        // TODO
    }
    
    @Override
    public final @NotNull StringBuilder append(final @NotNull StringBuilder sb) {
        sb.append("id = ").append(id);
        // TODO
        return sb;
    }
    
    @Override
    public final @NotNull String toString() {
        return defaultToString();
    }
    
}