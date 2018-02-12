package sen.khyber.unsafe.reflect;

import sen.khyber.util.immutable.ImmutableList;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Khyber Sen on 2/11/2018.
 *
 * @author Khyber Sen
 */
public final class ReflectedConstructors
        extends ReflectedMembers<Constructor<?>, MethodHandle> {
    
    public ReflectedConstructors(final @NotNull Class<?> klass) {
        super(klass, MemberType.CONSTRUCTOR);
    }
    
    @NotNull
    @Override
    final ReflectedConstructor reflectMember(final @NotNull Constructor<?> constructor) {
        return new ReflectedConstructor(constructor);
    }
    
    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public final ImmutableList<ReflectedConstructor> members() {
        return (ImmutableList<ReflectedConstructor>) super.members();
    }
    
    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public final Map<String, ReflectedConstructor> membersMap() {
        return (Map<String, ReflectedConstructor>) super.membersMap();
    }
    
    @Nullable
    @Override
    public final ReflectedConstructor member(final @NotNull String name) {
        return (ReflectedConstructor) super.member(name);
    }
    
}