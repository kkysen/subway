package sen.khyber.unsafe.buffers;

import sen.khyber.unsafe.UnsafeUtils;

import org.jetbrains.annotations.NotNull;

import sun.misc.Unsafe;

/**
 * @author Khyber Sen
 */
@SuppressWarnings({"restriction", "CloneableClassInSecureContext", "OverlyComplexClass"})
public abstract class AbstractUnsafeBuffer implements UnsafeBuffer {
    
    protected static final @NotNull Unsafe unsafe = UnsafeUtils.getUnsafe();
    
    protected boolean freed = false;
    
    protected final long size;
    
    protected long pos = 0;
    protected long limit;
    
    protected AbstractUnsafeBuffer(final long size) {
        assert size >= 0;
        this.size = size;
        limit = size;
    }
    
    protected abstract void freeInternal();
    
    @Override
    public final void free() {
        if (!freed) {
            freeInternal();
            freed = true;
        }
    }
    
    @Override
    public final void close() {
        free();
    }
    
    @SuppressWarnings({"FinalizeNotProtected", "FinalizeDeclaration"})
    @Override
    public final void finalize() {
        free();
    }
    
    @Override
    public boolean isWrapping() {
        return false; // default
    }
    
    @Override
    public boolean isMapped() {
        return false; // default
    }
    
    @Override
    public final long size() {
        return size;
    }
    
    @Override
    public final long position() {
        return pos;
    }
    
    @Override
    public final long limit() {
        return limit;
    }
    
    @Override
    public final long remaining() {
        return limit - pos;
    }
    
    @Override
    public final boolean hasRemaining() {
        return pos < limit;
    }
    
    @Override
    public final long positionAddress() {
        return address() + pos;
    }
    
    @Override
    public final void position(final long newPosition) {
        pos = newPosition;
        assert pos >= 0 && pos < limit;
    }
    
    @Override
    public final void skip(final long skipSize) {
        pos += skipSize;
        assert pos >= 0 && pos < limit;
    }
    
    @Override
    public final void rewind() {
        pos = 0;
    }
    
    @Override
    public final void reset() {
        pos = 0;
        limit = size;
    }
    
    @Override
    public final void limit(final long newLimit) {
        limit = newLimit;
        assert limit >= pos && limit < size;
    }
    
    @Override
    public final void moveLimit(final long skipSize) {
        limit += skipSize;
        assert limit >= pos && limit < size;
    }
    
    @Override
    public final void setRemaining(final long newRemaining) {
        limit = pos + newRemaining;
        assert limit >= pos && limit < size;
    }
    
    private long a(final long index) {
        assert index >= 0 && index < limit;
        return address() + index;
    }
    
    @Override
    public final void getMemory(final long index, final long address, final long size) {
        assert size >= 0;
        unsafe.copyMemory(a(index), address, size);
    }
    
    @Override
    public final void putMemory(final long index, final long address, final long size) {
        assert size >= 0;
        unsafe.copyMemory(address, a(index), size);
    }
    
    @Override
    public final void getMemory(final long address, final long size) {
        assert size >= 0;
        unsafe.copyMemory(positionAddress(), address, size);
        pos += size;
        assert pos < limit;
    }
    
    @Override
    public final void putMemory(final long address, final long size) {
        assert size >= 0;
        unsafe.copyMemory(address, positionAddress(), size);
        pos += size;
        assert pos < limit;
    }
    
    private void memsetFill(final byte val, final long offset, final long length) {
        unsafe.setMemory(address() + offset, length, val);
    }
    
    /*
     * TODO
     * also add methods like byte[] asByteArray, long[] asLongArray
     * for heap buffer, this is easy, but for direct buffers,
     * either the 24 bytes before will be overwritten (dangerous),
     * or 24 extra bytes will be allocated for each buffer (better idea)
     * might be a problem for mmap buffers tho
     */
    
    protected void superwordFill(final byte val, final long offset, final long length) {
        // cast memory as a byte[] or long[] and fill it using loop (or Arrays.fill)
        // this takes advantage of the JVM superword optimization,
        // which vectorizes the loop using SIMD instructions
        // TODO
    }
    
    @Override
    public void fill(final byte val, final long offset, final long length) {
        assert offset >= 0;
        assert offset + length <= limit;
        memsetFill(val, offset, length);
    }
    
    @Override
    public final byte getByte(final long index) {
        return unsafe.getByte(a(index));
    }
    
    @Override
    public final char getChar(final long index) {
        return unsafe.getChar(a(index));
    }
    
    @Override
    public final short getShort(final long index) {
        return unsafe.getShort(a(index));
    }
    
    @Override
    public final int getInt(final long index) {
        return unsafe.getInt(a(index));
    }
    
    @Override
    public final long getLong(final long index) {
        return unsafe.getLong(a(index));
    }
    
    @Override
    public final float getFloat(final long index) {
        return unsafe.getFloat(a(index));
    }
    
    @Override
    public final double getDouble(final long index) {
        return unsafe.getDouble(a(index));
    }
    
    @Override
    public final void putByte(final long index, final byte b) {
        unsafe.putByte(a(index), b);
    }
    
