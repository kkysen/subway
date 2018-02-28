package sen.khyber.unsafe.buffers;

import sen.khyber.unsafe.buffers.LongFileChannel.Unmapper;

import lombok.experimental.Accessors;

import org.jetbrains.annotations.NotNull;

/**
 * @author Khyber Sen
 */
@SuppressWarnings("CloneableClassInSecureContext")
@Accessors(fluent = true)
public final class UnsafeMappedBuffer extends UnsafeDirectBuffer {
    
    private final @NotNull Unmapper unmapper;
    
    public UnsafeMappedBuffer(final long address, final long size,
            final @NotNull Unmapper unmapper) {
        super(address, size);
        this.unmapper = unmapper;
    }
    
    @Override
    public final boolean isMapped() {
        return true;
    }
    
    @Override
    protected final void freeInternal() {
        unmapper.unmap();
    }
    
    @Override
    public final @NotNull UnsafeMappedBuffer duplicate() {
        return (UnsafeMappedBuffer) super.duplicate();
    }
    
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public final @NotNull UnsafeMappedBuffer clone() {
        return duplicate();
    }
    
    @Override
    public final @NotNull UnsafeMappedBuffer slice(final long offset, final long length) {
        return new UnsafeMappedBuffer(address + offset, length, unmapper);
    }
    
    @Override
    public final @NotNull UnsafeMappedBuffer slice() {
        return (UnsafeMappedBuffer) super.slice();
    }
    
}
