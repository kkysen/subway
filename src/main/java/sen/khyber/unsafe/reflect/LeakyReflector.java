package sen.khyber.unsafe.reflect;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.validation.constraints.Null;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Khyber Sen on 1/30/2018.
 *
 * @author Khyber Sen
 */
@Deprecated
public final class LeakyReflector {
    
    public static @NotNull Optional<Class<?>> classForName(final @NotNull String className) {
        Objects.requireNonNull(className);
        try {
            return Optional.of(Class.forName(className));
        } catch (final ClassNotFoundException ignored) {
            return Optional.empty();
        }
    }
    
    private LeakyReflector() {}
    
    private static final LeakyReflector REFLECTOR = new LeakyReflector();
    
    public static LeakyReflector get() {
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
    
    private static @Null @Nullable ReflectedClass<?> removeAndClearReflectedClass(
            final @NotNull Class<?> klass,
            final @NotNull ReflectedClass<?> reflectedClass) {
        reflectedClass.clearUnsafe();
        return null;
    }
    
    public final void clearClassCacheForClass(final @NotNull Class<?> klass) {
        Objects.requireNonNull(klass);
        reflectedClasses.computeIfPresent(klass, LeakyReflector::removeAndClearReflectedClass);
    }
    
    public final void clearClassCacheForClassName(final @NotNull String className) {
        classForName(className).ifPresent(this::clearClassCacheForClass);
    }
    
    public final void clearClassCache() {
        reflectedClasses.values().forEach(ReflectedClass::clearUnsafe);
        reflectedClasses.clear();
    }
    
}
