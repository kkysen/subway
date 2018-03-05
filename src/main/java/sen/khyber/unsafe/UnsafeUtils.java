package sen.khyber.unsafe;

import sen.khyber.util.exceptions.ExceptionUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import org.jetbrains.annotations.Nullable;

import sun.misc.Unsafe;

/**
 * Created by Khyber Sen on 1/30/2018.
 *
 * @author Khyber Sen
 */
public final class UnsafeUtils {
    
    private UnsafeUtils() {}
    
    private static native void registerNatives();
    
    static {
        //registerNatives();
    }
    
    public static native long getAddressNative(Object o);
    
    public static native Object getObjectNative(long address);
    
    public static native void pin(Object o);
    
    public static native void unpin(Object o);
    
    private static Unsafe reflectUnsafe() {
        if (System.getSecurityManager() == null) {
            final Field theUnsafe;
            try {
                theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            } catch (final NoSuchFieldException e) {
                throw ExceptionUtils.atRuntime(e);
            }
            theUnsafe.setAccessible(true);
            try {
                return (Unsafe) theUnsafe.get(null);
            } catch (final IllegalAccessException e) {
                throw ExceptionUtils.atRuntime(e);
            }
        } else {
            /*
             * use JNI to bypass security if JNI is still allowed
             *
             * long theUnsafeStaticOffset = COMPRESSED_OOPS ? 104 : 160;
             * I would use that, but that uses COMPRESSED_OOPS,
             * which uses Unsafe.ARRAY_OBJECT_INDEX_SCALE,
             * which will be blocked.
             *
             * Therefore, either I used native code,
             * or I try both offsets. TODO FIXME
             */
            final long theUnsafeStaticOffset = COMPRESSED_OOPS ? 104 : 160;
            final long address = getAddressNative(Unsafe.class) + theUnsafeStaticOffset;
            return (Unsafe) getObjectNative(address);
        }
    }
    
    private static final Unsafe unsafe = reflectUnsafe();
    
    public static Unsafe getUnsafe() {
        return unsafe;
    }
    
    public static final int OOP_SIZE = Unsafe.ARRAY_OBJECT_INDEX_SCALE;
    
    public static final boolean COMPRESSED_OOPS;
    
    static {
        switch (OOP_SIZE) {
            case Integer.BYTES:
                COMPRESSED_OOPS = true;
                break;
            case Long.BYTES:
                COMPRESSED_OOPS = false;
                break;
            default:
                throw new Error("impossible");
        }
    }
    
    public static final int OBJECT_OFFSET = COMPRESSED_OOPS ? 16 : 24;
    public static final int ARRAY_OFFSET = OBJECT_OFFSET;
    
    // TODO maybe used WeakReference instead
    // (but there are other problems with that)
    
    /**
     * Used to place objects into so that they can be addressed
     * When using compressed oops, it is easier to address a long and
     * take only the uint part of it (a simple & ~0 in little endian).
     * But this will read the memory beyond the end of a length=1 array,
     * so this array is made length=2.
     * <p>
     * *Note: Little-endianness is assumed,
     * but most CPUs are little endian now.
     */
    private static final Object[] REFERENCE = new Object[COMPRESSED_OOPS ? 2 : 1];
    
    //    private static long normalize(final int compressedAddress) {
    //        return (compressedAddress >= 0 ? compressedAddress
    //                : ~0L >>> Integer.SIZE & compressedAddress) << 3;
    //    }
    
    public static final long getObjectFieldAddress(final Object o, final long offset) {
        long address = unsafe.getLong(o, offset);
        if (COMPRESSED_OOPS) {
            /*
             * The last 3 bits are unused b/c objects are 8-byte aligned,
             * so it is in a sense 35-bit addresses.
             * Therefore, you need to bit-shift by 3.
             */
            address = address << 32 >>> 32 << 3;
        }
        return address;
    }
    
    public static final void putObjectFieldAddress(final Object o, final long offset,
            long address) {
        if (COMPRESSED_OOPS) {
            /*
             * The last 3 bits are unused b/c objects are 8-byte aligned,
             * so it is in a sense 35-bit addresses.
             * Therefore, you need to bit-shift by 3.
             */
            address >>= 3;
        }
        unsafe.putInt(o, offset, (int) address);
    }
    
