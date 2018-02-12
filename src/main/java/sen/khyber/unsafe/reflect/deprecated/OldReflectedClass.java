package sen.khyber.unsafe.reflect.deprecated;

import sen.khyber.unsafe.reflect.ReflectedField;

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
@Deprecated
public final class OldReflectedClass {
    
    private static final OldFieldReflector FIELDS = new OldFieldReflector();
    private static final OldMethodReflector METHODS = new OldMethodReflector();
    private static final OldConstructorReflector CONSTRUCTORS = new OldConstructorReflector();
    
    private final Class<?> klass;
    
    OldReflectedClass(final @NonNull Class<?> klass) {
        this.klass = klass;
    }
    
    public final Field[] fields() {
        return FIELDS.getDeclaredMembers(klass);
    }
    
    public final Field field(final String name) {
        return FIELDS.getDeclaredMember(klass, name);
    }
    
    public final boolean hasField(final String name) {
        return field(name) != null;
    }
    
    public final ReflectedField reflectedField(final String name) {
        final Field field = field(name);
        if (field == null) {
            return null;
        }
        // don't cache to avoid any synchronization issues
        // this may be bound to an object, 
        // giving the ReflectedField mutable state that isn't thread safe
        // and shouldn't be for performance reasons
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