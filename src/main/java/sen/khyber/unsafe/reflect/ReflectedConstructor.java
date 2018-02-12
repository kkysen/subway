package sen.khyber.unsafe.reflect;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;

/**
 * Created by Khyber Sen on 2/11/2018.
 *
 * @author Khyber Sen
 */
@Accessors(fluent = true)
@Getter
public final class ReflectedConstructor extends ReflectedMember<Constructor<?>, MethodHandle> {
    
    private final Constructor<?> constructor;
    
    public ReflectedConstructor(final Constructor<?> constructor) {
        super(constructor);
        this.constructor = constructor;
    }
    
    @Override
    protected final MethodHandle convertToHandle() throws IllegalAccessException {
        return LOOKUP.unreflectConstructor(constructor);
    }
    
}