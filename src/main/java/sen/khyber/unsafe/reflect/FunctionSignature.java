package sen.khyber.unsafe.reflect;

import sen.khyber.util.collections.immutable.ImmutableArrayList;
import sen.khyber.util.collections.immutable.ImmutableList;

import java.lang.reflect.Executable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Khyber Sen on 2/13/2018.
 *
 * @author Khyber Sen
 */
public final class FunctionSignature {
    
    public static final FunctionSignature ofFunction(final @NotNull Executable function) {
        return new FunctionSignature(function);
    }
    
    public static final FunctionSignature forMethod(final @Nullable String name,
            final @NotNull Class<?>... parameterTypes) {
        return new FunctionSignature(name, parameterTypes);
    }
    
    public static final FunctionSignature forConstructor(final @NotNull Class<?>... parameterTypes) {
        return forMethod(null, parameterTypes);
    }
    
    private final @Nullable String name;
    private final Class<?>[] parameterTypes;
    private final ImmutableList<Class<?>> parameterTypeList;
    
    private int hash;
    
    private FunctionSignature(final @NotNull Executable function) {
        name = function.getName();
        parameterTypes = function.getParameterTypes();
        parameterTypeList = new ImmutableArrayList<>(parameterTypes);
    }
    
    private FunctionSignature(final @Nullable String name,
            final @NotNull Class<?>... parameterTypes) {
        for (final Class<?> parameterType : parameterTypes) {
            Objects.requireNonNull(parameterType);
        }
        this.name = name;
        this.parameterTypes = parameterTypes.clone();
        parameterTypeList = new ImmutableArrayList<>(parameterTypes);
    }
    
    public final @Nullable String name() {
        return name;
    }
    
    public final ImmutableList<Class<?>> parameterTypes() {
        return parameterTypeList;
    }
    
    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = (name == null ? 0 : name.hashCode()) * 31 + Arrays.hashCode(parameterTypes);
        }
        return hash;
    }
    
    public boolean equals(final FunctionSignature signature) {
        return (hash == 0 || signature.hash == 0 || hash == signature.hash)
                && Arrays.equals(parameterTypes, signature.parameterTypes)
                && Objects.equals(name, signature.name);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null &&
                (o == this ||
                        (o.getClass() == getClass() && equals((FunctionSignature) o)));
    }
    
    @Override
    public final String toString() {
        final String name = this.name == null ? "constructor" : this.name;
        final StringJoiner sj = new StringJoiner(", ", name + '(', ")");
        for (final Class<?> parameterType : parameterTypes) {
            sj.add(ClassNames.classToName(parameterType));
        }
        return sj.toString();
    }
    
}