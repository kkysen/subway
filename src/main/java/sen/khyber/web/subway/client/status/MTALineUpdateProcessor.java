package sen.khyber.web.subway.client.status;

import sen.khyber.util.ObjectUtils;

import java.io.IOException;
import java.nio.file.Path;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/25/2018.
 *
 * @author Khyber Sen
 */
public final class MTALineUpdateProcessor implements MTAUpdateProcessor<IOException> {
    
    private final @NotNull MTALine<?> line;
    private final @NotNull MTALineHistory history;
    
    private final @NotNull MTAUsers users;
    
    public MTALineUpdateProcessor(final @NotNull MTALine<?> line, final @NotNull Path dir)
            throws IOException {
        ObjectUtils.requireNonNull(line, dir);
        this.line = line;
        history = new MTALineHistory(line, dir);
        users = new MTAUsers(line);
    }
    
    public final @NotNull MTAUser[] users() {
        return users.users();
    }
    
    public final void update(final @NotNull MTAUserUpdate userUpdate) {
        users.update(userUpdate);
    }
    
    @Override
    public final void update(final @NotNull MTALineStatus status) throws IOException {
        history.update(status);
        users.update(status);
    }
    
}