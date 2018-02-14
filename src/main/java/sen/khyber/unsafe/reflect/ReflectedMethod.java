package sen.khyber.unsafe.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

/**
 * Created by Khyber Sen on 2/11/2018.
 *
 * @author Khyber Sen
 */
public final class ReflectedMethod extends ReflectedFunction<Method> {
    
    private final Method method;
    
    public ReflectedMethod(final Method method) {
        super(method);
        this.method = method;
    }
    
    public final Method method() {
        return method;
    }
    
    @Override
    protected final MethodHandle convertToHandle() throws IllegalAccessException {
        return LOOKUP.unreflect(method);
    }
    
}