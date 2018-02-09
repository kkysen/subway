package sen.khyber.proto;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Formats a generated Java file from a .proto protobuf file.
 * <p>
 * Created by Khyber Sen on 2/2/2018.
 *
 * @author Khyber Sen
 */
public class ProtoFormatter {
    
    public static List<Consumer<ProtoFileFormatter>> allActions() {
        return Arrays.asList(
                ProtoFileFormatter::removeProtocInsertionPointComments,
                ProtoFileFormatter::addClassJavadoc,
                ProtoFileFormatter::formatLastFewLines,
                ProtoFileFormatter::refactorNullChecks,
                ProtoFileFormatter::refactorNullChecks,
                ProtoFileFormatter::refactorStringAndByteStringGetters
        );
    }
    
    private final Set<Consumer<ProtoFileFormatter>> actions = new HashSet<>();
    
    public void addAction(final Consumer<ProtoFileFormatter> action) {
        actions.add(action);
    }
    
    // TODO other methods
    
    // TODO other constructors
    
    public void format(final Path inPath, final Path outPath) throws IOException {
        final ProtoFileFormatter formatter = new ProtoFileFormatter(inPath);
        actions.forEach(action -> action.accept(formatter));
        formatter.save(outPath);
    }
    
    public void format(final Path path) throws IOException {
        format(path, path);
    }
    
}