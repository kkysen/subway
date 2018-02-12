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
public final class ReflectedConstructors<T>
        extends ReflectedMembers<Constructor<T>, MethodHandle> {
    
    public ReflectedConstructors(final @NotNull Class<T> klass) {
        super(klass, MemberType.CONSTRUCTOR);
    }
    
    @NotNull
    @Override
    final ReflectedConstructor<T> reflectMember(final @NotNull Constructor<T> constructor) {
        return new ReflectedConstructor<>(constructor);
    }
    
    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public final ImmutableList<ReflectedConstructor<T>> members() {
        return (ImmutableList<ReflectedConstructor<T>>) super.members();
    }
    
    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public final Map<String, ReflectedConstructor<T>> membersMap() {
        return (Map<String, ReflectedConstructor<T>>) super.membersMap();
    }
    
    @Nullable
    @Override
    public final ReflectedConstructor<T> member(final @NotNull String name) {
        return (ReflectedConstructor<T>) super.member(name);
    }
    
}