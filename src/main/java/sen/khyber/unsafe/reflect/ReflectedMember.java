package sen.khyber.unsafe.reflect;

import sen.khyber.util.exceptions.ExceptionUtils;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Khyber Sen on 2/9/2018.
 *
 * @author Khyber Sen
 */
public abstract class ReflectedMember<T extends AccessibleObject & Member, Signature, Handle> {
    
    static final @NotNull Lookup LOOKUP = MethodHandles.lookup();
    
    private final @NotNull T member;
    protected final @NotNull Signature signature;
    protected final @NotNull String name;
    protected final boolean isStatic;
    
    private @Nullable Handle handle;
    
    ReflectedMember(final @NotNull T member) {
        Accessor.setAccessible(member);
        this.member = member;
        signature = getSignature(member);
        name = member.getName();
        isStatic = Modifier.isStatic(member.getModifiers());
    }
    
    protected abstract @NotNull Signature getSignature(@NotNull T member);
    
    public final @NotNull T member() {
        return member;
    }
    
    public final @NotNull Signature signature() {
        return signature;
    }
    
    public final @NotNull String name() {
        return name;
    }
    
    public final boolean isStatic() {
        return isStatic;
    }
    
    public final boolean isInstance() {
        return !isStatic;
    }
    
    protected abstract @NotNull Handle convertToHandle() throws IllegalAccessException;
    
    public final @NotNull Handle handle() {
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
    public final @NotNull String toString() {
        return getClass().getSimpleName() +
                '[' + ClassNames.classToName(member.getDeclaringClass()) + "::" + signature + ']';
    }
    
}