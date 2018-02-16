package sen.khyber.unsafe.reflect;

import sen.khyber.util.exceptions.ExceptionUtils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

/**
 * Created by Khyber Sen on 2/11/2018.
 *
 * @author Khyber Sen
 */
public final class Accessor {
    
    private Accessor() {}
    
    private static final ReflectedField accessibleObjectOverrideField;
    
    static {
        final Field field;
        try {
            // LeakyReflector not initialized yet, so must use normal reflection
            field = AccessibleObject.class.getDeclaredField("override");
        } catch (final NoSuchFieldException e) {
            throw ExceptionUtils.atRuntime(e);
        }
        //        field.setAccessible(true);
        accessibleObjectOverrideField = new ReflectedField(field);
    }
    
    public static final void setAccessible(final AccessibleObject accessibleObject,
            final boolean accessible) {
        // must check when initializing self
        if (accessibleObjectOverrideField != null) {
            accessibleObjectOverrideField.bind(accessibleObject).setBoolean(accessible);
        }
    }
    
    public static final void setAccessible(final AccessibleObject accessibleObject) {
        setAccessible(accessibleObject, true);
    }
    
}