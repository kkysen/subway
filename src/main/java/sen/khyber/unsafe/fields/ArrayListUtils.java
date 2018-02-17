package sen.khyber.unsafe.fields;

import sen.khyber.unsafe.reflect.ReflectedClass;
import sen.khyber.unsafe.reflect.ReflectedField;
import sen.khyber.unsafe.reflect.Reflectors;

import java.util.ArrayList;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/16/2018.
 *
 * @author Khyber Sen
 */
public final class ArrayListUtils {
    
    private ArrayListUtils() {}
    
    @SuppressWarnings("rawtypes")
    private static final @NotNull ReflectedClass<ArrayList> arrayListClass =
            Reflectors.main().get(ArrayList.class);
    
    private static final @NotNull ReflectedField elementDataField =
            Objects.requireNonNull(arrayListClass.field("elementData"));
    
    private static final @NotNull ReflectedField sizeField =
            Objects.requireNonNull(arrayListClass.field("size"));
    
    @SuppressWarnings("CollectionDeclaredAsConcreteClass")
    public static final <T> ArrayList<T> wrap(final T[] a) {
        Objects.requireNonNull(a);
        final ArrayList<T> list = arrayListClass.allocateInstance();
        elementDataField.bindUnsafe(list).setObject(a);
        elementDataField.bindUnsafe(null);
        sizeField.bindUnsafe(list).setInt(a.length);
        sizeField.bindUnsafe(null);
        return list;
    }
    
    // TODO
    
}