package sen.khyber.unsafe.reflect;

import sen.khyber.util.immutable.ImmutableList;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Khyber Sen on 2/11/2018.
 *
 * @author Khyber Sen
 */
@Accessors(fluent = true)
@Getter(onMethod = @__(@NotNull))
public final class ReflectedClass {
    
    private final @NotNull Class<?> klass;
    private final @NotNull ReflectedFields fields;
    private final @NotNull ReflectedMethods methods;
    private final @NotNull ReflectedConstructors constructors;
    
    ReflectedClass(final @NotNull Class<?> klass) {
        Objects.requireNonNull(klass);
        this.klass = klass;
        fields = new ReflectedFields(klass);
        methods = new ReflectedMethods(klass);
        constructors = new ReflectedConstructors(klass);
    }
    
    @NotNull
    public final ImmutableList<ReflectedField> fields() {
        return fields.members();
    }
    
    @NotNull
    public final Map<String, ReflectedField> fieldMap() {
        return fields.membersMap();
    }
    
    @Nullable
    public final ReflectedField field(final @NotNull String name) {
        return fields.member(name);
    }
    
    @NotNull
    public final Field[] rawFields() {
        return fields.rawMembers();
    }
    
    @Nullable
    public final Field rawField(final @NotNull String name) {
        return fields.rawMember(name);
    }
    
    public final boolean hasField(final @NotNull String name) {
        return fields.hasMember(name);
    }
    
    @NotNull
    public final ImmutableList<ReflectedMethod> methods() {
        return methods.members();
    }
    
    @NotNull
    public final Map<String, ReflectedMethod> methodMap() {
        return methods.membersMap();
    }
    
    @Nullable
    public final ReflectedMethod method(final @NotNull String name) {
        return methods.member(name);
    }
    
    @NotNull
    public final Method[] rawMethods() {
        return methods.rawMembers();
    }
    
    @Nullable
    public final Method rawMethod(final @NotNull String name) {
        return methods.rawMember(name);
    }
    
    public final boolean hasMethod(final @NotNull String name) {
        return methods.hasMember(name);
    }
    
    @NotNull
    public final ImmutableList<ReflectedConstructor> constructors() {
        return constructors.members();
    }
    
    @NotNull
    public final Map<String, ReflectedConstructor> constructorMap() {
        return constructors.membersMap();
    }
    
    @Nullable
    public final ReflectedConstructor constructor(final @NotNull String name) {
        return constructors.member(name);
    }
    
    @NotNull
    public final Constructor[] rawConstructors() {
        return constructors.rawMembers();
    }
    
    @Nullable
    public final Constructor rawConstructor(final @NotNull String name) {
        return constructors.rawMember(name);
    }
    
    public final boolean hasConstructor(final @NotNull String name) {
        return constructors.hasMember(name);
    }
    
}