    @Override
    public final void putChar(final long index, final char c) {
        unsafe.putChar(a(index), c);
    }
    
    @Override
    public final void putShort(final long index, final short s) {
        unsafe.putShort(a(index), s);
    }
    
    @Override
    public final void putInt(final long index, final int i) {
        unsafe.putInt(a(index), i);
    }
    
    @Override
    public final void putLong(final long index, final long L) {
        unsafe.putLong(a(index), L);
    }
    
    @Override
    public final void putFloat(final long index, final float f) {
        unsafe.putFloat(a(index), f);
    }
    
    @Override
    public final void putDouble(final long index, final double d) {
        unsafe.putDouble(a(index), d);
    }
    
    @Override
    public final byte getByte() {
        final byte b = getByte(pos);
        pos += Byte.BYTES;
        return b;
    }
    
    @Override
    public final char getChar() {
        final char c = getChar(pos);
        pos += Character.BYTES;
        return c;
    }
    
    @Override
    public final short getShort() {
        final short s = getShort(pos);
        pos += Short.BYTES;
        return s;
    }
    
    @Override
    public final int getInt() {
        final int i = getInt(pos);
        pos += Integer.BYTES;
        return i;
    }
    
    @Override
    public final long getLong() {
        final long L = getLong(pos);
        pos += Long.BYTES;
        return L;
    }
    
    @Override
    public final float getFloat() {
        final float f = getFloat(pos);
        pos += Float.BYTES;
        return f;
    }
    
    @Override
    public final double getDouble() {
        final double d = getDouble(pos);
        pos += Double.BYTES;
        return d;
    }
    
    @Override
    public final void putByte(final byte b) {
        putByte(pos, b);
        pos += Byte.BYTES;
    }
    
    @Override
    public final void putChar(final char c) {
        putChar(pos, c);
        pos += Character.BYTES;
    }
    
    @Override
    public final void putShort(final short s) {
        putShort(pos, s);
        pos += Short.BYTES;
    }
    
    @Override
    public final void putInt(final int i) {
        putInt(pos, i);
        pos += Integer.BYTES;
    }
    
    @Override
    public final void putLong(final long L) {
        putLong(pos, L);
        pos += Long.BYTES;
    }
    
    @Override
    public final void putFloat(final float f) {
        putFloat(pos, f);
        pos += Float.BYTES;
    }
    
    @Override
    public final void putDouble(final double d) {
        putDouble(pos, d);
        pos += Double.BYTES;
    }
    
    private void getArray(final long index, final @NotNull Object array, final int offset,
            final int length, final int indexScale) {
        assert offset >= 0;
        assert length >= 0;
        if (index + length * indexScale > limit) {
            final String s = "";
            System.out.print(s);
        }
        assert index + length * indexScale <= limit;
        getMemory(index,
                UnsafeUtils.rawAddress(array) + UnsafeUtils.ARRAY_OFFSET + offset * indexScale,
                length * indexScale);
    }
    
    private void putArray(final long index, final @NotNull Object array, final int offset,
            final int length, final int indexScale) {
        assert offset >= 0;
        assert length >= 0;
        assert index + length * indexScale <= limit;
        putMemory(index,
                UnsafeUtils.rawAddress(array) + UnsafeUtils.ARRAY_OFFSET + offset * indexScale,
                length * indexScale);
    }
    
    @Override
    public final void getBytes(final long index, final @NotNull byte[] array, final int offset,
            final int length) {
        assert offset <= array.length;
        assert offset + length <= array.length;
        getArray(index, array, offset, length, Byte.BYTES);
    }
    
    @Override
    public final void getChars(final long index, final @NotNull char[] array, final int offset,
            final int length) {
        assert offset <= array.length;
        assert offset + length <= array.length;
        getArray(index, array, offset, length, Character.BYTES);
    }
    
    @Override
    public final void getShorts(final long index, final @NotNull short[] array, final int offset,
            final int length) {
        assert offset <= array.length;
        assert offset + length <= array.length;
        getArray(index, array, offset, length, Short.BYTES);
    }
    
    @Override
    public final void getInts(final long index, final @NotNull int[] array, final int offset,
            final int length) {
        assert offset <= array.length;
        assert offset + length <= array.length;
        getArray(index, array, offset, length, Integer.BYTES);
    }
    
    @Override
    public final void getLongs(final long index, final @NotNull long[] array, final int offset,
            final int length) {
        assert offset <= array.length;
        assert offset + length <= array.length;
        getArray(index, array, offset, length, Long.BYTES);
    }
    
    @Override
    public final void getFloats(final long index, final @NotNull float[] array, final int offset,
            final int length) {
        assert offset <= array.length;
        assert offset + length <= array.length;
        getArray(index, array, offset, length, Float.BYTES);
    }
    
    @Override
    public final void getDoubles(final long index, final @NotNull double[] array, final int offset,
            final int length) {
        assert offset <= array.length;
        assert offset + length <= array.length;
        getArray(index, array, offset, length, Double.BYTES);
    }
    
