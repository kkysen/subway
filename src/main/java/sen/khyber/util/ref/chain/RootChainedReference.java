package sen.khyber.util.ref.chain;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Khyber Sen on 2/16/2018.
 *
 * @author Khyber Sen
 */
public class RootChainedReference<T> extends AbstractChainedReference<T> {
    
    private @Nullable T value;
    
    RootChainedReference(final @NotNull T value) {
        super(1);
        Objects.requireNonNull(value);
        this.value = value;
    }
    
    @Override
    public final @Nullable ChainedReference<T> parent() {
        return null;
    }
    
    @Override
    public final int depth() {
        return 1;
    }
    
    @Override
    public final @Nullable T value() {
        return value;
    }
    
    @Override
    public final ChainedReference<T> acquire() {
        count++;
        return this;
    }
    
    @Override
    public final @Nullable T release() {
        final T t = value;
        if (count > 0) {
            count--;
            if (count == 0) {
                value = null;
            }
        }
        return t;
    }
    
    @Override
    public final boolean isCompletelyFree() {
        return isFree();
    }
    
    @Override
    final void appendToString(final StringBuilder sb) {
        sb.append(value);
        sb.append(", ");
        sb.append(count);
    }
    
}