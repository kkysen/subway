package sen.khyber.unsafe.fields;

import sen.khyber.unsafe.UnsafeUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import sun.misc.Unsafe;


/**
 * Created by Khyber Sen on 1/29/2018.
 *
 * @author Khyber Sen
 */
public final class ByteBufferUtils {
    
    private ByteBufferUtils() {}
    
    private static final Unsafe unsafe = UnsafeUtils.getUnsafe();
    
    private static final ByteOrder NON_NATIVE_ORDER =
            ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN
                    ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
    
    public static ByteBuffer useNativeOrder(final ByteBuffer buffer) {
        return buffer.order(ByteOrder.nativeOrder());
    }
    
    public static ByteBuffer useNonNativeOrder(final ByteBuffer buffer) {
        return buffer.order(NON_NATIVE_ORDER);
    }
    
    /**
     * Explicitly destroys/cleans the buffer.
     *
     * @param buffer DirectBuffer to be destroyed
     * @return true if the buffer was direct and it was destroyed/cleaned
     */
    public static boolean free(final ByteBuffer buffer) {
        if (buffer == null) {
            return true;
        }
        if (!buffer.isDirect()) {
            return false;
        }
        unsafe.invokeCleaner(buffer);
        return true;
    }
    
}