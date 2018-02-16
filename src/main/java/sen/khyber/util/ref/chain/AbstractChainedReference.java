package sen.khyber.util.ref.chain;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/16/2018.
 *
 * @author Khyber Sen
 */
public abstract class AbstractChainedReference<T> implements ChainedReference<T> {
    
    int count;
    
    AbstractChainedReference(final int count) {
        this.count = count;
    }
    
    @Override
    public final int count() {
        return count;
    }
    
    @Override
    public final boolean exists() {
        return !isFree();
    }
    
    @Override
    public final boolean isFree() {
        return count == 0;
    }
    
    @Override
    public final @NotNull ChainedReference<T> chain() {
        return new ChildChainedReference<>(this);
    }
    
    abstract void appendToString(StringBuilder sb);
    
    @Override
    public final @NotNull String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ChainedReference[");
        appendToString(sb);
        sb.append(']');
        return sb.toString();
    }
    
}