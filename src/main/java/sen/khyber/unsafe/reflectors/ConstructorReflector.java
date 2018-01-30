package sen.khyber.unsafe.reflectors;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;

/**
 * Created by Khyber Sen on 1/30/2018.
 *
 * @author Khyber Sen
 */
public final class ConstructorReflector extends Reflector<Constructor<?>> {
    
    ConstructorReflector() {
        super(MemberType.Constructor);
    }
    
    @Override
    protected MethodHandle convertToHandle(final Constructor<?> constructor)
            throws IllegalAccessException {
        return LOOKUP.unreflectConstructor(constructor);
    }
    
}
