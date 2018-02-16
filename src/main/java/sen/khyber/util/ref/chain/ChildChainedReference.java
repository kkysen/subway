package sen.khyber.util.ref.chain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Khyber Sen on 2/15/2018.
 *
 * @author Khyber Sen
 */
public class ChildChainedReference<T> extends AbstractChainedReference<T> {
    
    private final @NotNull ChainedReference<T> parent;
    
    ChildChainedReference(final @NotNull ChainedReference<T> parent) {
        super(0);
        this.parent = parent;
    }
    
    @Override
    public final int depth() {
        return parent.depth() + 1;
    }
    
    @Override
    public final @NotNull ChainedReference<T> parent() {
        return parent;
    }
    
    @Override
    public final @Nullable T value() {
        return parent.value();
    }
    
    @Override
    public final ChainedReference<T> acquire() {
        count++;
        parent.acquire();
        return this;
    }
    
    @Override
    public final @Nullable T release() {
        if (count > 0) {
            count--;
            return parent.release();
        }
        return null;
    }
    
    @Override
    public final boolean isCompletelyFree() {
        return parent.isCompletelyFree();
    }
    
    @Override
    final void appendToString(final StringBuilder sb) {
        sb.append(", ");
        sb.append(count);
    }
    
}