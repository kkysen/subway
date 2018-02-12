package sen.khyber.unsafe.reflect;

import sen.khyber.util.exceptions.ExceptionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 1/30/2018.
 *
 * @author Khyber Sen
 */
public final class Reflectors {
    
    private Reflectors() {}
    
    @NotNull
    private static Class<?> classForName(final @NotNull String className) {
        Objects.requireNonNull(className);
        try {
            return Class.forName(className);
        } catch (final ClassNotFoundException e) {
            throw ExceptionUtils.atRuntime(e);
        }
    }
    
    private static final Map<Class<?>, ReflectedClass> reflectedClasses = new HashMap<>();
    
    @NotNull
    public static final ReflectedClass forClass(final @NotNull Class<?> klass) {
        Objects.requireNonNull(klass);
        return reflectedClasses.computeIfAbsent(klass, ReflectedClass::new);
    }
    
    @NotNull
    public static final ReflectedClass forClassName(final @NotNull String className) {
        return forClass(classForName(className));
    }
    
    public static final void unCacheClass(final @NotNull Class<?> klass) {
        Objects.requireNonNull(klass);
        ReflectedClass reflectedClass = reflectedClasses.remove(klass);
        if (reflectedClass != null) {
            reflectedClass.clear();
        }
    }
    
    public static final void unCacheClass(final @NotNull String className) {
        unCacheClass(classForName(className));
    }
    
}
