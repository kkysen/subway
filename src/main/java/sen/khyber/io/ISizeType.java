package sen.khyber.io;

import sen.khyber.unsafe.buffers.UnsafeBuffer;

import java.nio.ByteBuffer;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/24/2018.
 *
 * @author Khyber Sen
 */
public interface ISizeType {
    
    public int numBytes();
    
    public long maxValue();
    
    public void serialize(final @NotNull ByteBuffer buffer, long value);
    
    public void serialize(final @NotNull UnsafeBuffer buffer, long value);
    
    public long deserialize(final @NotNull ByteBuffer buffer);
    
    public long deserialize(final @NotNull UnsafeBuffer buffer);
    
    public default int deserializeOrdinal(final @NotNull ByteBuffer buffer) {
        return (int) deserialize(buffer);
    }
    
    public default int deserializeOrdinal(final @NotNull UnsafeBuffer buffer) {
        return (int) deserialize(buffer);
    }
    
}