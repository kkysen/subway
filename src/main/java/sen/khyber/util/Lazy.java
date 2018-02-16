package sen.khyber.util;

import java.util.Objects;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Khyber Sen on 2/14/2018.
 *
 * @author Khyber Sen
 */
public final class Lazy<T> implements Supplier<T> {
    
    private final Object lock = new Object();
    
    private volatile @Nullable Supplier<T> delegate;
    private volatile boolean initialized;
    
    private @Nullable T value;
    
    public Lazy(final @NotNull Supplier<T> delegate) {
        Objects.requireNonNull(delegate);
        this.delegate = delegate;
    }
    
    public final boolean isInitialized() {
        return initialized;
    }
    
    public final @Nullable T uncheckedGet() {
        return value;
    }
    
    @Override
    public final T get() {
        if (!initialized) {
            synchronized (lock) {
                if (!initialized) {
                    //noinspection ConstantConditions
                    final T t = delegate.get();
                    value = t;
                    initialized = true;
                    // Release the delegate to GC.
                    delegate = null;
                    return t;
                }
            }
        }
        //noinspection ConstantConditions
        return value;
    }
    
    public final void free() {
        initialized = false;
        value = null;
    }
    
    public final T reload() {
        free();
        return get();
    }
    
    @Override
    public final String toString() {
        return "Lazy[" + (initialized ? ("value=" + value) : ("initialized=false")) + ']';
    }
    
}