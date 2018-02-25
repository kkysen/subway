package sen.khyber.io;

import sen.khyber.unsafe.fields.ByteBufferUtils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Path;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import net.jpountz.lz4.LZ4Compressor;

/**
 * @author Khyber Sen
 */
public interface FastSerializable {
    
    public int serializedLength();
    
    public void serialize(@NotNull ByteBuffer out);
    
    private @NotNull ByteBuffer serialize(final boolean direct) {
        final int length = serializedLength();
        final ByteBuffer out = direct
                ? ByteBuffer.allocateDirect(length)
                : ByteBuffer.allocate(length);
        ByteBufferUtils.useNativeOrder(out);
        serialize(out);
        return out;
    }
    
    public default @NotNull ByteBuffer serialize() {
        return serialize(false);
    }
    
    public default @NotNull ByteBuffer serializeDirect() {
        return serialize(true);
    }
    
    public default void serialize(final @NotNull Path path) throws IOException {
        Objects.requireNonNull(path);
        final ByteBuffer out = IO.mmap(path, serializedLength());
        serialize(out);
        ByteBufferUtils.free(out);
    }
    
    public default void serializeUnknownSize(final @NotNull Path path, final int overestimate)
            throws IOException {
        Objects.requireNonNull(path);
        try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "rw");
                FileChannel channel = raf.getChannel()) {
            final ByteBuffer out = channel.map(MapMode.READ_WRITE, 0, overestimate);
            serialize(out);
            final int newSize = out.position();
            ByteBufferUtils.free(out);
            channel.truncate(newSize);
        }
    }
    
    public default void serializeTruncated(final @NotNull Path path) throws IOException {
        Objects.requireNonNull(path);
        serializeUnknownSize(path, serializedLength());
    }
    
    public default void serializeCompressedUnknownSize(final @NotNull Path path,
            final int overestimate)
            throws IOException {
        Objects.requireNonNull(path);
        final ByteBuffer out = ByteBuffer.allocateDirect(overestimate)
                .order(ByteOrder.nativeOrder());
        serialize(out);
        out.limit(out.position());
        out.rewind();
        final LZ4Compressor compressor = LZ4.compressor();
        final int maxCompressedSize = compressor.maxCompressedLength(out.limit());
        try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "rw");
                FileChannel channel = raf.getChannel()) {
            final ByteBuffer compressed = channel.map(MapMode.READ_WRITE, 0,
                    maxCompressedSize + Integer.BYTES);
            compressed.order(ByteOrder.nativeOrder());
            compressed.putInt(out.remaining()); // original length not compressed
            compressor.compress(out, compressed);
            final int newSize = compressed.position();
            ByteBufferUtils.free(out);
            ByteBufferUtils.free(compressed);
            channel.truncate(newSize);
        }
    }
    
    public default void serializeCompressedTruncated(final @NotNull Path path) throws IOException {
        Objects.requireNonNull(path);
        serializeCompressedUnknownSize(path, serializedLength());
    }
    
}