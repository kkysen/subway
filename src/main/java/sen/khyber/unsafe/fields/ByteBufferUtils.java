package sen.khyber.unsafe.fields;

import sen.khyber.io.SizeType;
import sen.khyber.unsafe.UnsafeUtils;
import sen.khyber.unsafe.buffers.UnsafeSerializable;
import sen.khyber.unsafe.reflect.ReflectedClass;
import sen.khyber.unsafe.reflect.ReflectedField;
import sen.khyber.unsafe.reflect.Reflectors;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import sun.misc.Unsafe;

import static sen.khyber.unsafe.fields.StringUtils.coder;
import static sen.khyber.unsafe.fields.StringUtils.getRawValue;


/**
 * Created by Khyber Sen on 1/29/2018.
 *
 * @author Khyber Sen
 */
public final class ByteBufferUtils {
    
    private ByteBufferUtils() {
    }
    
    private static final @NotNull Unsafe unsafe = UnsafeUtils.getUnsafe();
    
    private static final @NotNull ByteOrder NON_NATIVE_ORDER =
            ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN
                    ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
    
    private static final @NotNull ReflectedClass<Buffer> BufferClass =
            Reflectors.main().get(Buffer.class);
    
    private static final @NotNull ReflectedField addressField =
            BufferClass.fieldUnchecked("address");
    private static final @NotNull ReflectedField capacityField =
            BufferClass.fieldUnchecked("capacity");
    
    public static final ByteBuffer useNativeOrder(final ByteBuffer buffer) {
        return buffer.order(ByteOrder.nativeOrder());
    }
    
    public static final ByteBuffer useNonNativeOrder(final ByteBuffer buffer) {
        return buffer.order(NON_NATIVE_ORDER);
    }
    
    /**
     * Explicitly destroys/cleans the buffer.
     *
     * @param buffer DirectBuffer to be destroyed
     * @return true if the buffer was direct and it was destroyed/cleaned
     */
    public static final boolean free(final ByteBuffer buffer) {
        if (buffer == null) {
            return true;
        }
        if (!buffer.isDirect()) {
            return false;
        }
        unsafe.invokeCleaner(buffer);
        return true;
    }
    
    public static final long getAddress(final @NotNull Buffer buffer) {
        return addressField.getLong(buffer);
    }
    
    public static final void setAddress(final @NotNull Buffer buffer, final long address) {
        addressField.setLong(buffer, address);
    }
    
    public static final void setCapacity(final @NotNull Buffer buffer, final long capacity) {
        capacityField.setLong(buffer, capacity);
    }
    
    public static final void putUnsignedByte(final @NotNull ByteBuffer buffer, final int value) {
        assert value < (1 << Byte.SIZE);
        buffer.put((byte) (value + Byte.MIN_VALUE));
    }
    
    public static final int getUnsignedByte(final @NotNull ByteBuffer buffer) {
        return buffer.get() - Byte.MIN_VALUE;
    }
    
    public static final <T> @NotNull T get(final @NotNull ByteBuffer buffer,
                                           final @NotNull Function<ByteBuffer, T> deserializer) {
        return deserializer.apply(buffer);
    }
    
    public static final <T> @Nullable T getNullable(final @NotNull ByteBuffer buffer,
                                                    final @NotNull Function<ByteBuffer, T> deserializer) {
        if (isNull(buffer)) {
            return null;
        }
        return get(buffer, deserializer);
    }
    
    public static final <T, U> @NotNull T get(final @NotNull ByteBuffer buffer,
                                              final @NotNull BiFunction<ByteBuffer, U, T> deserializer,
                                              final U context) {
        return deserializer.apply(buffer, context);
    }
    
    public static final <T, U> @Nullable T getNullable(final @NotNull ByteBuffer buffer,
                                                       final @NotNull BiFunction<ByteBuffer, U, T> deserializer, final U context) {
        if (isNull(buffer)) {
            return null;
        }
        return get(buffer, deserializer, context);
    }
    
    public static final void put(final @NotNull ByteBuffer buffer,
                                 final @NotNull UnsafeSerializable serializable) {
        serializable.serialize(buffer);
    }
    
    public static final void putNullable(final @NotNull ByteBuffer buffer,
                                         final @Nullable UnsafeSerializable serializable) {
        if (serializable == null) {
            putNull(buffer);
        } else {
            put(buffer, serializable);
        }
    }
    
