package sen.khyber.unsafe.reflect;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.validation.constraints.Null;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 1/30/2018.
 *
 * @author Khyber Sen
 */
public final class Reflector {
    
    public static @NotNull Optional<Class<?>> classForName(final @NotNull String className) {
        Objects.requireNonNull(className);
        try {
            return Optional.of(Class.forName(className));
        } catch (final ClassNotFoundException e) {
            return Optional.empty();
        }
    }
    
    private Reflector() {}
    
    private static final Reflector REFLECTOR = new Reflector();
    
    public static Reflector get() {
        return REFLECTOR;
    }
    
    private final Map<Class<?>, ReflectedClass<?>> reflectedClasses = new HashMap<>();
    
    public final @NotNull ReflectedClass<?> forClass(final @NotNull Class<?> klass) {
        Objects.requireNonNull(klass);
        return reflectedClasses.computeIfAbsent(klass, ReflectedClass::new);
    }
    
    public final @NotNull Optional<ReflectedClass<?>> forClassName(
            final @NotNull String className) {
        return classForName(className).map(this::forClass);
    }
    
    public final @NotNull ReflectedClass<?> forClassNameUnchecked(final @NotNull String className) {
        //noinspection ConstantConditions
        return forClassName(className).get();
    }
    
    private static @Null ReflectedClass<?> removeAndClearReflectedClass(
            final @NotNull Class<?> klass,
            final @NotNull ReflectedClass reflectedClass) {
        reflectedClass.clear();
        return null;
    }
    
    public final void unCacheClass(final @NotNull Class<?> klass) {
        Objects.requireNonNull(klass);
        reflectedClasses.computeIfPresent(klass, Reflector::removeAndClearReflectedClass);
    }
    
    public final void unCacheClass(final @NotNull String className) {
        classForName(className).ifPresent(this::unCacheClass);
    }
    
    public final void unCacheAll() {
        reflectedClasses.values().forEach(ReflectedClass::clear);
        reflectedClasses.clear();
    }
    
}
