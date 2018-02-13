package sen.khyber.unsafe.reflect;

import sen.khyber.util.immutable.ImmutableList;

import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Khyber Sen on 2/11/2018.
 *
 * @author Khyber Sen
 */
public final class ReflectedFields extends ReflectedMembers<Field, VarHandle> {
    
    public ReflectedFields(final @NotNull Class<?> klass) {
        super(klass, MemberType.FIELD);
    }
    
    @Override
    final @NotNull ReflectedField reflectMember(final Field field) {
        return new ReflectedField(field);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public final @NotNull ImmutableList<ReflectedField> members() {
        return (ImmutableList<ReflectedField>) super.members();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public final @NotNull Map<String, ReflectedField> membersMap() {
        return (Map<String, ReflectedField>) super.membersMap();
    }
    
    @Override
    public final @Nullable ReflectedField member(final @NotNull String name) {
        return (ReflectedField) super.member(name);
    }
    
}