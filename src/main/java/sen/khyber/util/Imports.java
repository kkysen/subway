package sen.khyber.util;

import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Created by Khyber Sen on 2/11/2018.
 *
 * @author Khyber Sen
 */
public class Imports extends Classes {
    
    private final BiMap<Class<?>, String> imports;
    
    private static final class Init {
        
        private static final BiMap<Class<?>, String> imports = HashBiMap.create();
        
    }
    
    public Imports() {
        super(Init.imports.keySet());
        imports = Init.imports;
    }
    
    public Stream<Entry<Class<?>, String>> importStream() {
        return imports.entrySet().stream();
    }
    
    @Override
    public void add(final @NotNull Class<?> klass) {
        imports.put(klass, klass.getSimpleName());
    }
    
    public boolean hasImportedName(final @NotNull String simpleClassName) {
        Objects.requireNonNull(simpleClassName);
        return imports.containsValue(simpleClassName);
    }
    
    @Nullable
    public Class<?> importedClass(final @NotNull String simpleClassName) {
        Objects.requireNonNull(simpleClassName);
        return imports.inverse().get(simpleClassName);
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