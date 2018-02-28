package sen.khyber.unsafe.buffers;

import sen.khyber.unsafe.UnsafeUtils;
import sen.khyber.unsafe.gc.PostGC;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import sun.misc.Unsafe;

/**
 * @author Khyber Sen
 */
@SuppressWarnings({"restriction", "CloneableClassInSecureContext"})
public final class UnsafeHeapBuffer extends AbstractUnsafeBuffer {
    
    private static final List<UnsafeHeapBuffer> buffers = new ArrayList<>();
    
    static {
        PostGC.runAfterGC(() -> {
            // TODO synchronize
            for (final UnsafeHeapBuffer buffer : buffers) {
                buffer.readdress();
                // GC may move backing arrays,
                // so address must be refreshed
            }
        });
    }
    
    private final @NotNull Object array;
    private final int arrayOffset;
    private final @NotNull Object[] reference;
    
    private final long offset;
    private volatile long address; // may change
    
    private UnsafeHeapBuffer(final long size, final @NotNull Object array) {
        super(size);
        this.array = array;
        arrayOffset = unsafe.arrayBaseOffset(array.getClass());
        reference = new Object[] {array};
        offset = 0;
        readdress();
        buffers.add(this);
    }
    
    private UnsafeHeapBuffer(final long size, final @NotNull Object array, final int arrayOffset,
            final @NotNull Object[] reference, final long address, final long offset) {
        super(size);
        this.array = array;
        this.arrayOffset = arrayOffset;
        this.reference = reference;
        this.address = address;
        this.offset = offset;
        buffers.add(this);
    }
    
    private long arrayAddress() {
        /*
         * This addressOf function is slow,
         * so while UnsafeHeapBuffer can still function
         * using this address() implementation,
         * for better performance,
         * most other methods using address()
         * and single-register addressing
         * through unsafe.get<E>(long address)
         * will be changed to use double-register addressing
         * through unsafe.get<E>(Object o, long offset)
         */
        switch (UnsafeUtils.OOP_SIZE) {
            case Integer.BYTES:
                return unsafe.getInt(reference, (long) Unsafe.ARRAY_INT_BASE_OFFSET);
            case Long.BYTES:
                return unsafe.getLong(reference, (long) Unsafe.ARRAY_LONG_BASE_OFFSET);
            default:
                throw new AssertionError("impossible");
        }
    }
    
    private void readdress() {
        address = arrayAddress() + arrayOffset + offset;
    }
    
    @Override
    public final long address() {
        return address;
    }
    
    @Override
    protected final void freeInternal() {
        address = UnsafeUtils.NULLPTR;
        //noinspection ConstantConditions
        reference[0] = null;
    }
    
    // TODO implement alternate methods using unsafe.get<E>(Object o, long offset)
    // may still be more performant and safer,
    // but now the address is saved and only updated after a GC,
    // during which the array might be moved.
    
    @Override
    public final boolean isDirect() {
        return false;
    }
    
    @Override
    public final @NotNull UnsafeHeapBuffer duplicate() {
        return slice(0, size);
    }
    
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public final @NotNull UnsafeHeapBuffer clone() {
        return duplicate();
    }
    
    @Override
    public final @NotNull UnsafeHeapBuffer slice(final long offset, final long length) {
        return new UnsafeHeapBuffer(length, array, arrayOffset, reference, address,
                this.offset + offset);
    }
    
    @Override
    public final @NotNull UnsafeHeapBuffer slice() {
        return (UnsafeHeapBuffer) super.slice();
    }
    
}
