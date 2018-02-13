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
    
    @Override
    final @NotNull ReflectedConstructor<T> reflectMember(
            final @NotNull Constructor<T> constructor) {
        return new ReflectedConstructor<>(constructor);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public final @NotNull ImmutableList<ReflectedConstructor<T>> members() {
        return (ImmutableList<ReflectedConstructor<T>>) super.members();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public final @NotNull Map<String, ReflectedConstructor<T>> membersMap() {
        return (Map<String, ReflectedConstructor<T>>) super.membersMap();
    }
    
    @Override
    public final @Nullable ReflectedConstructor<T> member(final @NotNull String name) {
        return (ReflectedConstructor<T>) super.member(name);
    }
    
}