package sen.khyber.util.function;

import java.util.function.BooleanSupplier;

/**
 * Created by Khyber Sen on 2/1/2018.
 *
 * @author Khyber Sen
 */
@FunctionalInterface
public interface IntBinaryPredicate {
    
    public boolean applyAsInt(int left, int right);
    
    public static IntBinaryPredicate fromSupplier(final BooleanSupplier supplier) {
        return (a, b) -> supplier.getAsBoolean();
    }
    
}