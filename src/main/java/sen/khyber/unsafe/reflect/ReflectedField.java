package sen.khyber.unsafe.reflect;

import sen.khyber.unsafe.UnsafeUtils;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;

import org.jetbrains.annotations.Nullable;

import sun.misc.Unsafe;

/**
 * Created by Khyber Sen on 1/30/2018.
 *
 * @author Khyber Sen
 */
@Accessors(fluent = true)
@Getter
public final class ReflectedField extends ReflectedMember<Field, VarHandle> {
    
    private static final Unsafe unsafe = UnsafeUtils.getUnsafe();
    
    private final Field field;
    private @Nullable Object object;
    private final long offset;
    
    public ReflectedField(final Field field) {
        super(field);
        this.field = field;
        object = isStatic ? field.getDeclaringClass() : null;
        offset = isStatic ? unsafe.staticFieldOffset(field) : unsafe.objectFieldOffset(field);
    }
    
    @Override
    protected final VarHandle convertToHandle() throws IllegalAccessException {
        return LOOKUP.unreflectVarHandle(field);
    }
    
    public final ReflectedField bindUnsafe(final Object newObject) {
        object = newObject;
        return this;
    }
    
    public final ReflectedField bind(final Object newObject) {
        if (isStatic) {
            return this;
        }
        if (!field.getDeclaringClass().isInstance(newObject)) {
            throw new IllegalArgumentException(
                    newObject + " is not an instance of the declaring class of " + field);
        }
        return bindUnsafe(newObject);
    }
    
    // TODO finish for all types
    
    public final Object getObject() {
        return unsafe.getObject(object, offset);
    }
    
    public final void setObject(final @Nullable Object value) {
        unsafe.putObject(object, offset, value);
    }
    
    public final Object setGetObject(final @Nullable Object value) {
        return unsafe.getAndSetObject(object, offset, value);
    }
    
    public final void setToNull() {
        setObject(null);
    }
    
    public final Object setGetToNull() {
        return setGetObject(null);
    }
    
    public final int getInt() {
        return unsafe.getInt(object, offset);
    }
    
    public final int getIntVolatile() {
        return unsafe.getIntVolatile(object, offset);
    }
    
    public final void setInt(final int value) {
        unsafe.putInt(object, offset, value);
    }
    
    public final int getSetInt(final int value) {
        return unsafe.getAndSetInt(object, offset, value);
    }
    
    public final long getLong() {
        return unsafe.getLong(object, offset);
    }
    
    public final long getLongVolatile() {
        return unsafe.getLongVolatile(object, offset);
    }
    
    public final void setLong(final long value) {
        unsafe.putLong(object, offset, value);
    }
    
    public final long getSetLong(final long value) {
        return unsafe.getAndSetLong(object, offset, value);
    }
    
    public final boolean getBoolean() {
        return unsafe.getBoolean(object, offset);
    }
    
    public final boolean getBooleanVolatile() {
        return unsafe.getBooleanVolatile(object, offset);
    }
    
    public final void setBoolean(final boolean value) {
        unsafe.putBoolean(object, offset, value);
    }
    
    public final boolean getSetBoolean(final boolean value) {
        final boolean old = getBoolean();
        setBoolean(value);
        return old;
    }
    
}