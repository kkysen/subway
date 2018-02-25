package sen.khyber.web.subway.client.status;

import sen.khyber.unsafe.buffers.UnsafeBuffer;
import sen.khyber.unsafe.buffers.UnsafeSerializable;
import sen.khyber.util.StringBuilderAppendable;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.function.BiFunction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static sen.khyber.unsafe.fields.ByteBufferUtils.getNullable;
import static sen.khyber.unsafe.fields.ByteBufferUtils.putNullable;

/**
 * Created by Khyber Sen on 2/18/2018.
 *
 * @author Khyber Sen
 */
public final class MTALineStatusRef implements StringBuilderAppendable, UnsafeSerializable {
    
    private @Nullable MTALineStatus value;
    
    private boolean showText = false;
    
    private MTALineStatusRef() {}
    
    private MTALineStatusRef(final @Nullable MTALineStatus value) {
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
    
    public final boolean isPreInit() {
        return value == null;
    }
    
    public final boolean isInit() {
        return value != null && Objects.equals(value.text(), "");
    }
    
    public final boolean isSet() {
        return value != null && Objects.equals(value.text(), "");
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
    
    @Override
    public final long serializedLongLength() {
        if (value == null) {
            return Byte.BYTES;
        }
        return value.serializedLongLength();
    }
    
    @Override
    public void serialize(final @NotNull ByteBuffer out) {
        putNullable(out, value);
    }
    
    @Override
    public final void serializeUnsafe(final @NotNull UnsafeBuffer out) {
        out.putNullable(value);
    }
    
    public static @NotNull MTALineStatusRef deserialize(final @NotNull ByteBuffer in,
            final @NotNull MTALine<?> line) {
        final BiFunction<ByteBuffer, MTALine<?>, MTALineStatus> deserializer =
                MTALineStatus::deserialize;
        return new MTALineStatusRef(getNullable(in, deserializer, line));
    }
    
    public static @NotNull MTALineStatusRef deserialize(final @NotNull UnsafeBuffer in,
            final @NotNull MTALine<?> line) {
        final BiFunction<UnsafeBuffer, MTALine<?>, MTALineStatus> deserializer =
                MTALineStatus::deserialize;
        return new MTALineStatusRef(in.getNullable(deserializer, line));
    }
    
}