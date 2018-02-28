package sen.khyber.util.function;

import java.util.Objects;
import java.util.function.BooleanSupplier;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/1/2018.
 *
 * @author Khyber Sen
 */
@FunctionalInterface
public interface IntBinaryPredicate {
    
    public boolean applyAsInt(int left, int right);
    
    public default IntBinaryPredicate and(final @NotNull IntBinaryPredicate and) {
        Objects.requireNonNull(and);
        return (a, b) -> applyAsInt(a, b) && and.applyAsInt(a, b);
    }
    
    public default IntBinaryPredicate or(final @NotNull IntBinaryPredicate or) {
        Objects.requireNonNull(or);
        return (a, b) -> applyAsInt(a, b) || or.applyAsInt(a, b);
    }
    
    public static IntBinaryPredicate fromSupplier(final BooleanSupplier supplier) {
        return (a, b) -> supplier.getAsBoolean();
    }
    
}