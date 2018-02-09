package sen.khyber.proto;

import lombok.AccessLevel;
import lombok.Setter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Khyber Sen on 2/2/2018.
 *
 * @author Khyber Sen
 */
public class ProtoFileFormatter {
    
    private final Charset charset = StandardCharsets.UTF_8;
    
    private final List<String> lines;
    
    private final Set<Class<?>> imports = new HashSet<>();
    
    public ProtoFileFormatter(final Path path) throws IOException {
        Objects.requireNonNull(path);
        lines = Files.readAllLines(path, charset);
    }
    
    public void removeProtocInsertionPointComments() {
        lines.removeIf(line -> line.contains("// @@protoc_insertion_point"));
    }
    
    private static final Pattern nullCheckPattern = Pattern.compile(
            "if \\(([^ ]*) == null\\) \\{[\\s]*throw new NullPointerException\\(\\);"
                    + "[^}]*}");
    
    public void refactorNullChecks() {
        boolean found = false;
        for (int i = 0; i < lines.size() - 2; i++) {
            final String line = lines.get(i);
            int numNullCheckLines = 3;
            if (line.contains("== null")) {
                List<String> nullCheckLines = lines.subList(i, i + numNullCheckLines);
                String nullCheck = String.join(" ", nullCheckLines);
                Matcher matcher = nullCheckPattern.matcher(nullCheck);
                if (matcher.find()) {
                    String variableName = matcher.group(1);
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
    
    public void refactorStringAndByteStringGetters() {
        final boolean found = false;
        // TODO
        if (found) {
            imports.add(ProtoUtils.class);
            imports.add(Setter.class);
            imports.add(AccessLevel.class);
        }
    }
    
    public void addAuthorTagInClassJavadoc() {
        
    }
    
    private static boolean isWhitespace(final String s) {
        return s.trim().isEmpty();
    }
    
    public void formatLastFewLines() {
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
    
    private void addImports() {
        if (imports.size() == 0) {
            return;
        }
        final int i = importLineNumber();
        if (i != -1) {
            final String[] importStrings = imports
                    .stream()
                    .map(klass -> "import " + klass.getName() + ";")
                    .toArray(String[]::new);
            lines.addAll(i, Arrays.asList(importStrings));
        }
    }
    
    public void save(final Path path) throws IOException {
        addImports();
        Files.write(path, lines, charset);
    }
    
}