package sen.khyber.unsafe.reflect;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

/**
 * Created by Khyber Sen on 2/11/2018.
 *
 * @author Khyber Sen
 */
@Accessors(fluent = true)
@Getter
public final class ReflectedMethod extends ReflectedMember<Method, MethodHandle> {
    
    private final Method method;
    
    public ReflectedMethod(final Method method) {
        super(method);
        this.method = method;
    }
    
    @Override
    protected final MethodHandle convertToHandle() throws IllegalAccessException {
        return LOOKUP.unreflect(method);
    }
    
}