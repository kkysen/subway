package sen.khyber.web.subway.client.status;

import sen.khyber.util.exceptions.ExceptionUtils;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/25/2018.
 *
 * @author Khyber Sen
 */
@FunctionalInterface
public interface MTAUpdateProcessor<Ex extends Exception> {
    
    public void update(final @NotNull MTALineStatus status) throws Ex;
    
    public default void updateQuietly(final @NotNull MTALineStatus status) {
        //noinspection OverlyBroadCatchBlock
        try {
            update(status);
        } catch (final Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw ExceptionUtils.atRuntime(e);
            }
        }
    }
    
}