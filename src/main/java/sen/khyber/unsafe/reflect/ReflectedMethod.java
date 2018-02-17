package sen.khyber.unsafe.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/11/2018.
 *
 * @author Khyber Sen
 */
public final class ReflectedMethod extends ReflectedFunction<Method> {
    
    private final @NotNull Method method;
    
    public ReflectedMethod(final @NotNull Method method) {
        super(method);
        this.method = method;
    }
    
    public final @NotNull Method method() {
        return method;
    }
    
    @Override
    protected final @NotNull MethodHandle convertToHandle() throws IllegalAccessException {
        return LOOKUP.unreflect(method);
    }
    
}