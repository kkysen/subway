package sen.khyber.util;

import java.util.Objects;

/**
 * Created by Khyber Sen on 1/30/2018.
 *
 * @author Khyber Sen
 */
public final class ObjectUtils {
    
    private ObjectUtils() {}
    
    public static final void requireNonNull(final Object... objects) {
        for (final Object o : objects) {
            Objects.requireNonNull(o);
        }
    }
    
    // TODO
    
}