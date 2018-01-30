package sen.khyber.unsafe.reflectors;

import sen.khyber.util.exceptions.ExceptionUtils;

import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Khyber Sen on 1/30/2018.
 *
 * @author Khyber Sen
 */
public final class Reflectors {
    
    private Reflectors() {}
    
    static Class<?> classForName(final String className) {
        try {
            return Class.forName(className);
        } catch (final ClassNotFoundException e) {
            throw ExceptionUtils.atRuntime(e);
        }
    }
    
    private static final Map<Class<?>, ReflectedClass> reflectors = new HashMap<>();
    
    public static ReflectedClass forClass(final @NonNull Class<?> klass) {
        return reflectors.computeIfAbsent(klass, ReflectedClass::new);
    }
    
    public static ReflectedClass forClassName(final @NonNull String className) {
        return forClass(classForName(className));
    }
    
}
