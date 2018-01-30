package sen.khyber.unsafe.reflectors;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

/**
 * Created by Khyber Sen on 1/30/2018.
 *
 * @author Khyber Sen
 */
public final class MethodReflector extends Reflector<Method> {
    
    MethodReflector() {
        super(MemberType.Method);
    }
    
    @Override
    protected MethodHandle convertToHandle(final Method method) throws IllegalAccessException {
        return LOOKUP.unreflect(method);
    }
    
}