    @Override
    public final void putBytes(final long index, final @NotNull byte[] array, final int offset,
            final int length) {
        assert offset <= array.length;
        assert offset + length <= array.length;
        putArray(index, array, offset, length, Byte.BYTES);
    }
    
    @Override
    public final void putChars(final long index, final @NotNull char[] array, final int offset,
            final int length) {
        assert offset <= array.length;
        assert offset + length <= array.length;
        putArray(index, array, offset, length, Character.BYTES);
    }
    
    @Override
    public final void putShorts(final long index, final @NotNull short[] array, final int offset,
            final int length) {
        assert offset <= array.length;
        assert offset + length <= array.length;
        putArray(index, array, offset, length, Short.BYTES);
    }
    
    @Override
    public final void putInts(final long index, final @NotNull int[] array, final int offset,
            final int length) {
        assert offset <= array.length;
        assert offset + length <= array.length;
        putArray(index, array, offset, length, Integer.BYTES);
    }
    
    @Override
    public final void putLongs(final long index, final @NotNull long[] array, final int offset,
            final int length) {
        assert offset <= array.length;
        assert offset + length <= array.length;
        putArray(index, array, offset, length, Long.BYTES);
    }
    
    @Override
    public final void putFloats(final long index, final @NotNull float[] array, final int offset,
            final int length) {
        assert offset <= array.length;
        assert offset + length <= array.length;
        putArray(index, array, offset, length, Float.BYTES);
    }
    
    @Override
    public final void putDoubles(final long index, final @NotNull double[] array, final int offset,
            final int length) {
        assert offset <= array.length;
        assert offset + length <= array.length;
        putArray(index, array, offset, length, Double.BYTES);
    }
    
    @Override
    public final void getBytes(final @NotNull byte[] array, final int offset, final int length) {
        getBytes(pos, array, offset, length);
        pos += length * Byte.BYTES;
    }
    
    @Override
    public final void getChars(final @NotNull char[] array, final int offset, final int length) {
        getChars(pos, array, offset, length);
        pos += length * Character.BYTES;
    }
    
    @Override
    public final void getShorts(final @NotNull short[] array, final int offset,
            final int length) {
        getShorts(pos, array, offset, length);
        pos += length * Short.BYTES;
    }
    
    @Override
    public final void getInts(final @NotNull int[] array, final int offset, final int length) {
        getInts(pos, array, offset, length);
        pos += length * Integer.BYTES;
    }
    
    @Override
    public final void getLongs(final @NotNull long[] array, final int offset, final int length) {
        getLongs(pos, array, offset, length);
        pos += length * Long.BYTES;
    }
    
    @Override
    public final void getFloats(final @NotNull float[] array, final int offset,
            final int length) {
        getFloats(pos, array, offset, length);
        pos += length * Float.BYTES;
    }
    
    @Override
    public final void getDoubles(final @NotNull double[] array, final int offset,
            final int length) {
        getDoubles(pos, array, offset, length);
        pos += length * Double.BYTES;
    }
    
    @Override
    public final void putBytes(final @NotNull byte[] array, final int offset, final int length) {
        putBytes(pos, array, offset, length);
        pos += length * Byte.BYTES;
    }
    
    @Override
    public final void putChars(final @NotNull char[] array, final int offset, final int length) {
        putChars(pos, array, offset, length);
        pos += length * Character.BYTES;
    }
    
    @Override
    public final void putShorts(final @NotNull short[] array, final int offset,
            final int length) {
        putShorts(pos, array, offset, length);
        pos += length * Short.BYTES;
    }
    
    @Override
    public final void putInts(final @NotNull int[] array, final int offset, final int length) {
        putInts(pos, array, offset, length);
        pos += length * Integer.BYTES;
    }
    
    @Override
    public final void putLongs(final @NotNull long[] array, final int offset, final int length) {
        putLongs(pos, array, offset, length);
        pos += length * Long.BYTES;
    }
    
    @Override
    public final void putFloats(final @NotNull float[] array, final int offset,
            final int length) {
        putFloats(pos, array, offset, length);
        pos += length * Float.BYTES;
    }
    
    @Override
    public final void putDoubles(final @NotNull double[] array, final int offset,
            final int length) {
        putDoubles(pos, array, offset, length);
        pos += length * Double.BYTES;
    }
    
    @Override
    public abstract @NotNull AbstractUnsafeBuffer duplicate();
    
    @SuppressWarnings({"MethodDoesntCallSuperMethod", "DesignForExtension", "NonFinalClone"})
    @Override
    public @NotNull AbstractUnsafeBuffer clone() {
        //noinspection OverriddenMethodCallDuringObjectConstruction
        return duplicate();
    }
    
    protected @NotNull String addressString() {
        return String.valueOf(address());
    }
    
    @Override
    public final @NotNull StringBuilder append(@NotNull final StringBuilder sb) {
        return sb.append("UnsafeBuffer @")
                .append(addressString())
                .append("[pos=")
                .append(pos)
                .append(", lim=")
                .append(limit)
                .append(", size=")
                .append(size)
                .append(']');
    }
    
    @Override
    public final @NotNull String toString() {
        return defaultToString();
    }
    
}
