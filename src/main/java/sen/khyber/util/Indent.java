package sen.khyber.util;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/21/2018.
 *
 * @author Khyber Sen
 */
public class Indent implements StringBuilderAppendable {
    
    private static final Indent INSTANCE = new Indent();
    
    public static final Indent get() {
        return INSTANCE;
    }
    
    private static final String INDENT = "    ";
    private static final String NEWLINE = "\n";
    
    private final StringBuilder indent = new StringBuilder(NEWLINE);
    private int indentLevel = 0;
    
    public final int indentLevel() {
        return indentLevel;
    }
    
    public final int indentLength() {
        return NEWLINE.length() + indentLevel * INDENT.length();
    }
    
    public final Indent indent() {
        indent.append(INDENT);
        indentLevel += INDENT.length();
        return this;
    }
    
    public final Indent unindent() {
        if (indentLevel != 0) {
            indent.delete(indent.length() - INDENT.length(), indent.length());
            indentLevel -= INDENT.length();
        }
        return this;
    }
    
    @Override
    public final @NotNull StringBuilder append(@NotNull final StringBuilder sb) {
        return sb.append(indent);
    }
    
    @Override
    public final @NotNull String toString() {
        return defaultToString();
    }
    
}