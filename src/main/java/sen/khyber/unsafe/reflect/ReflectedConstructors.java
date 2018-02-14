package sen.khyber.unsafe.reflect;

import sen.khyber.util.collections.immutable.ImmutableList;

import java.lang.reflect.Constructor;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Khyber Sen on 2/11/2018.
 *
 * @author Khyber Sen
 */
public final class ReflectedConstructors<T> extends ReflectedFunctions<Constructor<T>> {
    
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
    public final @NotNull Map<FunctionSignature, ReflectedConstructor<T>> membersMap() {
        return (Map<FunctionSignature, ReflectedConstructor<T>>) super.membersMap();
    }
    
    @Override
    public final @Nullable ReflectedConstructor<T> member(
            final @NotNull FunctionSignature signature) {
        return (ReflectedConstructor<T>) super.member(signature);
    }
    
}