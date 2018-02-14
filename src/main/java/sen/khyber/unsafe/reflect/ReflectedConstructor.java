package sen.khyber.unsafe.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;

/**
 * Created by Khyber Sen on 2/11/2018.
 *
 * @author Khyber Sen
 */
public final class ReflectedConstructor<T> extends ReflectedFunction<Constructor<T>> {
    
    private final Constructor<T> constructor;
    
    public ReflectedConstructor(final Constructor<T> constructor) {
        super(constructor);
        this.constructor = constructor;
    }
    
    public Constructor<T> constructor() {
        return constructor;
    }
    
    @Override
    protected final MethodHandle convertToHandle() throws IllegalAccessException {
        return LOOKUP.unreflectConstructor(constructor);
    }
    
}