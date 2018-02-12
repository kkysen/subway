package sen.khyber.unsafe.reflect.deprecated;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

/**
 * Created by Khyber Sen on 1/30/2018.
 *
 * @author Khyber Sen
 */
@Deprecated
public final class OldMethodReflector extends OldReflector<Method> {
    
    OldMethodReflector() {
        super(MemberType.Method);
    }
    
    @Override
    protected MethodHandle convertToHandle(final Method method) throws IllegalAccessException {
        return LOOKUP.unreflect(method);
    }
    
}
