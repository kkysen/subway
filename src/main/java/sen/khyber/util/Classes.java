package sen.khyber.util;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Created by Khyber Sen on 2/9/2018.
 *
 * @author Khyber Sen
 */
public class Classes {
    
    private final Set<Class<?>> classes = new LinkedHashSet<>();
    
    public int size() {
        return classes.size();
    }
    
    public Stream<Class<?>> stream() {
        return classes.stream();
    }
    
    public void add(final Class<?> klass) {
        classes.add(klass);
    }
    
    public boolean remove(final Class<?> klass) {
        return classes.remove(klass);
    }
    
    private Optional<Class<?>> loadClass(final String className) {
        try {
            return Optional.of(Class.forName(className, false, getClass().getClassLoader()));
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
    
    public boolean add(final String className) {
        return loadClass(className).map(klass -> {
            add(klass);
            return true;
        })
                .orElse(false);
    }
    
    public boolean remove(final String className) {
        return loadClass(className).map(this::remove).orElse(false);
    }
    
    public boolean addImport(final String line) {
        final int start = line.indexOf("import ");
        if (start == -1) {
            return false;
        }
        final int end = line.lastIndexOf(';');
        if (end == -1 || start >= end) {
            return false;
        }
        final String className = line.substring(start, end);
        return className.indexOf('.') != -1 && add(className);
    }
    
    public void addImports(final Iterable<String> lines) {
        lines.forEach(this::addImport);
    }
    
    public Stream<String> asImports() {
        return classes.stream()
                .map(klass -> "import " + klass.getName() + ";");
    }
    
    public String[] toImports() {
        return asImports().toArray(String[]::new);
    }
    
}