package sen.khyber.unsafe.reflect.deprecated;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;

/**
 * Created by Khyber Sen on 1/30/2018.
 *
 * @author Khyber Sen
 */
@Deprecated
public final class OldFieldReflector extends OldReflector<Field> {
    
    OldFieldReflector() {
        super(MemberType.Field);
    }
    
    @Override
    protected MethodHandle convertToHandle(final Field field) throws IllegalAccessException {
        return LOOKUP.unreflectGetter(field);
    }
    
}
