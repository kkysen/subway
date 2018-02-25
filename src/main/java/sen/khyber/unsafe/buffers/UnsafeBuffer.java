package sen.khyber.unsafe.buffers;

import sen.khyber.io.SizeType;
import sen.khyber.unsafe.fields.ByteBufferUtils;
import sen.khyber.unsafe.fields.StringUtils;
import sen.khyber.util.StringBuilderAppendable;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static sen.khyber.unsafe.fields.StringUtils.coder;
import static sen.khyber.unsafe.fields.StringUtils.getRawValue;

/**
 * @author Khyber Sen
 */
@SuppressWarnings({
        "ClassWithTooManyMethods", "OverlyComplexClass", "ClassReferencesSubclass",
        "StaticMethodOnlyUsedInOneClass"
})
public interface UnsafeBuffer extends Cloneable, Closeable, StringBuilderAppendable {
    
    public static @NotNull UnsafeDirectBuffer allocate(final long size) {
        return new UnsafeDirectBuffer(size);
    }
    
    public static @NotNull UnsafeWrappedBuffer wrap(final @NotNull ByteBuffer buffer) {
        Objects.requireNonNull(buffer);
        return new UnsafeWrappedBuffer(buffer);
    }
    
    public static @NotNull UnsafeWrappedBuffer allocateWrapped(final int size) {
        return wrap(ByteBufferUtils.useNativeOrder(ByteBuffer.allocateDirect(size)));
    }
    
    public static @NotNull UnsafeMappedBuffer mmap(final @NotNull Path path, final long offset,
            final long size)
            throws IOException {
        Objects.requireNonNull(path);
        try (final RandomAccessFile raf = new RandomAccessFile(path.toFile(), "rw");
                final LongFileChannel channel = new LongFileChannel(raf)) {
            //noinspection ConstantConditions
            return channel.longRWMap(offset, size);
        }
    }
    
    public static @NotNull UnsafeMappedBuffer mmap(final @NotNull Path path, final long size)
            throws IOException {
        Objects.requireNonNull(path);
        return mmap(path, 0, size);
    }
    
    public static @NotNull UnsafeMappedBuffer mmap(final @NotNull Path path) throws IOException {
        Objects.requireNonNull(path);
        return mmap(path, path.toFile().length());
    }
    
    public static void wrapExisting(final @NotNull UnsafeDirectBuffer unsafeBuffer,
            final @NotNull Buffer buffer) {
        try (unsafeBuffer) {
            //noinspection resource
            Objects.requireNonNull(unsafeBuffer);
        }
        Objects.requireNonNull(buffer);
        ByteBufferUtils.setAddress(buffer, unsafeBuffer.positionAddress());
        ByteBufferUtils.setCapacity(buffer, unsafeBuffer.remaining());
        buffer.position(0);
        buffer.limit(buffer.capacity());
    }
    
    public static @NotNull ByteBuffer wrapExisting(final @NotNull UnsafeDirectBuffer unsafeBuffer) {
        //noinspection resource
        Objects.requireNonNull(unsafeBuffer);
        if (unsafeBuffer.isWrapping()) {
            return ((UnsafeWrappedBuffer) unsafeBuffer).wrapped();
        }
        final ByteBuffer buffer = ByteBuffer.allocateDirect(0);
        wrapExisting(unsafeBuffer, buffer);
        return buffer;
    }
    
    /**
     * After calling free(), using this buffer has undefined behavior (likely a
     * segfault).
     * <p>
     * If {@link #duplicate()} or {@link Object#clone()} is called, then only
     * one out of the original and duplicates/clones can be freed.
     * If any one if freed, then using any other duplicate/clone has undefined
     * behavior (likely a segfault).
     */
    public void free();
    
    @Override
    public void close();
    
    public void finalize() throws Throwable;
    
    public boolean isDirect();
    
    public boolean isWrapping();
    
    public boolean isMapped();
    
    public long address();
    
    public long size();
    
    public long position();
    
    public long limit();
    
    public default long positionAddress() {
        return address() + position();
    }
    
    public default long remaining() {
        return limit() - position();
    }
    
    public default boolean hasRemaining() {
        return remaining() > 0;
    }
    
    public void position(long newPosition);
    
    public default void skip(final long skipSize) {
        position(position() + skipSize);
        assert position() >= 0 && position() < limit();
    }
    
    public default void rewind() {
        position(0);
    }
    
    public default void reset() {
        rewind();
        limit(size());
    }
    
    public void limit(long newLimit);
    
    public default void moveLimit(final long skipSize) {
        limit(limit() + skipSize);
        assert limit() >= position() && limit() < size();
    }
    
    public default void setRemaining(final long newRemaining) {
        limit(position() + newRemaining);
    }
    
