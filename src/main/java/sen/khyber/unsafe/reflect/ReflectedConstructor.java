package sen.khyber.unsafe.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/11/2018.
 *
 * @author Khyber Sen
 */
public final class ReflectedConstructor<T> extends ReflectedFunction<Constructor<T>> {
    
    private final @NotNull Constructor<T> constructor;
    
    public ReflectedConstructor(final @NotNull Constructor<T> constructor) {
        super(constructor);
        this.constructor = constructor;
    }
    
    public @NotNull Constructor<T> constructor() {
        return constructor;
    }
    
    @Override
    protected final @NotNull MethodHandle convertToHandle() throws IllegalAccessException {
        return LOOKUP.unreflectConstructor(constructor);
    }
    
}