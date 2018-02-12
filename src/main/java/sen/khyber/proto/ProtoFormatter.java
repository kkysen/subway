package sen.khyber.proto;

import lombok.ToString;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Formats a generated Java file from a .proto protobuf file.
 * <p>
 * Created by Khyber Sen on 2/2/2018.
 *
 * @author Khyber Sen
 */
@ToString
public class ProtoFormatter {
    
    private static final Set<Consumer<ProtoFileFormatter>> ALL_ACTIONS = Set.of(
            ProtoFileFormatter::removeProtocInsertionPointComments,
            ProtoFileFormatter::addClassJavadoc,
            ProtoFileFormatter::formatLastFewLines,
            ProtoFileFormatter::refactorNullChecks,
            ProtoFileFormatter::refactorNullChecks,
            ProtoFileFormatter::refactorStringAndByteStringGetters,
            ProtoFileFormatter::removeImplicitConstructorCalls,
            ProtoFileFormatter::replaceDeprecatedValueOfWithForNumber
    );
    
    public static Set<Consumer<ProtoFileFormatter>> allActions() {
        return ALL_ACTIONS;
    }
    
    private final Set<Consumer<ProtoFileFormatter>> actions = new HashSet<>();
    
    public ProtoFormatter() {}
    
    public final void addAction(final Consumer<ProtoFileFormatter> action) {
        actions.add(action);
    }
    
    // TODO other methods
    
    // TODO other constructors
    
    public final void format(final Path inPath, final Path outPath) throws IOException {
        final ProtoFileFormatter formatter = new ProtoFileFormatter(inPath);
        actions.forEach(action -> action.accept(formatter));
        formatter.save(outPath);
    }
    
    public final void format(final Path path) throws IOException {
        format(path, path);
    }
    
}