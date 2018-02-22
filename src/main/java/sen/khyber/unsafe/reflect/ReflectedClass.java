package sen.khyber.unsafe.reflect;

import sen.khyber.unsafe.UnsafeUtils;
import sen.khyber.util.Lazy;
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
    
    // lazily load
    private final @NotNull Lazy<ReflectedFields> fields;
    private final @NotNull Lazy<ReflectedMethods> methods;
    private final @NotNull Lazy<ReflectedConstructors<T>> constructors;
    
    ReflectedClass(final @NotNull Class<T> klass) {
        Objects.requireNonNull(klass);
        this.klass = klass;
        fields = new Lazy<>(() -> new ReflectedFields(klass));
        methods = new Lazy<>(() -> new ReflectedMethods(klass));
        constructors = new Lazy<>(() -> new ReflectedConstructors<>(klass));
    }
    
    public final @NotNull Class<T> klass() {
        return klass;
    }
    
    public final @NotNull ImmutableList<ReflectedField> fields() {
        return fields.get().members();
    }
    
    public final @NotNull Map<String, ReflectedField> fieldMap() {
        return fields.get().membersMap();
    }
    
    public final @Nullable ReflectedField field(final @NotNull String name) {
        return fields.get().member(name);
    }
    
    public final @NotNull Field[] rawFields() {
        return fields.get().rawMembers();
    }
    
    public final @Nullable Field rawField(final @NotNull String name) {
        return fields.get().rawMember(name);
    }
    
    public final boolean hasField(final @NotNull String name) {
        return fields.get().hasMember(name);
    }
    
    public final @NotNull ImmutableList<ReflectedMethod> methods() {
        return methods.get().members();
    }
    
    public final @NotNull Map<FunctionSignature, ReflectedMethod> methodMap() {
        return methods.get().membersMap();
    }
    
    public final @Nullable ReflectedMethod method(final @NotNull FunctionSignature signature) {
        return methods.get().member(signature);
    }
    
    public final @NotNull Method[] rawMethods() {
        return methods.get().rawMembers();
    }
    
    public final @Nullable Method rawMethod(final @NotNull FunctionSignature signature) {
        return methods.get().rawMember(signature);
    }
    
    public final boolean hasMethod(final @NotNull FunctionSignature signature) {
        return methods.get().hasMember(signature);
    }
    
    public final @Nullable ReflectedMethod method(final @NotNull String name,
            final @NotNull Class<?>... parameterTypes) {
        return method(FunctionSignature.forMethod(name, parameterTypes));
    }
    
    public final @Nullable Method rawMethod(final @NotNull String name,
            final @NotNull Class<?>... parameterTypes) {
        return rawMethod(FunctionSignature.forMethod(name, parameterTypes));
    }
    
    public final boolean hasMethod(final @NotNull String name,
            final @NotNull Class<?>... parameterTypes) {
        return hasMethod(FunctionSignature.forMethod(name, parameterTypes));
    }
    
    public final @NotNull ImmutableList<ReflectedConstructor<T>> constructors() {
        return constructors.get().members();
    }
    
    public final @NotNull Map<FunctionSignature, ReflectedConstructor<T>> constructorMap() {
        return constructors.get().membersMap();
    }
    
    public final @Nullable ReflectedConstructor<T> constructor(
            final @NotNull FunctionSignature signature) {
        return constructors.get().member(signature);
    }
    
    public final @NotNull Constructor<T>[] rawConstructors() {
        return constructors.get().rawMembers();
    }
    
    public final @Nullable Constructor<T> rawConstructor(
            final @NotNull FunctionSignature signature) {
        return constructors.get().rawMember(signature);
    }
    
    public final boolean hasConstructor(final @NotNull FunctionSignature signature) {
        return constructors.get().hasMember(signature);
    }
    
    public final @Nullable ReflectedConstructor<T> constructor(
            final @NotNull Class<?>... parameterTypes) {
        return constructor(FunctionSignature.forConstructor(parameterTypes));
    }
    
    public final @Nullable Constructor<T> rawConstructor(
            final @NotNull Class<?>... parameterTypes) {
        return rawConstructor(FunctionSignature.forConstructor(parameterTypes));
    }
    
    public final boolean hasConstructor(final @NotNull Class<?>... parameterTypes) {
        return hasConstructor(FunctionSignature.forConstructor(parameterTypes));
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
    
    public final ReflectedClass<T> ensureInitialized() {
        unsafe.ensureClassInitialized(klass);
        return this;
    }
    
    @SuppressWarnings("ConstantConditions")
    final void clearUnsafe() {
        final Lazy<ReflectedMembers<?, ?, ?>>[] allMembers =
                new Lazy[] {fields, methods, constructors};
        for (final Lazy<ReflectedMembers<?, ?, ?>> members : allMembers) {
            if (members.isInitialized()) {
                members.uncheckedGet().clear();
            }
        }
    }
    
    @Override
    public final String toString() {
        return "ReflectedClass[" + ClassNames.classToName(klass) + ']';
    }
    
}