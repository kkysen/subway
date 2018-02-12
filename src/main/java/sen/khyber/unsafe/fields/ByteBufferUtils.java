package sen.khyber.unsafe.fields;

import sen.khyber.unsafe.UnsafeUtils;
import sen.khyber.unsafe.reflect.Reflectors;
import sen.khyber.util.exceptions.ExceptionUtils;

import java.lang.invoke.MethodHandle;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

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
    
    private static final MethodHandle directBufferCleanerMethod;
    private static final MethodHandle internalCleanerCleanMethod;
    
    static {
        //noinspection ConstantConditions
        directBufferCleanerMethod =
                Reflectors.forClassName("sun.nio.ch.DirectBuffer").method("cleaner").handle();
        //noinspection ConstantConditions
        internalCleanerCleanMethod =
                Reflectors.forClassName("jdk.internal.ref.Cleaner").method("clean").handle();
        Objects.requireNonNull(directBufferCleanerMethod);
        Objects.requireNonNull(internalCleanerCleanMethod);
    }
    
    private static void cleanDirectBuffer(final ByteBuffer buffer) {
        unsafe.invokeCleaner(buffer);
    }
    
    private static void cleanDirectBuffer(final Buffer buffer) {
        if (!buffer.isDirect()) {
            return;
        }
        try {
            internalCleanerCleanMethod.invoke(directBufferCleanerMethod.invoke(buffer));
        } catch (final Throwable e) {
            throw ExceptionUtils.atRuntime(e);
        }
    }
    
    /**
     * Explicitly destroys/cleans the buffer.
     *
     * @param buffer DirectBuffer to be destroyed
     * @return true if the buffer was direct and it was destroyed/cleaned
     */
    public static boolean free(final Buffer buffer) {
        if (buffer == null) {
            return true;
        }
        if (!buffer.isDirect()) {
            return false;
        }
        if (buffer instanceof ByteBuffer) {
            cleanDirectBuffer((ByteBuffer) buffer);
        } else {
            cleanDirectBuffer(buffer);
        }
        return true;
    }
    
}