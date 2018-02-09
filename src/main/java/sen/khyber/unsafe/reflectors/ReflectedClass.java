package sen.khyber.unsafe.reflectors;

import lombok.NonNull;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Khyber Sen on 1/30/2018.
 *
 * @author Khyber Sen
 */
public final class ReflectedClass {
    
    static final FieldReflector FIELDS = new FieldReflector();
    static final MethodReflector METHODS = new MethodReflector();
    static final ConstructorReflector CONSTRUCTORS = new ConstructorReflector();
    
    private final Class<?> klass;
    
    ReflectedClass(final @NonNull Class<?> klass) {
        this.klass = klass;
    }
    
    public final Field[] fields() {
        return FIELDS.getDeclaredMembers(klass);
    }
    
    public final Field field(final String name) {
        return FIELDS.getDeclaredMember(klass, name);
    }
    
    public final ReflectedField reflectedField(final String name) {
        // TODO cache
        final Field field = field(name);
        if (field == null) {
            return null;
        }
        return new ReflectedField(field);
    }
    
    public final MethodHandle fieldHandle(final String name) {
        return FIELDS.getDeclaredHandle(klass, name);
    }
    
    public final Method[] methods() {
        return METHODS.getDeclaredMembers(klass);
    }
    
    public final Method method(final String name) {
        return METHODS.getDeclaredMember(klass, name);
    }
    
    public final MethodHandle methodHandle(final String name) {
        return METHODS.getDeclaredHandle(klass, name);
    }
    
    public final Constructor<?>[] constructors() {
        return CONSTRUCTORS.getDeclaredMembers(klass);
    }
    
    public final Constructor<?> constructor(final String name) {
        return CONSTRUCTORS.getDeclaredMember(klass, name);
    }
    
    public final MethodHandle constructorHandle(final String name) {
        return CONSTRUCTORS.getDeclaredHandle(klass, name);
    }
    
}