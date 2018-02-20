package sen.khyber.unsafe.fields;

import sen.khyber.unsafe.reflect.ReflectedClass;
import sen.khyber.unsafe.reflect.ReflectedField;
import sen.khyber.unsafe.reflect.Reflectors;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/18/2018.
 *
 * @author Khyber Sen
 */
public final class EnumUtils {
    
    private EnumUtils() {}
    
    private static final @NotNull ReflectedClass<Enum<?>> enumClass =
            (ReflectedClass<Enum<?>>) (ReflectedClass<?>) Reflectors.main().get(Enum.class);
    
    private static final @NotNull ReflectedField nameField =
            Objects.requireNonNull(enumClass.field("name"));
    
    public static final <T extends Enum<T>> void changeName(final @NotNull T t,
            final String newName) {
        Objects.requireNonNull(t);
        nameField.setObject(t, newName);
    }
    
    public static final <T extends Enum<T>> void changeName(
            final @NotNull Class<Enum<T>> enumClass,
            final @NotNull BiFunction<String, T, String> renamer) {
        Objects.requireNonNull(enumClass);
        Objects.requireNonNull(renamer);
        //noinspection SuspiciousArrayCast
        for (final T t : (T[]) enumClass.getEnumConstants()) {
            changeName(t, renamer.apply(t.name(), t));
        }
    }
    
    public static final <T extends Enum<T>> void changeName(
            final @NotNull Class<Enum<T>> enumClass, final @NotNull Function<T, String> renamer) {
        changeName(enumClass, (oldName, t) -> renamer.apply(t));
    }
    
}