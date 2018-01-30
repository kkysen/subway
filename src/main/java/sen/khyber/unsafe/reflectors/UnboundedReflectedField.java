package sen.khyber.unsafe.reflectors;

import sen.khyber.unsafe.UnsafeUtils;

import lombok.NonNull;
import lombok.experimental.Accessors;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

/**
 * Created by Khyber Sen on 1/30/2018.
 *
 * @author Khyber Sen
 */
@Accessors(fluent = true)
public final class UnboundedReflectedField {
    
    // TODO add static() and instance(Object) classes
    
    private static final Unsafe unsafe = UnsafeUtils.getUnsafe();
    
    private final Field field;
    
    public UnboundedReflectedField(final @NonNull Field field) {
        this.field = field;
    }
    
    public ReflectedField bindStatic() {
        final ReflectedField reflectedField = new ReflectedField(field);
        if (!reflectedField.isStatic()) {
            return null;
        }
        return reflectedField;
    }
    
    public ReflectedField bindObject(final @NonNull Object object) {
        final ReflectedField reflectedField = new ReflectedField(field);
        if (reflectedField.isStatic()) {
            return null;
        }
        return reflectedField.bind(object);
    }
    
}