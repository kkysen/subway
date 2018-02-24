package sen.khyber.unsafe.fields;

import sen.khyber.unsafe.UnsafeUtils;
import sen.khyber.unsafe.reflect.ReflectedClass;
import sen.khyber.unsafe.reflect.ReflectedField;
import sen.khyber.unsafe.reflect.Reflectors;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.jetbrains.annotations.NotNull;

import sun.misc.Unsafe;


/**
 * Created by Khyber Sen on 1/29/2018.
 *
 * @author Khyber Sen
 */
public final class ByteBufferUtils {
    
    private ByteBufferUtils() {}
    
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
    
}