package sen.khyber.util.ref.shared;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Khyber Sen on 2/14/2018.
 *
 * @author Khyber Sen
 */
@Deprecated // not finished (ChainedReference is more updated)
public final class SharedReference<T> {
    
    private @Nullable T value;
    private int count;
    
    public SharedReference(final @NotNull T object) {
        Objects.requireNonNull(object);
        value = object;
        count = 1;
    }
    
    public final int count() {
        return count;
    }
    
    public final @Nullable T value() {
        return value;
    }
    
    public final SharedReference<T> acquire() {
        count++;
        return this;
    }
    
    public final void release() {
        count--;
        if (count == 0) {
            value = null;
        }
    }
    
    public final boolean isFree() {
        return count == 0;
    }
    
    @Override
    public final String toString() {
        return "SharedReference[" + String.valueOf(value) + " x " + count + ']';
    }
    
    public static final class Acquirer<K, V> {
        
        private final Function<K, V> constructor;
        
        public Acquirer(final @NotNull Function<K, V> constructor) {
            Objects.requireNonNull(constructor);
            this.constructor = constructor;
        }
        
        public final @NotNull SharedReference<V> acquire(
                final @NotNull K key, final @Nullable SharedReference<V> reference) {
            if (reference == null) {
                return new SharedReference<>(constructor.apply(key));
            }
            reference.acquire();
            return reference;
        }
        
    }
    
    public static final class Releaser<V> {
        
        private final @Nullable Consumer<V> destructor;
        
        public Releaser(@Nullable final Consumer<V> destructor) {
            this.destructor = destructor;
        }
        
        public Releaser() {
            this(null);
        }
        
        public final <K> @Nullable SharedReference<V> release(
                final @NotNull K key, final @NotNull SharedReference<V> reference) {
            if (destructor != null && reference.count == 1) {
                destructor.accept(reference.value);
            }
            reference.release();
            return reference.isFree() ? null : reference;
        }
        
    }
    
}