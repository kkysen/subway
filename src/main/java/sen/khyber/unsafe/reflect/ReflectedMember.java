package sen.khyber.unsafe.reflect;

import sen.khyber.util.exceptions.ExceptionUtils;

import lombok.Getter;
import lombok.experimental.Accessors;

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
@Accessors(fluent = true)
@Getter
public abstract class ReflectedMember<T extends AccessibleObject & Member, Handle> {
    
    static final Lookup LOOKUP = MethodHandles.lookup();
    
    private final T member;
    protected final String name;
    protected final boolean isStatic;
    
    private Handle handle;
    
    ReflectedMember(final T member) {
        Accessor.setAccessible(member);
        this.member = member;
        name = member.getName();
        isStatic = Modifier.isStatic(member.getModifiers());
    }
    
    protected abstract Handle convertToHandle() throws IllegalAccessException;
    
    public Handle handle() {
        if (handle == null) {
            try {
                handle = convertToHandle();
            } catch (final IllegalAccessException e) {
                throw ExceptionUtils.atRuntime(e); // shouldn't happen
            }
        }
        return handle;
    }
    
}