    public static final long rawAddress(final @Nullable Object o) {
        final Object[] array = REFERENCE;
        array[0] = o;
        final long address = getObjectFieldAddress(array, (long) ARRAY_OFFSET);
        array[0] = null;
        return address;
    }
    
    public static final Object objectAtAddress(final long address) {
        final Object[] array = REFERENCE;
        putObjectFieldAddress(array, (long) ARRAY_OFFSET, address);
        final Object o = array[0];
        array[0] = null;
        return o;
    }
    
    public static final long NULLPTR = 0;
    
    static {
        MemoryLayoutVerifier.verify();
        System.out.println("&unsafe: " + rawAddress(unsafe));
    }
    
    public static final void copyArray(final Object arraySrc, final Object arrayDest,
            final int srcLength) {
        assert srcLength > 0 && srcLength <= Array.getLength(arraySrc);
        assert srcLength * unsafe.arrayIndexScale(arraySrc.getClass()) <= Array.getLength(arrayDest)
                * unsafe.arrayIndexScale(arrayDest.getClass());
        unsafe.copyMemory(arraySrc, ARRAY_OFFSET, arrayDest, ARRAY_OFFSET,
                srcLength * unsafe.arrayIndexScale(arraySrc.getClass()));
    }
    
    public static final void setArrayLength(final Object array, final int newLength) {
        assert newLength >= 0;
        unsafe.putInt(array, (long) (ARRAY_OFFSET - Integer.BYTES), newLength);
    }
    
    private static long arraySize(final Object array, final Class<?> klass) {
        assert array.getClass().isArray();
        return ARRAY_OFFSET + Array.getLength(array) * unsafe.arrayIndexScale(klass);
    }
    
    public static final long arraySize(final Object array) {
        return arraySize(array, array.getClass());
    }
    
    public static final long objectSize(final Object o) {
        assert !o.getClass().isArray();
        final long unalignedAddress = unsafe.getLong(o, 4L);
        final long address = unalignedAddress << Integer.SIZE >>> Integer.SIZE << 3;
        return unsafe.getAddress(address + 12L);
        //return unsafe.getAddress(o.objectAddress(4L) + 3 * OOP_SIZE);
    }
    
    public static final long shallowSize(final Object o) {
        if (o == null) {
            return OOP_SIZE;
        }
        final Class<?> klass = o.getClass();
        if (klass.isArray()) {
            return arraySize(o, klass);
        }
        return objectSize(o);
    }
    
    public static final <T> void shallowCopy(final T src, final T dest) {
        assert src.getClass() == dest.getClass();
        final long size = shallowSize(src);
        unsafe.copyMemory(src, OBJECT_OFFSET, dest, OBJECT_OFFSET, size - OBJECT_OFFSET);
    }
    
    public static final <T> T allocateInstance(final Class<T> klass) {
        try {
            @SuppressWarnings("unchecked") final T t = (T) unsafe.allocateInstance(klass);
            return t;
        } catch (final InstantiationException e) {
            throw ExceptionUtils.atRuntime(e);
        }
    }
    
    public static final <T> T emptyCopy(final T t) {
        @SuppressWarnings("unchecked") final Class<? extends T> klass =
                (Class<? extends T>) t.getClass();
        return allocateInstance(klass);
    }
    
    public static final <T> T shallowCopy(final T t) {
        final T copy = emptyCopy(t);
        shallowCopy(t, copy);
        return copy;
    }
    
    // FIXME this method shouldn't be here
    // and it does weird things with endianness, b/c it's primarily for debugging
    @Deprecated
    public static final String longBits(final long L) {
        final StringBuilder sb = new StringBuilder(Long.SIZE);
        for (int i = Long.SIZE - 1; i >= 0; i--) {
            sb.append((L >> i & 1) == 1 ? '1' : '0');
        }
        return sb.toString();
    }
    
    public static final String objectHeaderBits(final Object o) {
        final StringBuilder sb = new StringBuilder(OOP_SIZE * Byte.SIZE);
        for (long i = 0; i < OBJECT_OFFSET; i += Integer.BYTES) {
            final long headerBits = unsafe.getLong(o, i) << 32 >>> 32;
            sb.append(longBits(headerBits).substring(Integer.SIZE));
        }
        return sb.toString();
    }
    
}