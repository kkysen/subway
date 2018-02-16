package sen.khyber.unsafe.reflect.deprecated;

import sen.khyber.unsafe.reflect.ReflectedField;
import sen.khyber.util.exceptions.ExceptionUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by Khyber Sen on 1/30/2018.
 *
 * @param <T> Field, Method, or Constructor (all Members)
 * @author Khyber Sen
 */
@Deprecated
public abstract class OldReflector<T extends AccessibleObject & Member> {
    
    protected static final Lookup LOOKUP = MethodHandles.lookup();
    
    /**
     * Created by Khyber Sen on 1/30/2018.
     *
     * @author Khyber Sen
     */
    protected enum MemberType {
        
        Field(),
        Method(),
        Constructor();
        
        public String methodName() {
            return "getDeclared" + name() + "s0";
        }
        
    }
    
    private final MethodHandle reflector;
    
    // TODO cache MethodHandles
    
    private final Map<Class<?>, Entry<Map<String, T>, T[]>> cache =
            new HashMap<>();
    
    private static final ReflectedField accessibleObjectOverrideField;
    
    static {
        final Field field;
        try {
            // LeakyReflector not initialized yet, so must use normal reflection
            field = AccessibleObject.class.getDeclaredField("override");
        } catch (final NoSuchFieldException e) {
            throw ExceptionUtils.atRuntime(e);
        }
        accessibleObjectOverrideField = new ReflectedField(field);
    }
    
    protected OldReflector(final MemberType memberType) {
        final Method method;
        try {
            method = Class.class.getDeclaredMethod(memberType.methodName(), boolean.class);
        } catch (final NoSuchMethodException e) {
            throw ExceptionUtils.atRuntime(e);
        }
        method.setAccessible(true);
        try {
            reflector = MethodHandles.lookup().unreflect(method);
        } catch (final IllegalAccessException e) {
            throw ExceptionUtils.atRuntime(e);
        }
    }
    
    public static final void setAccessible(final AccessibleObject accessibleObject,
            final boolean accessible) {
        accessibleObjectOverrideField.bind(accessibleObject).setBoolean(accessible);
    }
    
    public static final void setAccessible(final AccessibleObject accessibleObject) {
        setAccessible(accessibleObject, true);
    }
    
    T[] getRawMembers(final Class<?> klass) {
        try {
            @SuppressWarnings("unchecked") final T[] members = (T[]) reflector.invoke(klass, false);
            for (final T member : members) {
                setAccessible(member);
            }
            return members;
            /*
             * invokeExact requires return type to match exactly
             * so generic arrays can't be used
             * e.x. Member[] != Field[] according to invokeExact
             * invokeExact can be supported using a switch statement,
             * but performance is the same
             */
        } catch (final Throwable e) {
            throw ExceptionUtils.atRuntime(e);
        }
    }
    
    private void addMemberNames(final Entry<Map<String, T>, T[]> classMembers,
            final Class<?> klass) {
        final T[] members = getRawMembers(klass);
        final Map<String, T> fieldNames = classMembers.getKey();
        for (final T member : members) {
            fieldNames.put(member.getName(), member);
        }
        classMembers.setValue(members);
    }
    
    public T[] getDeclaredMembers(final Class<?> klass) {
        Entry<Map<String, T>, T[]> classMembers = cache.get(klass);
        if (classMembers == null) {
            classMembers = new SimpleEntry<>(new HashMap<>(), null);
            cache.put(klass, classMembers);
        }
        if (classMembers.getValue() == null) {
            addMemberNames(classMembers, klass);
        }
        return classMembers.getValue();
    }
    
    public Map<String, T> getDeclaredMembersMap(final Class<?> klass) {
        getDeclaredMembers(klass);
        return cache.get(klass).getKey();
    }
    
    public T getDeclaredMember(final Class<?> klass, final String name) {
        final Entry<Map<String, T>, T[]> classMembers = cache.get(klass);
        if (classMembers == null) {
            getDeclaredMembers(klass);
        }
        return (classMembers == null ? cache.get(klass) : classMembers).getKey().get(name);
    }
    
    protected abstract MethodHandle convertToHandle(T member) throws IllegalAccessException;
    
    public MethodHandle getDeclaredHandle(final Class<?> klass, final String name) {
        try {
            final T member = getDeclaredMember(klass, name);
            // setAccessible already run
            return convertToHandle(member);
        } catch (final IllegalAccessException e) {
            throw ExceptionUtils.atRuntime(e); // shouldn't happen
        }
    }
    
}
