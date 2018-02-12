package sen.khyber.proto;

import sen.khyber.unsafe.reflect.ReflectedClass;
import sen.khyber.unsafe.reflect.Reflector;
import sen.khyber.util.Imports;

import lombok.AccessLevel;
import lombok.Setter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

/**
 * Created by Khyber Sen on 2/2/2018.
 *
 * @author Khyber Sen
 */
public class ProtoFileFormatter {
    
    /*
    Run as IntelliJ inspections:
    
    - Anonymous type can be replaced with method reference
     */
    
    private final Charset charset = StandardCharsets.UTF_8;
    
    private final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendValue(MONTH_OF_YEAR)
            .appendLiteral('/')
            .appendValue(DAY_OF_MONTH)
            .appendLiteral('/')
            .appendValue(YEAR)
            .toFormatter();
    
    private final Path path;
    
    private final List<String> lines;
    
    private final Imports imports = new Imports();
    
    public ProtoFileFormatter(final Path path) throws IOException {
        Objects.requireNonNull(path);
        this.path = path;
        lines = Files.readAllLines(path, charset);
        loadExistingImports();
    }
    
    @Override
    public final String toString() {
        return "ProtoFileFormatter[path=" + path + ']';
    }
    
    public final void removeProtocInsertionPointComments() {
        lines.removeIf(line -> line.contains("// @@protoc_insertion_point"));
    }
    
    private static final Pattern nullCheckPattern = Pattern.compile(
            "if \\(([^ ]*) == null\\) \\{[\\s]*throw new NullPointerException\\(\\);"
                    + "[^}]*}");
    
    public final void refactorNullChecks() {
        boolean found = false;
        for (int i = 0; i < lines.size() - 2; i++) {
            final String line = lines.get(i);
            final int numNullCheckLines = 3;
            if (line.contains("== null")) {
                final List<String> nullCheckLines = lines.subList(i, i + numNullCheckLines);
                final String nullCheck = String.join(" ", nullCheckLines);
                final Matcher matcher = nullCheckPattern.matcher(nullCheck);
                if (matcher.find()) {
                    final String variableName = matcher.group(1);
                    nullCheckLines.subList(1, numNullCheckLines).clear();
                    nullCheckLines.set(0, "Objects.requireNonNull(" + variableName + ");");
                    found = true;
                }
            }
        }
        if (found) {
            imports.add(Objects.class);
        }
    }
    
    public final void refactorStringAndByteStringGetters() {
        final boolean found = false;
        // TODO
        if (found) {
            imports.add(ProtoUtils.class);
            imports.add(Setter.class);
            imports.add(AccessLevel.class);
        }
    }
    
    public final void replaceDeprecatedValueOfWithForNumber() {
        final String toReplace = "valueOf";
        final String replaceWith = "forNumber";
        for (int i = 0; i < lines.size(); i++) {
            final String line = lines.get(i);
            final int classNameEnd = line.indexOf('.' + toReplace + ')');
            if (classNameEnd == -1) {
                continue;
            }
            final int classNameStart = line.lastIndexOf(' ', classNameEnd);
            final String className = line.substring(classNameStart, classNameStart);
            Class<?> klass = imports.importedClass(className);
            if (klass == null) {
                final Optional<Class<?>> optionalClass = Reflector.classForName(className);
                if (!optionalClass.isPresent()) {
                    throw new IllegalStateException("illegal class name: " + className);
                }
                klass = optionalClass.get();
            }
            final ReflectedClass<?> reflectedClass = Reflector.get().forClass(klass);
            if (reflectedClass.hasMethod(toReplace) && reflectedClass.hasMethod(replaceWith)) {
                final String newLine = line.substring(0, classNameEnd + ".".length())
                        + replaceWith
                        + line.substring(classNameEnd + ('.' + toReplace).length());
                lines.set(i, newLine);
                i--; // recheck this line for any more replacements
            }
        }
    }
    
    private static final String classStartPattern = " class ";
    
    public final void addClassJavadoc() {
        final String date = LocalDate.now().format(formatter);
        final String[] javadoc = {
                "/**",
                "* Created by Khyber Sen on " + date + '.',
                "*",
                "* @author Khyber Sen", "*/",
        };
        
        //        IntStream.range(0, lines.size())
        //                .mapToObj(i -> Pair.of(i, lines.get(i)))
        //                .filter(line -> line.getValue().contains(" class "))
        //                .filter(line -> !lines.get(line.getKey()).contains("*/"))
        //                // subList ensures that each insertion doesn't change mess up indices
        //                .map(line -> lines.subList(line.getKey(), line.getKey()))
        //                .forEach(insertionPoint -> insertionPoint.addAll(Arrays.asList(javadoc)));
        
        for (int i = 0; i < lines.size(); i++) {
            // place before class declaration and only if no comment already there
            if (lines.get(i).contains(classStartPattern) && !lines.get(i - 1).contains("*/")) {
                lines.addAll(i - 1, Arrays.asList(javadoc));
                i += javadoc.length;
            }
        }
    }
    
    public final void removeImplicitConstructorCalls() {
        lines.removeIf(line -> line.contains("this()") || line.contains("super()"));
    }
    
    private static boolean isWhitespace(final String s) {
        return s.trim().isEmpty();
    }
    
    public final void formatLastFewLines() {
        int i = lines.size() - 1;
        for (; i >= 0; i--) {
            if (isWhitespace(lines.get(i))) {
                lines.remove(i);
            } else {
                break;
            }
        }
        if (lines.get(i).charAt(0) == '}' && !isWhitespace(lines.get(i - 1))) {
            lines.add(i, "");
        }
    }
    
    private int importLineNumber() {
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains("package")) {
                return i + 1;
            }
        }
        return -1;
    }
    
    private void loadExistingImports() {
        final int i = importLineNumber();
        if (i != -1) {
            for (int j = i; j < lines.size(); j++) {
                // remove existing
                final String line = lines.get(j);
                if (line.contains(classStartPattern)) {
                    break;
                }
                if (imports.addImport(line)) {
                    lines.remove(j);
                    j--;
                }
            }
        }
    }
    
    private void addImports() {
        if (imports.size() == 0) {
            return;
        }
        final int i = importLineNumber();
        if (i != -1) {
            lines.addAll(i, Arrays.asList(imports.toImports()));
        }
    }
    
    public final void save(final Path path) throws IOException {
        addImports();
        Files.write(path, lines, charset);
    }
    
}