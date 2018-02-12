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
public final class ReflectedConstructor<T> extends ReflectedMember<Constructor<T>, MethodHandle> {
    
    private final Constructor<T> constructor;
    
    public ReflectedConstructor(final Constructor<T> constructor) {
        super(constructor);
        this.constructor = constructor;
    }
    
    @Override
    protected final MethodHandle convertToHandle() throws IllegalAccessException {
        return LOOKUP.unreflectConstructor(constructor);
    }
    
}