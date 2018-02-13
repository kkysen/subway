package sen.khyber.unsafe.reflect;

import sen.khyber.util.immutable.ImmutableList;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Khyber Sen on 2/11/2018.
 *
 * @author Khyber Sen
 */
public final class ReflectedMethods extends ReflectedMembers<Method, MethodHandle> {
    
    public ReflectedMethods(final @NotNull Class<?> klass) {
        super(klass, MemberType.METHOD);
    }
    
    @Override
    final @NotNull ReflectedMethod reflectMember(final @NotNull Method method) {
        return new ReflectedMethod(method);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public final @NotNull ImmutableList<ReflectedMethod> members() {
        return (ImmutableList<ReflectedMethod>) super.members();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public final @NotNull Map<String, ReflectedMethod> membersMap() {
        return (Map<String, ReflectedMethod>) super.membersMap();
    }
    
    @Override
    public final @Nullable ReflectedMethod member(final @NotNull String name) {
        return (ReflectedMethod) super.member(name);
    }
    
}