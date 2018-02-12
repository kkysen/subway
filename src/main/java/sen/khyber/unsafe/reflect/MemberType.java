package sen.khyber.unsafe.reflect;

import sen.khyber.util.exceptions.ExceptionUtils;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;


/**
 * Created by Khyber Sen on 2/11/2018.
 *
 * @author Khyber Sen
 */
@Accessors(fluent = true)
public enum MemberType {
    
    FIELD(Field.class),
    METHOD(Method.class),
    CONSTRUCTOR(Constructor.class),;
    
    private final Class<?> type;
    private final @Getter String typeName;
    private final MethodHandle reflector;
    
    private <T extends AccessibleObject & Member> MemberType(final Class<T> type) {
        this.type = type;
        typeName = type.getSimpleName();
        final String methodName = "getDeclared" + typeName + "s0";
        final Method method;
        try {
            method = Class.class.getDeclaredMethod(methodName, boolean.class);
        } catch (final NoSuchMethodException e) {
            throw ExceptionUtils.atRuntime(e);
        }
        reflector = new ReflectedMethod(method).handle();
    }
    
    @SuppressWarnings("unchecked")
    public <T extends AccessibleObject & Member> Class<T> type() {
        return (Class<T>) type;
    }
    
    @SuppressWarnings("unchecked")
    final <T extends AccessibleObject & Member> T[] rawMembers(final Class<?> klass) {
        try {
            /*
             * invokeExact requires return type to match exactly
             * so generic arrays can't be used
             * e.x. Member[] != Field[] according to invokeExact
             * invokeExact can be supported using a switch statement,
             * but performance is the same
             */
            return (T[]) reflector.invoke(klass, false);
        } catch (final Throwable e) {
            throw ExceptionUtils.atRuntime(e);
        }
    }
    
}