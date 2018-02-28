package sen.khyber.unsafe.buffers;

import sen.khyber.unsafe.fields.ByteBufferUtils;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

import org.jetbrains.annotations.NotNull;

/**
 * @author Khyber Sen
 */
@SuppressWarnings({"restriction", "CloneableClassInSecureContext"})
@Accessors(fluent = true)
public final class UnsafeWrappedBuffer extends UnsafeDirectBuffer {
    
    /**
     * @param buffer a DirectBuffer (non-direct buffers will only be caught with
     *               assertions on)
     * @return the address of the DirectBuffer
     */
    public static long getAddress(final @NotNull Buffer buffer) {
        assert buffer.isDirect();
        return ByteBufferUtils.getAddress(buffer);
    }
    
    private final @Getter @NotNull ByteBuffer wrapped;
    
    private final boolean isMapped;
    
    private UnsafeWrappedBuffer(final long address, final long size,
            final @NotNull ByteBuffer buffer) {
        super(address, size);
        wrapped = buffer;
        isMapped = buffer instanceof MappedByteBuffer;
    }
    
    public UnsafeWrappedBuffer(final @NotNull ByteBuffer buffer) {
        this(getAddress(buffer), buffer.capacity(), buffer);
    }
    
    @Override
    public final boolean isWrapping() {
        return true;
    }
    
    @Override
    public final boolean isMapped() {
        return isMapped;
    }
    
    @Override
    protected final void freeInternal() {
        ByteBufferUtils.free(wrapped);
    }
    
    @Override
    public final @NotNull UnsafeWrappedBuffer duplicate() {
        return (UnsafeWrappedBuffer) super.duplicate();
    }
    
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public final @NotNull UnsafeWrappedBuffer clone() {
        return duplicate();
    }
    
    @Override
    public final @NotNull UnsafeWrappedBuffer slice(final long offset, final long length) {
        return new UnsafeWrappedBuffer(address + offset, length, wrapped);
    }
    
    @Override
    public final @NotNull UnsafeWrappedBuffer slice() {
        return (UnsafeWrappedBuffer) super.slice();
    }
    
}
