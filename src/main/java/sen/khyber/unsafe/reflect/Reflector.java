package sen.khyber.unsafe.reflect;

import sen.khyber.util.ref.chain.ChainedReference;
import sen.khyber.util.ref.chain.ChainedReference.Acquirer;
import sen.khyber.util.ref.chain.ChainedReference.Releaser;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import static java.util.Comparator.comparing;

/**
 * Created by Khyber Sen on 2/13/2018.
 *
 * @author Khyber Sen
 */
public final class Reflector {
    
    public static @NotNull Optional<Class<?>> classForName(final @NotNull String className) {
        Objects.requireNonNull(className);
        try {
            return Optional.of(Class.forName(className));
        } catch (final ClassNotFoundException ignored) {
            return Optional.empty();
        }
    }
    
    private static final Releaser<ReflectedClass<?>> releaser =
            new Releaser<>(ReflectedClass::clearUnsafe);
    
    private final int depth;
    private final Acquirer<Class<?>, ReflectedClass<?>> acquirer;
    private final Map<Class<?>, ChainedReference<ReflectedClass<?>>> classes;
    
    private Reflector(final int depth,
            final Map<Class<?>, ChainedReference<ReflectedClass<?>>> classes) {
        this.depth = depth;
        acquirer = new Acquirer<>(ReflectedClass::new, depth);
        this.classes = classes;
        classes.replaceAll(ChainedReference::chainRemapping);
    }
    
    Reflector() {
        this(1, new HashMap<>());
    }
    
    public final int depth() {
        return depth;
    }
    
    public final <T> @NotNull ReflectedClass<T> get(final @NotNull Class<T> klass,
            final boolean cache) {
        Objects.requireNonNull(klass);
        if (!cache) {
            return new ReflectedClass<>(klass);
        }
        //noinspection ConstantConditions
        return (ReflectedClass<T>) classes.compute(klass, acquirer::acquire).value();
    }
    
    public final @NotNull Optional<ReflectedClass<?>> get(final @NotNull String className,
            final boolean cache) {
        return classForName(className).map(klass -> get(klass, cache));
    }
    
    public final <T> @NotNull ReflectedClass<T> get(final @NotNull Class<T> klass) {
        return get(klass, true);
    }
    
    public final @NotNull Optional<ReflectedClass<?>> get(final @NotNull String className) {
        return get(className, true);
    }
    
    public final @NotNull ReflectedClass<?> getUnchecked(final @NotNull String className,
            final boolean cache) {
        //noinspection ConstantConditions
        return get(className, cache).get();
    }
    
    public final @NotNull ReflectedClass<?> getUnchecked(final @NotNull String className) {
        return getUnchecked(className, false);
    }
    
    public final void remove(final @NotNull Class<?> klass) {
        Objects.requireNonNull(klass);
        classes.computeIfPresent(klass, releaser::release);
    }
    
    public final void remove(final @NotNull String className) {
        classForName(className).ifPresent(this::remove);
    }
    
    public final boolean contains(final @NotNull Class<?> klass) {
        Objects.requireNonNull(klass);
        return classes.containsKey(klass);
    }
    
    public final boolean contains(final @NotNull String className) {
        return classForName(className).map(this::contains).orElse(false);
    }
    
    public final void clear() {
        classes.keySet().forEach(this::remove);
    }
    
    public final @NotNull Reflector spawnChild() {
        return new Reflector(depth + 1, classes);
    }
    
    @SuppressWarnings("FinalizeDeclaration")
    @Override
    protected final void finalize() {
        try {
            super.finalize();
            classes.values().forEach(ChainedReference::release);
        } catch (final Throwable e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public final @NotNull String toString() {
        final String indent = "\n    ";
        return classes
                .values()
                .stream()
                .filter(ChainedReference::exists)
                .map(ChainedReference::value)
                .filter(Objects::nonNull)
                .sorted(comparing(ReflectedClass::klass, comparing(Class::getName)))
                .map(ReflectedClass::toString)
                .collect(Collectors.joining(indent, '[' + indent, "\n]"));
    }
    
}