    public static final byte NULL_BYTE = -1;
    
    public static final void putNull(final @NotNull ByteBuffer buffer) {
        buffer.put(NULL_BYTE);
    }
    
    // advance position only if isNull
    public static final boolean isNull(final @NotNull ByteBuffer buffer) {
        if (buffer.get(buffer.position()) == NULL_BYTE) {
            buffer.get();
            return true;
        }
        return false;
    }
    
    private static @NotNull String getString(final @NotNull ByteBuffer buffer,
                                             final int encodedLength) {
        final int length = StringUtils.decodeLength(encodedLength);
        final byte coder = StringUtils.decodeCoder(encodedLength);
        final byte[] value = new byte[length];
        buffer.get(value);
        return StringUtils.ofRawValue(value, coder);
    }
    
    public static final @NotNull String getString(final @NotNull ByteBuffer buffer) {
        return getString(buffer, buffer.getInt());
    }
    
    public static final @NotNull String getShortString(final @NotNull ByteBuffer buffer) {
        return getString(buffer, buffer.getShort());
    }
    
    public static final @NotNull String getSuperShortString(final @NotNull ByteBuffer buffer) {
        return getString(buffer, buffer.get());
    }
    
    private static void putString(final @NotNull ByteBuffer buffer, final @NotNull String s,
                                  final @NotNull SizeType sizeType) {
        assert sizeType.isSigned();
        final byte coder = coder(s);
        final byte[] value = getRawValue(s);
        final int length = value.length;
        final int encodedLength = StringUtils.encodeLength(length, coder);
        sizeType.serialize(buffer, encodedLength);
        buffer.put(value);
    }
    
    public static final void putString(final @NotNull ByteBuffer buffer, final @NotNull String s) {
        putString(buffer, s, SizeType.INT);
    }
    
    public static final void putShortString(final @NotNull ByteBuffer buffer,
                                            final @NotNull String s) {
        putString(buffer, s, SizeType.SHORT);
    }
    
    public static final void putSuperShortString(final @NotNull ByteBuffer buffer,
                                                 final @NotNull String s) {
        putString(buffer, s, SizeType.BYTE);
    }
    
    public static final @Nullable String getNullableString(final @NotNull ByteBuffer buffer) {
        if (isNull(buffer)) {
            return null;
        }
        return getString(buffer);
    }
    
    public static final @Nullable String getNullableShortString(final @NotNull ByteBuffer buffer) {
        if (isNull(buffer)) {
            return null;
        }
        return getShortString(buffer);
    }
    
    public static final @Nullable String getNullableSuperShortString(
            final @NotNull ByteBuffer buffer) {
        if (isNull(buffer)) {
            return null;
        }
        return getSuperShortString(buffer);
    }
    
    public static final void putNullableString(final @NotNull ByteBuffer buffer,
                                               final @Nullable String s) {
        if (s == null) {
            putNull(buffer);
        } else {
            putString(buffer, s);
        }
    }
    
    public static final void putNullableShortString(final @NotNull ByteBuffer buffer,
                                                    final @Nullable String s) {
        if (s == null) {
            putNull(buffer);
        } else {
            putShortString(buffer, s);
        }
    }
    
    public static final void putNullableSuperShortString(final @NotNull ByteBuffer buffer,
                                                         final @Nullable String s) {
        if (s == null) {
            putNull(buffer);
        } else {
            putSuperShortString(buffer, s);
        }
    }
    
    public static final @NotNull Instant getInstantMillis(final @NotNull ByteBuffer buffer) {
        return Instant.ofEpochMilli(buffer.getLong());
    }
    
    public static final void putInstantMillis(final @NotNull ByteBuffer buffer,
                                              final @NotNull Instant instant) {
        buffer.putLong(instant.toEpochMilli());
    }
    
    public static final @Nullable Instant getNullableInstantMillis(
            final @NotNull ByteBuffer buffer) {
        if (isNull(buffer)) {
            return null;
        }
        return getInstantMillis(buffer);
    }
    
    public static final void putNullableInstantMillis(final @NotNull ByteBuffer buffer,
                                                      final @Nullable Instant instant) {
        if (instant == null) {
            putNull(buffer);
        } else {
            putInstantMillis(buffer, instant);
        }
    }
    
}