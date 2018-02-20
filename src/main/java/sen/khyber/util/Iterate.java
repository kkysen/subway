package sen.khyber.util;

import java.util.Iterator;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/18/2018.
 *
 * @author Khyber Sen
 */
public final class Iterate {
    
    private Iterate() {}
    
    public static final <T> @NotNull Iterable<T> over(
            final @NotNull Supplier<Iterator<T>> iteratorSupplier) {
        return iteratorSupplier::get;
    }
    
}