    public void getMemory(long index, long address, long size);
    
    public void putMemory(long index, long address, long size);
    
    public void getMemory(long address, long size);
    
    public void putMemory(long address, long size);
    
    public default void getMemory(final long index, final @NotNull UnsafeBuffer buffer) {
        // TODO verify
        assert index >= 0 && index < limit();
        assert buffer.hasRemaining();
        getMemory(index, buffer.positionAddress(), buffer.remaining());
    }
    
    public default void putMemory(final long index, final @NotNull UnsafeBuffer buffer) {
        // TODO verify
        assert index >= 0 && index < limit();
        assert buffer.hasRemaining();
        putMemory(index, buffer.positionAddress(), buffer.remaining());
    }
    
    public default void getMemory(final @NotNull UnsafeBuffer buffer) {
        getMemory(position(), buffer);
        skip(buffer.remaining());
        assert position() < limit();
    }
    
    public default void putMemory(final @NotNull UnsafeBuffer buffer) {
        putMemory(position(), buffer);
        skip(buffer.remaining());
        assert position() < limit();
    }
    
    public void fill(byte val, long offset, long length);
    
    // TODO add other fillers
    
    public byte getByte(long index);
    
    public char getChar(long index);
    
    public short getShort(long index);
    
    public int getInt(long index);
    
    public long getLong(long index);
    
    public float getFloat(long index);
    
    public double getDouble(long index);
    
    public void putByte(long index, byte b);
    
    public void putChar(long index, char c);
    
    public void putShort(long index, short s);
    
    public void putInt(long index, int i);
    
    public void putLong(long index, long L);
    
    public void putFloat(long index, float f);
    
    public void putDouble(long index, double d);
    
    public default byte getByte() {
        final byte b = getByte(position());
        skip(Byte.BYTES);
        return b;
    }
    
    public default char getChar() {
        final char c = getChar(position());
        skip(Character.BYTES);
        return c;
    }
    
    public default short getShort() {
        final short s = getShort(position());
        skip(Short.BYTES);
        return s;
    }
    
    public default int getInt() {
        final int i = getInt(position());
        skip(Integer.BYTES);
        return i;
    }
    
    public default long getLong() {
        final long L = getLong(position());
        skip(Long.BYTES);
        return L;
    }
    
    public default float getFloat() {
        final float f = getFloat(position());
        skip(Float.BYTES);
        return f;
    }
    
    public default double getDouble() {
        final double d = getDouble(position());
        skip(Double.BYTES);
        return d;
    }
    
    public default void putByte(final byte b) {
        putByte(position(), b);
        skip(Byte.BYTES);
    }
    
    public default void putChar(final char c) {
        putChar(position(), c);
        skip(Character.BYTES);
    }
    
    public default void putShort(final short s) {
        putShort(position(), s);
        skip(Short.BYTES);
    }
    
    public default void putInt(final int i) {
        putInt(position(), i);
        skip(Integer.BYTES);
    }
    
    public default void putLong(final long L) {
        putLong(position(), L);
        skip(Long.BYTES);
    }
    
    public default void putFloat(final float f) {
        putFloat(position(), f);
        skip(Float.BYTES);
    }
    
    public default void putDouble(final double d) {
        putDouble(position(), d);
        skip(Double.BYTES);
    }
    
    public void getBytes(long index, @NotNull byte[] array, int offset, int length);
    
    public void getChars(long index, @NotNull char[] array, int offset, int length);
    
    public void getShorts(long index, @NotNull short[] array, int offset, int length);
    
    public void getInts(long index, @NotNull int[] array, int offset, int length);
    
    public void getLongs(long index, @NotNull long[] array, int offset, int length);
    
    public void getFloats(long index, @NotNull float[] array, int offset, int length);
    
    public void getDoubles(long index, @NotNull double[] array, int offset, int length);
    
    public void putBytes(long index, @NotNull byte[] array, int offset, int length);
    
    public void putChars(long index, @NotNull char[] array, int offset, int length);
    
    public void putShorts(long index, @NotNull short[] array, int offset, int length);
    
    public void putInts(long index, @NotNull int[] array, int offset, int length);
    
    public void putLongs(long index, @NotNull long[] array, int offset, int length);
    
    public void putFloats(long index, @NotNull float[] array, int offset, int length);
    
    public void putDoubles(long index, @NotNull double[] array, int offset, int length);
    
    public default void getBytes(final @NotNull byte[] array, final int offset, final int length) {
        getBytes(position(), array, offset, length);
        skip(length * Byte.BYTES);
    }
    
    public default void getChars(final @NotNull char[] array, final int offset, final int length) {
        getChars(position(), array, offset, length);
        skip(length * Character.BYTES);
    }
    
