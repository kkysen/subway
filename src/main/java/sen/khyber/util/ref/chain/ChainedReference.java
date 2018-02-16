package sen.khyber.util.ref.chain;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Khyber Sen on 2/15/2018.
 *
 * @author Khyber Sen
 */
public interface ChainedReference<T> {
    
    private static void checkDepth(final int depth) {
        if (!(depth > 0)) {
            throw new IllegalArgumentException("depth must be positive");
        }
    }
    
    public static <T> ChainedReference<T> of(final @NotNull T value) {
        return new RootChainedReference<>(value);
    }
    
    public static <T> ChainedReference<T> of(final @NotNull T value, final int depth) {
        checkDepth(depth);
        ChainedReference<T> ref = of(value);
        for (int i = 1; i < depth; i++) {
            ref = ref.chain();
        }
        return ref;
    }
    
    public @Nullable ChainedReference<T> parent();
    
    public int count();
    
    public int depth();
    
    public @Nullable T value();
    
    public ChainedReference<T> acquire();
    
    public @Nullable T release();
    
    public boolean exists();
    
    public boolean isFree();
    
    public boolean isCompletelyFree();
    
    public @NotNull ChainedReference<T> chain();
    
    @Override
    public @NotNull String toString();
    
    public static final class Acquirer<K, V> {
        
        private final Function<K, V> constructor;
        private final int depth;
        
        public Acquirer(final @NotNull Function<K, V> constructor, final int depth) {
            Objects.requireNonNull(constructor);
            checkDepth(depth);
            this.constructor = constructor;
            this.depth = depth;
        }
        
        public final @NotNull ChainedReference<V> acquire(
                final @NotNull K key, final @Nullable ChainedReference<V> reference) {
            if (reference == null) {
                return of(constructor.apply(key), depth);
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
        
        public final <K> @Nullable ChainedReference<V> release(
                final @NotNull K key, final @NotNull ChainedReference<V> reference) {
            final V value = reference.release();
            final boolean completelyFree = reference.isCompletelyFree();
            if (destructor != null && completelyFree) {
                destructor.accept(value);
            }
            return completelyFree ? null : reference;
        }
        
    }
    
    public static <K, V> @Nullable ChainedReference<V> chainRemapping(final @Nullable K key,
            final @Nullable ChainedReference<V> ref) {
        return ref == null ? null : ref.chain();
    }
    
}