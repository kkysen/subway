package sen.khyber.web.subway.client.status;

import sen.khyber.unsafe.UnsafeUtils;
import sen.khyber.unsafe.buffers.UnsafeBuffer;
import sen.khyber.unsafe.buffers.UnsafeSerializable;
import sen.khyber.unsafe.reflect.Reflectors;
import sen.khyber.util.StringBuilderAppendable;

import java.nio.ByteBuffer;

import org.jetbrains.annotations.NotNull;

import static sen.khyber.unsafe.fields.ByteBufferUtils.getUnsignedByte;
import static sen.khyber.unsafe.fields.ByteBufferUtils.putUnsignedByte;


/**
 * Created by Khyber Sen on 2/18/2018.
 *
 * @author Khyber Sen
 */
public interface MTALine<T extends Enum<T> & MTALine<T>> extends StringBuilderAppendable,
        UnsafeSerializable {
    
    public static @NotNull MTALine<?> get(final int typeOrdinal, final int lineOrdinal) {
        return MTAType.get(typeOrdinal).line(lineOrdinal);
    }
    
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
    
    public default @NotNull T copy() {
        return UnsafeUtils.shallowCopy((T) this);
    }
    
    @Override
    public default @NotNull StringBuilder appendSelf(final @NotNull StringBuilder sb) {
        sb.append(officialName())
                .append(": Status[");
        return lineStatus()
                .appendSelf(sb)
                .append(']');
    }
    
    @Override
    public default @NotNull StringBuilder append(final @NotNull StringBuilder sb) {
        sb.append(type().name())
                .append("Line[");
        return appendSelf(sb).append(']');
    }
    
    @Override
    public default @NotNull String defaultToString() {
        return append(new StringBuilder()).toString();
    }
    
    @Override
    default long serializedLongLength() {
        return type().serializedLongLength() + Byte.BYTES + lineStatus().serializedLongLength();
    }
    
    @Override
    default void serialize(final @NotNull ByteBuffer out) {
        type().serialize(out);
        putUnsignedByte(out, ordinal());
        lineStatus().serialize(out);
    }
    
    @Override
    default void serializeUnsafe(final @NotNull UnsafeBuffer out) {
        type().serializeUnsafe(out);
        out.putUnsignedByte(ordinal());
        lineStatus().serializeUnsafe(out);
    }
    
    private static @NotNull MTALine<?> deserialize(final @NotNull MTAType type, final int ordinal) {
        return type.line(ordinal).copy();
    }
    
    private static void setLineStatus(final @NotNull MTALine<?> line,
            final @NotNull MTALineStatusRef lineStatus) {
        Reflectors.main().get(line.getClass()).fieldUnchecked("lineStatus")
                .setObject(line, lineStatus);
    }
    
    public static @NotNull MTALine<?> deserialize(final @NotNull ByteBuffer in) {
        final MTALine<?> line = deserialize(MTAType.deserialize(in), getUnsignedByte(in));
        setLineStatus(line, MTALineStatusRef.deserialize(in, line));
        return line;
    }
    
    public static @NotNull MTALine<?> deserialize(final @NotNull UnsafeBuffer in) {
        final MTALine<?> line = deserialize(MTAType.deserialize(in), in.getUnsignedByte());
        setLineStatus(line, MTALineStatusRef.deserialize(in, line));
        return line;
    }
    
}