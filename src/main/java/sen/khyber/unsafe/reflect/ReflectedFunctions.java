package sen.khyber.unsafe.reflect;

import sen.khyber.util.collections.immutable.ImmutableList;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Executable;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Khyber Sen on 2/13/2018.
 *
 * @author Khyber Sen
 */
public abstract class ReflectedFunctions<T extends Executable>
        extends ReflectedMembers<T, FunctionSignature, MethodHandle> {
    
    ReflectedFunctions(final @NotNull Class<?> klass, final @NotNull MemberType memberType) {
        super(klass, memberType);
        if (!Executable.class.isAssignableFrom(memberType.type())) {
            throw new IllegalStateException(memberType + " must represented an Executable");
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public @NotNull ImmutableList<? extends ReflectedFunction<T>> members() {
        return (ImmutableList<? extends ReflectedFunction<T>>) super.members();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public @NotNull Map<FunctionSignature, ? extends ReflectedFunction<T>> membersMap() {
        return (Map<FunctionSignature, ? extends ReflectedFunction<T>>) super.membersMap();
    }
    
    @Override
    public @Nullable ReflectedFunction<T> member(final @NotNull FunctionSignature signature) {
        return (ReflectedFunction<T>) super.member(signature);
    }
    
}