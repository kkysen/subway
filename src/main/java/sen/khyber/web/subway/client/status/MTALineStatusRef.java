package sen.khyber.web.subway.client.status;

import sen.khyber.util.StringBuilderAppendable;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Khyber Sen on 2/18/2018.
 *
 * @author Khyber Sen
 */
public final class MTALineStatusRef implements StringBuilderAppendable {
    
    private @Nullable MTALineStatus value;
    
    private boolean showText = false;
    
    private MTALineStatusRef(final @NotNull MTALineStatus value) {
        Objects.requireNonNull(value);
        this.value = value;
    }
    
    MTALineStatusRef(final @NotNull MTALine<?> line) {
        value = null;
    }
    
    public final void showText(final boolean showText) {
        this.showText = showText;
    }
    
    public final void showText() {
        showText(true);
    }
    
    public final @NotNull MTALineStatus get() {
        //noinspection ConstantConditions
        return value;
    }
    
    public final boolean set(final @NotNull MTALineStatus value, final MTASystemStatus system) {
        Objects.requireNonNull(value);
        final MTALineStatus old = this.value;
        if (old != null) {
            if (!old.isSameKind(value)) {
                throw new IllegalArgumentException(value + " is not the same kind as " + old);
            }
            if (old.equals(value)) {
                old.setSameEndTime(value);
                return false;
            }
        }
        // TODO add to DB
        value.showText(showText);
        this.value = value;
        return true;
    }
    
    public final void init(final @NotNull MTALine<?> line) {
        Objects.requireNonNull(line);
        value = MTALineStatus.createDefault(line);
    }
    
    @Override
    public final @NotNull StringBuilder appendSelf(final @NotNull StringBuilder sb) {
        if (value == null) {
            return sb;
        }
        return value.appendSelf(sb);
    }
    
    @Override
    public final @NotNull StringBuilder append(@NotNull final StringBuilder sb) {
        if (value == null) {
            return sb;
        }
        return value.append(sb);
    }
    
    @Override
    public final @NotNull String toString() {
        return defaultToString();
    }
    
}