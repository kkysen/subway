package sen.khyber.unsafe.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Executable;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/13/2018.
 *
 * @author Khyber Sen
 */
public abstract class ReflectedFunction<T extends Executable>
        extends ReflectedMember<T, FunctionSignature, MethodHandle> {
    
    private final @NotNull T function;
    
    ReflectedFunction(final @NotNull T function) {
        super(function);
        this.function = function;
    }
    
    public final @NotNull T function() {
        return function;
    }
    
    @Override
    protected final @NotNull FunctionSignature getSignature(final @NotNull T function) {
        return FunctionSignature.ofFunction(function);
    }
    
    @SuppressWarnings("AbstractMethodOverridesAbstractMethod")
    @Override
    protected abstract @NotNull MethodHandle convertToHandle() throws IllegalAccessException;
    
}