    public default void getShorts(final @NotNull short[] array, final int offset,
            final int length) {
        getShorts(position(), array, offset, length);
        skip(length * Short.BYTES);
    }
    
    public default void getInts(final @NotNull int[] array, final int offset, final int length) {
        getInts(position(), array, offset, length);
        skip(length * Integer.BYTES);
    }
    
    public default void getLongs(final @NotNull long[] array, final int offset, final int length) {
        getLongs(position(), array, offset, length);
        skip(length * Long.BYTES);
    }
    
    public default void getFloats(final @NotNull float[] array, final int offset,
            final int length) {
        getFloats(position(), array, offset, length);
        skip(length * Float.BYTES);
    }
    
    public default void getDoubles(final @NotNull double[] array, final int offset,
            final int length) {
        getDoubles(position(), array, offset, length);
        skip(length * Double.BYTES);
    }
    
    public default void putBytes(final @NotNull byte[] array, final int offset, final int length) {
        putBytes(position(), array, offset, length);
        skip(length * Byte.BYTES);
    }
    
    public default void putChars(final @NotNull char[] array, final int offset, final int length) {
        putChars(position(), array, offset, length);
        skip(length * Character.BYTES);
    }
    
    public default void putShorts(final @NotNull short[] array, final int offset,
            final int length) {
        putShorts(position(), array, offset, length);
        skip(length * Short.BYTES);
    }
    
    public default void putInts(final @NotNull int[] array, final int offset, final int length) {
        putInts(position(), array, offset, length);
        skip(length * Integer.BYTES);
    }
    
    public default void putLongs(final @NotNull long[] array, final int offset, final int length) {
        putLongs(position(), array, offset, length);
        skip(length * Long.BYTES);
    }
    
    public default void putFloats(final @NotNull float[] array, final int offset,
            final int length) {
        putFloats(position(), array, offset, length);
        skip(length * Float.BYTES);
    }
    
    public default void putDoubles(final @NotNull double[] array, final int offset,
            final int length) {
        putDoubles(position(), array, offset, length);
        skip(length * Double.BYTES);
    }
    
    public default void getBytes(final @NotNull byte[] array) {
        getBytes(array, 0, array.length);
    }
    
    public default void getChars(final @NotNull char[] array) {
        getChars(array, 0, array.length);
    }
    
    public default void getShorts(final @NotNull short[] array) {
        getShorts(array, 0, array.length);
    }
    
    public default void getInts(final @NotNull int[] array) {
        getInts(array, 0, array.length);
    }
    
    public default void getLongs(final @NotNull long[] array) {
        getLongs(array, 0, array.length);
    }
    
    public default void getFloats(final @NotNull float[] array) {
        getFloats(array, 0, array.length);
    }
    
    public default void getDoubles(final @NotNull double[] array) {
        getDoubles(array, 0, array.length);
    }
    
    public default void putBytes(final @NotNull byte[] array) {
        putBytes(array, 0, array.length);
    }
    
    public default void putChars(final @NotNull char[] array) {
        putChars(array, 0, array.length);
    }
    
    public default void putShorts(final @NotNull short[] array) {
        putShorts(array, 0, array.length);
    }
    
    public default void putInts(final @NotNull int[] array) {
        putInts(array, 0, array.length);
    }
    
    public default void putLongs(final @NotNull long[] array) {
        putLongs(array, 0, array.length);
    }
    
    public default void putFloats(final @NotNull float[] array) {
        putFloats(array, 0, array.length);
    }
    
    public default void putDoubles(final @NotNull double[] array) {
        putDoubles(array, 0, array.length);
    }
    
    public default @NotNull byte[] getBytes(final int length) {
        final byte[] array = new byte[length];
        getBytes(array);
        return array;
    }
    
    public default @NotNull char[] getChars(final int length) {
        final char[] array = new char[length];
        getChars(array);
        return array;
    }
    
    public default @NotNull short[] getShorts(final int length) {
        final short[] array = new short[length];
        getShorts(array);
        return array;
    }
    
    public default @NotNull int[] getInts(final int length) {
        final int[] array = new int[length];
        getInts(array);
        return array;
    }
    
    public default @NotNull long[] getLongs(final int length) {
        final long[] array = new long[length];
        getLongs(array);
        return array;
    }
    
    public default @NotNull float[] getFloats(final int length) {
        final float[] array = new float[length];
        getFloats(array);
        return array;
    }
    
    public default @NotNull double[] getDoubles(final int length) {
        final double[] array = new double[length];
        getDoubles(array);
        return array;
    }
    
    // TODO finish other variants of these
    
