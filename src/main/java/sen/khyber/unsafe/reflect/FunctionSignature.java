package sen.khyber.unsafe.reflect;

import sen.khyber.util.collections.immutable.ImmutableArrayList;
import sen.khyber.util.collections.immutable.ImmutableList;

import java.lang.reflect.Executable;
import java.util.Arrays;
import java.util.StringJoiner;

/**
 * Created by Khyber Sen on 2/13/2018.
 *
 * @author Khyber Sen
 */
public final class FunctionSignature {
    
    private final String name;
    private final Class<?>[] parameterTypes;
    private final ImmutableList<Class<?>> parameterTypeList;
    
    private int hash;
    
    public FunctionSignature(final Executable function) {
        name = function.getName();
        parameterTypes = function.getParameterTypes();
        parameterTypeList = new ImmutableArrayList<>(parameterTypes);
    }
    
    public FunctionSignature(final String name, final Class<?>... parameterTypes) {
        this.name = name;
        this.parameterTypes = parameterTypes.clone();
        parameterTypeList = new ImmutableArrayList<>(parameterTypes);
    }
    
    public final String name() {
        return name;
    }
    
    public final ImmutableList<Class<?>> parameterTypes() {
        return parameterTypeList;
    }
    
    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = name.hashCode() * 31 + Arrays.hashCode(parameterTypes);
        }
        return hash;
    }
    
    public boolean equals(final FunctionSignature signature) {
        return (hash == 0 || signature.hash == 0 || hash == signature.hash)
                && Arrays.equals(parameterTypes, signature.parameterTypes)
                && name.equals(signature.name);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null &&
                (o == this ||
                        (o.getClass() == getClass() && equals((FunctionSignature) o)));
    }
    
    @Override
    public final String toString() {
        final StringJoiner sj = new StringJoiner(", ", name + '(', ")");
        for (final Class<?> parameterType : parameterTypes) {
            sj.add(ClassNames.classToName(parameterType));
        }
        return sj.toString();
    }
    
}