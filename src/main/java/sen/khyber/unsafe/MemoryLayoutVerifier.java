package sen.khyber.unsafe;

import sun.misc.Unsafe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by Khyber Sen on 1/30/2018.
 *
 * @author Khyber Sen
 */
public class MemoryLayoutVerifier {
    
    private MemoryLayoutVerifier() {}
    
    public static final Class<?>[] primitiveClasses = {
            boolean.class,
            byte.class,
            char.class,
            short.class,
            int.class,
            long.class,
            float.class,
            double.class,
    };
    
    public static final Class<?>[] boxedTypes = {
            Boolean.TYPE,
            Byte.TYPE,
            Character.TYPE,
            Short.TYPE,
            Integer.TYPE,
            Long.TYPE,
            Float.TYPE,
            Double.TYPE,
    };
    
    private static void verifyPrimitiveClasses() {
        for (int i = 0; i < boxedTypes.length; i++) {
            assertSame(boxedTypes[i], primitiveClasses[i]);
        }
    }
    
    private static void verifyArrayScales() {
        // Boolean excluded
        final int[] typeSizes = {
                Byte.BYTES,
                Character.BYTES,
                Short.BYTES,
                Integer.BYTES,
                Long.BYTES,
                Float.BYTES,
                Double.BYTES,
        };
        final int[] arrayScales = {
                Unsafe.ARRAY_BYTE_INDEX_SCALE,
                Unsafe.ARRAY_CHAR_INDEX_SCALE,
                Unsafe.ARRAY_SHORT_INDEX_SCALE,
                Unsafe.ARRAY_INT_INDEX_SCALE,
                Unsafe.ARRAY_LONG_INDEX_SCALE,
                Unsafe.ARRAY_FLOAT_INDEX_SCALE,
                Unsafe.ARRAY_DOUBLE_INDEX_SCALE,
        };
        for (int i = 0; i < typeSizes.length; i++) {
            assertEquals(typeSizes[i], arrayScales[i]);
        }
    }
    
    private static void verifyArrayOffsets() {
        final int[] arrayOffsets = {
                Unsafe.ARRAY_BOOLEAN_BASE_OFFSET,
                Unsafe.ARRAY_BYTE_BASE_OFFSET,
                Unsafe.ARRAY_CHAR_BASE_OFFSET,
                Unsafe.ARRAY_SHORT_BASE_OFFSET,
                Unsafe.ARRAY_INT_BASE_OFFSET,
                Unsafe.ARRAY_LONG_BASE_OFFSET,
                Unsafe.ARRAY_FLOAT_BASE_OFFSET,
                Unsafe.ARRAY_DOUBLE_BASE_OFFSET,
                Unsafe.ARRAY_OBJECT_BASE_OFFSET,
        };
        for (final int arrayOffset : arrayOffsets) {
            assertEquals(UnsafeUtils.ARRAY_OFFSET, arrayOffset);
        }
    }
    
    private static void verifyOopSize() {
        assertTrue(UnsafeUtils.OOP_SIZE == Integer.BYTES || UnsafeUtils.OOP_SIZE == Long.BYTES);
    }
    
    private static void verifyNullAddress() {
        assertEquals(0, UnsafeUtils.NULLPTR);
        assertEquals(UnsafeUtils.NULLPTR, UnsafeUtils.rawAddress(null));
    }
    
    public static void verify() {
        verifyPrimitiveClasses();
        verifyArrayScales();
        verifyArrayOffsets();
        verifyOopSize();
        verifyNullAddress();
    }
    
}