    public default void putUnsignedByte(final int value) {
        assert value < (1 << Byte.SIZE);
        putByte((byte) (value + Byte.MIN_VALUE));
    }
    
    public default int getUnsignedByte() {
        return getByte() - Byte.MIN_VALUE;
    }
    
    public default @NotNull byte[] getShortBytes() {
        return getBytes(getShort());
    }
    
    public default @NotNull char[] getShortChars() {
        return getChars(getShort());
    }
    
    public default void putShortBytes(final @NotNull byte[] array) {
        putShort((short) array.length);
        putBytes(array);
    }
    
    public default void putShortChars(final @NotNull char[] array) {
        putShort((short) array.length);
        putChars(array);
    }
    
    public default <T> @NotNull T get(final @NotNull Function<UnsafeBuffer, T> deserializer) {
        return deserializer.apply(this);
    }
    
    public default <T> @Nullable T getNullable(
            final @NotNull Function<UnsafeBuffer, T> deserializer) {
        if (isNull()) {
            return null;
        }
        return get(deserializer);
    }
    
    public default <T, U> @NotNull T get(final @NotNull BiFunction<UnsafeBuffer, U, T> deserializer,
            final U context) {
        return deserializer.apply(this, context);
    }
    
    public default <T, U> @Nullable T getNullable(
            final @NotNull BiFunction<UnsafeBuffer, U, T> deserializer, final U context) {
        if (isNull()) {
            return null;
        }
        return get(deserializer, context);
    }
    
    public default void put(final @NotNull UnsafeSerializable serializable) {
        serializable.serializeUnsafe(this);
    }
    
    public default void putNullable(final @Nullable UnsafeSerializable serializable) {
        if (serializable == null) {
            putNull();
        } else {
            put(serializable);
        }
    }
    
    public static final byte NULL_BYTE = -1;
    
    public default void putNull() {
        putByte(NULL_BYTE);
    }
    
    // advance position only if isNull
    public default boolean isNull() {
        if (getByte(position()) == NULL_BYTE) {
            skip(1);
            return true;
        }
        return false;
    }
    
    private @NotNull String getString(final int encodedLength) {
        final int length = StringUtils.decodeLength(encodedLength);
        final byte coder = StringUtils.decodeCoder(encodedLength);
        final byte[] value = getBytes(length);
        return StringUtils.ofRawValue(value, coder);
    }
    
    public default @NotNull String getString() {
        return getString(getInt());
    }
    
    public default @NotNull String getShortString() {
        return getString(getShort());
    }
    
    public default @NotNull String getSuperShortString() {
        return getString(getByte());
    }
    
    private void putString(final @NotNull String s, final @NotNull SizeType sizeType) {
        assert sizeType.isSigned();
        final byte coder = coder(s);
        final byte[] value = getRawValue(s);
        final int length = value.length;
        final int encodedLength = StringUtils.encodeLength(length, coder);
        sizeType.serialize(this, encodedLength);
        putBytes(value);
    }
    
    public default void putString(final @NotNull String s) {
        putString(s, SizeType.INT);
    }
    
    public default void putShortString(final @NotNull String s) {
        putString(s, SizeType.SHORT);
    }
    
    public default void putSuperShortString(final @NotNull String s) {
        putString(s, SizeType.BYTE);
    }
    
    public default @Nullable String getNullableString() {
        if (isNull()) {
            return null;
        }
        return getString();
    }
    
    public default @Nullable String getNullableShortString() {
        if (isNull()) {
            return null;
        }
        return getShortString();
    }
    
    public default @Nullable String getNullableSuperShortString() {
        if (isNull()) {
            return null;
        }
        return getSuperShortString();
    }
    
    public default void putNullableString(final @Nullable String s) {
        if (s == null) {
            putNull();
        } else {
            putString(s);
        }
    }
    
    public default void putNullableShortString(final @Nullable String s) {
        if (s == null) {
            putNull();
        } else {
            putShortString(s);
        }
    }
    
    public default void putNullableSuperShortString(final @Nullable String s) {
        if (s == null) {
            putNull();
        } else {
            putSuperShortString(s);
        }
    }
    
    public default @NotNull Instant getInstantMillis() {
        return Instant.ofEpochMilli(getLong());
    }
    
    public default void putInstantMillis(final @NotNull Instant instant) {
        putLong(instant.toEpochMilli());
    }
    
    public default @Nullable Instant getNullableInstantMillis() {
        if (isNull()) {
            return null;
        }
        return getInstantMillis();
    }
    
    public default void putNullableInstantMillis(final @Nullable Instant instant) {
        if (instant == null) {
            putNull();
        } else {
            putInstantMillis(instant);
        }
    }
    
    // end
    
    public @NotNull UnsafeBuffer duplicate();
    
}