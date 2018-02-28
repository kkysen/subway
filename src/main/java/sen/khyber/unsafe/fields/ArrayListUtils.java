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
            arrayListClass.fieldUnchecked("elementData");
    
    private static final @NotNull ReflectedField sizeField = arrayListClass.fieldUnchecked("size");
    
    @SuppressWarnings("CollectionDeclaredAsConcreteClass")
    public static final <E> ArrayList<E> wrap(final E[] a) {
        Objects.requireNonNull(a);
        final ArrayList<E> list = arrayListClass.allocateInstance();
        elementDataField.setObject(list, a);
        sizeField.setInt(list, a.length);
        return list;
    }
    
    public static final <E> E[] getArray(final @NotNull ArrayList<E> arrayList) {
        Objects.requireNonNull(arrayList);
        return (E[]) elementDataField.getObject(arrayList);
    }
    
    // TODO
    
}