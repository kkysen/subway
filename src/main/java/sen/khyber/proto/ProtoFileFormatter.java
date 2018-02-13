package sen.khyber.proto;

import sen.khyber.unsafe.reflect.ReflectedClass;
import sen.khyber.unsafe.reflect.ReflectedField;
import sen.khyber.unsafe.reflect.ReflectedMember;
import sen.khyber.unsafe.reflect.ReflectedMethod;
import sen.khyber.unsafe.reflect.Reflector;
import sen.khyber.util.Imports;
import sen.khyber.util.exceptions.ExceptionUtils;
import sen.khyber.util.immutable.ImmutableArrayList;

import lombok.AccessLevel;
import lombok.Setter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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

import com.google.protobuf.ByteString;

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
//    private final List<List<String>> initialLines;
    
    private final Imports imports = new Imports();
    
    public ProtoFileFormatter(final Path path) throws IOException {
        Objects.requireNonNull(path);
        this.path = path;
        lines = Files.readAllLines(path, charset);
//        initialLines = IntStream.range(0, lines.size())
//                .mapToObj(i -> lines.subList(i, i + 1))
//                .collect(Collectors.toList());
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
    
    private static final String classStartPattern = " class ";
    
    private String findBetween(final String preIdentifier, final char postIdenitifer,
            final String name) {
        for (final String line : lines) {
            int index = line.indexOf(preIdentifier);
            if (index == -1) {
                continue;
            }
            index += preIdentifier.length();
            return line.substring(index, line.indexOf(postIdenitifer, index)).trim();
        }
        throw new IllegalStateException("no " + name + " found");
    }
    
    private String packageName() {
        return findBetween("package ", ';', "package");
    }
    
    private String className() {
        return findBetween(classStartPattern, '{', "class");
    }
    
    private ReflectedClass<?> loadProtoClass() {
        final String fullClassName = packageName() + '.' + className();
        return Reflector.get().forClassNameUnchecked(fullClassName);
    }
    
    private static String uncapitalize(final String s) {
        if (s.isEmpty()) {
            return "";
        }
        final char[] chars = s.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }
    
    private void refactorStringUnionByteStringField(final ReflectedField field) {
        return; // TODO
    }
    
    private void refactorStringGetter(final ReflectedMethod method,
            final StackTraceElement stackFrame) {
        return; // TODO
    }
    
    private void refactorByteStringGetter(final ReflectedMethod method,
            final StackTraceElement stackFrame) {
        return; // TODO
    }
    
    private void refactorStringAndByteStringGetters(final ReflectedClass<?> klass,
            final ReflectedField field,
            final ReflectedMethod stringGetter,
            final ReflectedMethod byteStringGetter) {
        refactorStringUnionByteStringField(field);
        final Object proto = klass.allocateInstance();
        final Object ref = 0;
        field.bindUnsafe(proto).setObject(ref);
        final StackTraceElement[] stackTraces =
                new ImmutableArrayList<>(stringGetter, byteStringGetter)
                        .stream()
                        .map(method -> {
                            try {
                                stringGetter.method().invoke(proto);
                                throw new IllegalStateException(
                                        "should've caused and caught a ClassCastException");
                            } catch (final IllegalAccessException e) {
                                throw ExceptionUtils.atRuntime(e);
                            } catch (final InvocationTargetException e) {
                                return e;
                            }
                        })
                        .map(InvocationTargetException::getTargetException)
                        .map(Throwable::getStackTrace)
                        .map(a -> a[0])
                        .toArray(StackTraceElement[]::new);
        refactorStringGetter(stringGetter, stackTraces[0]);
        refactorByteStringGetter(byteStringGetter, stackTraces[1]);
    }
    
    private boolean refactorStringAndByteStringGetters(final ReflectedMethod stringGetter,
            final ReflectedClass<?> klass) {
        final String getterName = stringGetter.name();
        final ReflectedMethod byteStringGetter =
                klass.method(getterName + "Bytes");
        if (byteStringGetter == null
                || byteStringGetter.method().getReturnType() != ByteString.class) {
            return false;
        }
        final String fieldName = uncapitalize(getterName.substring("get".length())) + '_';
        final ReflectedField field = klass.field(fieldName);
        if (field == null || field.field().getType() != Object.class) {
            return false;
        }
        refactorStringAndByteStringGetters(klass, field, stringGetter, byteStringGetter);
        return true;
    }
    
    public final void refactorStringAndByteStringGetters() {
        final ReflectedClass<?> klass = loadProtoClass();
        // get all String getters that have a normal and bytes version
        klass
                .methods()
                .stream()
                .filter(ReflectedMember::isInstance)
                .filter(method -> method.method().getReturnType() == String.class)
                .filter(method -> method.name().startsWith("get"))
                .map(stringGetter -> refactorStringAndByteStringGetters(stringGetter, klass))
                .filter(Boolean::booleanValue)
                .findAny()
                .ifPresent(pair -> {
                    imports.add(ProtoUtils.class);
                    imports.add(Setter.class);
                    imports.add(AccessLevel.class);
                });
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