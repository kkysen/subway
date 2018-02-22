package sen.khyber.io;

import sen.khyber.unsafe.fields.ByteBufferUtils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Path;

import net.jpountz.lz4.LZ4Compressor;

/**
 * @author Khyber Sen
 */
public interface FastSerializable {
    
    public int serializedLength();
    
    public void serialize(ByteBuffer out);
    
    private ByteBuffer serialize(final boolean direct) {
        final int length = serializedLength();
        final ByteBuffer out = direct 
                ? ByteBuffer.allocateDirect(length)
                : ByteBuffer.allocate(length);
        ByteBufferUtils.useNativeOrder(out);
        serialize(out);
        return out;
    }
    
    public default ByteBuffer serialize() {
        return serialize(false);
    }
    
    public default ByteBuffer serializeDirect() {
        return serialize(true);
    }
    
    public default void serialize(final Path path) throws IOException {
        final ByteBuffer out = IO.mmap(path, serializedLength());
        serialize(out);
        ByteBufferUtils.free(out);
    }
    
    public default void serializeUnknownSize(final Path path, final int overestimate)
            throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "rw");
                FileChannel channel = raf.getChannel()) {
            final ByteBuffer out = channel.map(MapMode.READ_WRITE, 0, overestimate);
            serialize(out);
            final int newSize = out.position();
            ByteBufferUtils.free(out);
            channel.truncate(newSize);
        }
    }
    
    public default void serializeTruncated(final Path path) throws IOException {
        serializeUnknownSize(path, serializedLength());
    }
    
    public default void serializeCompressedUnknownSize(final Path path, final int overestimate)
            throws IOException {
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
    
    public default void serializeCompressedTruncated(final Path path) throws IOException {
        serializeCompressedUnknownSize(path, serializedLength());
    }
    
}