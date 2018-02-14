package sen.khyber.unsafe.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Executable;

/**
 * Created by Khyber Sen on 2/13/2018.
 *
 * @author Khyber Sen
 */
public abstract class ReflectedFunction<T extends Executable>
        extends ReflectedMember<T, FunctionSignature, MethodHandle> {
    
    private final T function;
    
    ReflectedFunction(final T function) {
        super(function);
        this.function = function;
    }
    
    public final T function() {
        return function;
    }
    
    @Override
    protected final FunctionSignature getSignature(final T function) {
        return FunctionSignature.ofFunction(function);
    }
    
}