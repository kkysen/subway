package sen.khyber.util;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/18/2018.
 *
 * @author Khyber Sen
 */
@FunctionalInterface
public interface StringBuilderAppendable {
    
    public @NotNull StringBuilder append(@NotNull StringBuilder sb);
    
    public default @NotNull String defaultToString() {
        return append(new StringBuilder()).toString();
    }
    
    @Override
    public @NotNull String toString();
    
}