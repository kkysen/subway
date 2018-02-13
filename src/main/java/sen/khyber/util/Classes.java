package sen.khyber.util;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/9/2018.
 *
 * @author Khyber Sen
 */
public class Classes {
    
    protected final @NotNull Set<Class<?>> classes;
    
    protected Classes(final @NotNull Set<Class<?>> classes) {
        Objects.requireNonNull(classes);
        this.classes = classes;
    }
    
    public Classes() {
        this(new LinkedHashSet<>());
    }
    
    public int size() {
        return classes.size();
    }
    
    public @NotNull Stream<Class<?>> stream() {
        return classes.stream();
    }
    
    public void add(final @NotNull Class<?> klass) {
        classes.add(klass);
    }
    
    public boolean remove(final @NotNull Class<?> klass) {
        return classes.remove(klass);
    }
    
    public boolean has(final @NotNull Class<?> klass) {
        return classes.contains(klass);
    }
    
    public static @NotNull Optional<Class<?>> loadClass(final @NotNull String className) {
        Objects.requireNonNull(className);
        try {
            return Optional.of(Class.forName(className, false, ClassLoader.getSystemClassLoader()));
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
    
    public boolean add(final @NotNull String className) {
        return loadClass(className).map(klass -> {
            add(klass);
            return true;
        })
                .orElse(false);
    }
    
    public boolean remove(final @NotNull String className) {
        return loadClass(className).map(this::remove).orElse(false);
    }
    
}