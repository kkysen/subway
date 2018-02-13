package sen.khyber.unsafe.reflect;

import sen.khyber.unsafe.UnsafeUtils;
import sen.khyber.util.collections.immutable.ImmutableList;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import sun.misc.Unsafe;

/**
 * Created by Khyber Sen on 2/11/2018.
 *
 * @author Khyber Sen
 */
@Accessors(fluent = true)
@Getter
public final class ReflectedClass<T> {
    
    private static final Unsafe unsafe = UnsafeUtils.getUnsafe();
    
    private final @NotNull Class<T> klass;
    private final @NotNull ReflectedFields fields;
    private final @NotNull ReflectedMethods methods;
    private final @NotNull ReflectedConstructors<T> constructors;
    
    ReflectedClass(final @NotNull Class<T> klass) {
        Objects.requireNonNull(klass);
        this.klass = klass;
        fields = new ReflectedFields(klass);
        methods = new ReflectedMethods(klass);
        constructors = new ReflectedConstructors<>(klass);
    }
    
    public final @NotNull ImmutableList<ReflectedField> fields() {
        return fields.members();
    }
    
    public final @NotNull Map<String, ReflectedField> fieldMap() {
        return fields.membersMap();
    }
    
    public final @Nullable ReflectedField field(final @NotNull String name) {
        return fields.member(name);
    }
    
    public final @NotNull Field[] rawFields() {
        return fields.rawMembers();
    }
    
    public final @Nullable Field rawField(final @NotNull String name) {
        return fields.rawMember(name);
    }
    
    public final boolean hasField(final @NotNull String name) {
        return fields.hasMember(name);
    }
    
    public final @NotNull ImmutableList<ReflectedMethod> methods() {
        return methods.members();
    }
    
    public final @NotNull Map<String, ReflectedMethod> methodMap() {
        return methods.membersMap();
    }
    
    public final @Nullable ReflectedMethod method(final @NotNull String name) {
        return methods.member(name);
    }
    
    public final @NotNull Method[] rawMethods() {
        return methods.rawMembers();
    }
    
    public final @Nullable Method rawMethod(final @NotNull String name) {
        return methods.rawMember(name);
    }
    
    public final boolean hasMethod(final @NotNull String name) {
        return methods.hasMember(name);
    }
    
    public final @NotNull ImmutableList<ReflectedConstructor<T>> constructors() {
        return constructors.members();
    }
    
    public final @NotNull Map<String, ReflectedConstructor<T>> constructorMap() {
        return constructors.membersMap();
    }
    
    public final @Nullable ReflectedConstructor<?> constructor(final @NotNull String name) {
        return constructors.member(name);
    }
    
    public final @NotNull Constructor<T>[] rawConstructors() {
        return constructors.rawMembers();
    }
    
    public final @Nullable Constructor<T> rawConstructor(final @NotNull String name) {
        return constructors.rawMember(name);
    }
    
    public final boolean hasConstructor(final @NotNull String name) {
        return constructors.hasMember(name);
    }
    
    @SuppressWarnings("unchecked")
    public final @NotNull T allocateInstance() {
        try {
            return (T) unsafe.allocateInstance(klass);
        } catch (final InstantiationException e) {
            unsafe.throwException(e);
            System.exit(1);
            return null;
        }
    }
    
    final void clear() {
        fields.clear();
        methods.clear();
        constructors.clear();
    }
    
    @Override
    public final String toString() {
        return "ReflectedClass[" + klass.getName() + ']';
    }
    
}