package sen.khyber.unsafe.reflectors;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;

/**
 * Created by Khyber Sen on 1/30/2018.
 *
 * @author Khyber Sen
 */
public final class FieldReflector extends Reflector<Field> {
    
    FieldReflector() {
        super(MemberType.Field);
    }
    
    @Override
    protected MethodHandle convertToHandle(final Field field) throws IllegalAccessException {
        return LOOKUP.unreflectGetter(field);
    }
    
}
