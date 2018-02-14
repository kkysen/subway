package sen.khyber.unsafe.reflect;

import sen.khyber.util.exceptions.ExceptionUtils;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

/**
 * Created by Khyber Sen on 2/9/2018.
 *
 * @author Khyber Sen
 */
public abstract class ReflectedMember<T extends AccessibleObject & Member, Signature, Handle> {
    
    static final Lookup LOOKUP = MethodHandles.lookup();
    
    private final T member;
    protected final Signature signature;
    protected final String name;
    protected final boolean isStatic;
    
    private Handle handle;
    
    ReflectedMember(final T member) {
        Accessor.setAccessible(member);
        this.member = member;
        signature = getSignature(member);
        name = member.getName();
        isStatic = Modifier.isStatic(member.getModifiers());
    }
    
    protected abstract Signature getSignature(T member);
    
    public final T member() {
        return member;
    }
    
    public final Signature signature() {
        return signature;
    }
    
    public final String name() {
        return name;
    }
    
    public final boolean isStatic() {
        return isStatic;
    }
    
    public final boolean isInstance() {
        return !isStatic;
    }
    
    protected abstract Handle convertToHandle() throws IllegalAccessException;
    
    public final Handle handle() {
        if (handle == null) {
            try {
                handle = convertToHandle();
            } catch (final IllegalAccessException e) {
                throw ExceptionUtils.atRuntime(e); // shouldn't happen
            }
        }
        return handle;
    }
    
    @Override
    public final String toString() {
        return getClass().getSimpleName() +
                '[' + ClassNames.classToName(member.getDeclaringClass()) + "::" + name + ']';
    }
    
}