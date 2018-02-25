package sen.khyber.io;

import sen.khyber.unsafe.buffers.UnsafeBuffer;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Comparator;

import org.jetbrains.annotations.NotNull;

import static sen.khyber.unsafe.fields.ByteBufferUtils.getUnsignedByte;
import static sen.khyber.unsafe.fields.ByteBufferUtils.putUnsignedByte;

/**
 * Created by Khyber Sen on 2/24/2018.
 *
 * @author Khyber Sen
 */
@Accessors(fluent = true)
public enum SizeType implements ISizeType {
    
    BYTE(Byte.BYTES, true) {
        //
        @Override
        public void serialize(final @NotNull ByteBuffer buffer, final long value) {
            assert check(value);
            buffer.put((byte) value);
        }
        
        @Override
        public void serialize(final @NotNull UnsafeBuffer buffer, final long value) {
            assert check(value);
            buffer.putByte((byte) value);
        }
        
        @Override
        public long deserialize(final @NotNull ByteBuffer buffer) {
            return buffer.get();
        }
        
        @Override
        public long deserialize(final @NotNull UnsafeBuffer buffer) {
            return buffer.getByte();
        }
        
    },
    
    UBYTE(Byte.BYTES, false) {
        //
        @Override
        public void serialize(final @NotNull ByteBuffer buffer, final long value) {
            assert check(value);
            putUnsignedByte(buffer, (byte) value);
        }
        
        @Override
        public void serialize(final @NotNull UnsafeBuffer buffer, final long value) {
            assert check(value);
            buffer.putUnsignedByte((byte) value);
        }
        
        @Override
        public long deserialize(final @NotNull ByteBuffer buffer) {
            return getUnsignedByte(buffer);
        }
        
        @Override
        public long deserialize(final @NotNull UnsafeBuffer buffer) {
            return buffer.getUnsignedByte();
        }
        
    },
    
    SHORT(Short.BYTES, true) {
        //
        @Override
        public void serialize(final @NotNull ByteBuffer buffer, final long value) {
            assert check(value);
            buffer.putShort((short) value);
        }
        
        @Override
        public void serialize(final @NotNull UnsafeBuffer buffer, final long value) {
            assert check(value);
            buffer.putShort((short) value);
        }
        
        @Override
        public long deserialize(final @NotNull ByteBuffer buffer) {
            return buffer.getShort();
        }
        
        @Override
        public long deserialize(final @NotNull UnsafeBuffer buffer) {
            return buffer.getShort();
        }
        
    },
    
    CHAR(Character.BYTES, false) {
        //
        @Override
        public void serialize(final @NotNull ByteBuffer buffer, final long value) {
            assert check(value);
            buffer.putChar((char) value);
        }
        
        @Override
        public void serialize(final @NotNull UnsafeBuffer buffer, final long value) {
            assert check(value);
            buffer.putChar((char) value);
        }
        
        @Override
        public long deserialize(final @NotNull ByteBuffer buffer) {
            return buffer.getChar();
        }
        
        @Override
        public long deserialize(final @NotNull UnsafeBuffer buffer) {
            return buffer.getChar();
        }
        
    },
    
    INT(Integer.BYTES, true) {
        //
        @Override
        public void serialize(final @NotNull ByteBuffer buffer, final long value) {
            assert check(value);
            buffer.putInt((int) value);
        }
        
        @Override
        public void serialize(final @NotNull UnsafeBuffer buffer, final long value) {
            assert check(value);
            buffer.putInt((int) value);
        }
        
        @Override
        public long deserialize(final @NotNull ByteBuffer buffer) {
            return buffer.getInt();
        }
        
        @Override
        public long deserialize(final @NotNull UnsafeBuffer buffer) {
            return buffer.getInt();
        }
        
    },
    //        
    //        UINT(Integer.BYTES, false) {
    //            
    //        },
    
    LONG(Long.BYTES, true) {
        //
        @Override
        public void serialize(final @NotNull ByteBuffer buffer, final long value) {
            assert check(value);
            buffer.putLong(value);
        }
        
        @Override
        public void serialize(final @NotNull UnsafeBuffer buffer, final long value) {
            assert check(value);
            buffer.putLong(value);
        }
        
        @Override
        public long deserialize(final @NotNull ByteBuffer buffer) {
            return buffer.getLong();
        }
        
        @Override
        public long deserialize(final @NotNull UnsafeBuffer buffer) {
            return buffer.getLong();
        }
        
    },
    
    //
    ;
    
    private final @Getter boolean isSigned;
    private final @Getter int numBytes;
    private final @Getter long maxValue;
    private final @Getter long minValue;
    
    private SizeType(final int numBytes, final boolean signed) {
        assert numBytes >= 1 && numBytes <= Long.BYTES;
        isSigned = signed;
        this.numBytes = numBytes;
        final int signedShift = signed ? 1 : 0;
        maxValue = (1 << (numBytes - signedShift)) - 1;
        minValue = signed ? -maxValue - 1 : 0;
    }
    
    public final boolean check(final long value) {
        return value >= minValue && value <= maxValue;
    }
    
    private static final long[] maxValues = new long[values().length];
    
    static {
        final SizeType[] values = values();
        Arrays.sort(values, Comparator.comparing(SizeType::maxValue));
        for (int i = 0; i < values.length; i++) {
            maxValues[i] = values[i].maxValue;
        }
    }
    
    public static @NotNull SizeType map(final long size) {
        int i = Arrays.binarySearch(maxValues, size);
        if (i < 0) {
            i = -i - 1; // TODO check
        }
        return values()[i];
    }
    
}