package sen.khyber.web.subway.client.status;

import sen.khyber.util.StringBuilderAppendable;

import org.jetbrains.annotations.NotNull;


/**
 * Created by Khyber Sen on 2/18/2018.
 *
 * @author Khyber Sen
 */
public interface MTALine<T extends Enum<T> & MTALine<T>> extends StringBuilderAppendable {
    
    public @NotNull MTAType type();
    
    public @NotNull String officialName();
    
    public @NotNull String name();
    
    public @NotNull MTALineStatusRef lineStatus();
    
    public default void initLineStatus() {
        lineStatus().init(this);
    }
    
    public int ordinal();
    
    public default int allLinesOrdinal() {
        return type().lineOrdinalOffset() + ordinal();
    }
    
    @Override
    public boolean equals(Object o);
    
    public default boolean equals(final MTALine<?> line) {
        return equals((Object) line);
    }
    
    public default boolean equals(final T line) {
        return equals((MTALine<?>) line);
    }
    
    @Override
    public int hashCode();
    
    @Override
    public default @NotNull StringBuilder append(final @NotNull StringBuilder sb) {
        sb.append(type().name())
                .append("Line[")
                .append(officialName())
                .append(", status=");
        return lineStatus().append(sb)
                .append(']');
    }
    
    @Override
    public default @NotNull String defaultToString() {
        return append(new StringBuilder()).toString();
    }
    
}