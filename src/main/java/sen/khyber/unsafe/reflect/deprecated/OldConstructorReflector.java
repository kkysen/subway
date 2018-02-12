package sen.khyber.unsafe.reflect.deprecated;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;

/**
 * Created by Khyber Sen on 1/30/2018.
 *
 * @author Khyber Sen
 */
@Deprecated
public final class OldConstructorReflector extends OldReflector<Constructor<?>> {
    
    OldConstructorReflector() {
        super(MemberType.Constructor);
    }
    
    @Override
    protected MethodHandle convertToHandle(final Constructor<?> constructor)
            throws IllegalAccessException {
        return LOOKUP.unreflectConstructor(constructor);
    }
    
}
