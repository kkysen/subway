package sen.khyber.unsafe.buffers;

import sen.khyber.io.FastSerializable;
import sen.khyber.io.LZ4;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Path;
import java.util.Objects;

import net.jpountz.lz4.LZ4Compressor;

/**
 * @author Khyber Sen
 */
public interface UnsafeSerializable extends FastSerializable {
    
    public default long serializedLongLength() {
        return serializedLength();
    }
    
    public void serializeUnsafe(UnsafeBuffer out);
    
    @Override
    public default void serialize(final ByteBuffer out) {
        serializeUnsafe(UnsafeBuffer.wrap(out));
    }
    
    public default UnsafeBuffer serializeUnsafe() {
        final UnsafeBuffer out = UnsafeBuffer.allocate(serializedLongLength());
        serializeUnsafe(out);
        return out;
    }
    
    public default void serializeUnsafe(final Path path) throws IOException {
        final UnsafeBuffer out = UnsafeBuffer.mmap(path, serializedLongLength());
        serializeUnsafe(out);
        out.free();
    }
    
    public default void serializeUnknownSizeUnsafe(final Path path, final long overestimate)
            throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "rw");
                LongFileChannel channel = new LongFileChannel(raf)) {
            final UnsafeBuffer out = channel.longRWMap(0, overestimate);
            //noinspection resource
            Objects.requireNonNull(out);
            serializeUnsafe(out);
            final long newSize = out.position();
            out.free();
            channel.truncate(newSize);
        }
    }
    
    public default void serializeTruncatedUnsafe(final Path path) throws IOException {
        serializeUnknownSizeUnsafe(path, serializedLongLength());
    }
    
    public default void serializeCompressedUnknownSizeUnsafe(final Path path,
            final int overestimate)
            throws IOException {
        final UnsafeWrappedBuffer out = UnsafeBuffer.allocateWrapped(overestimate);
        serializeUnsafe(out);
        out.limit(out.position());
        out.rewind();
        final LZ4Compressor compressor = LZ4.compressor();
        final int maxCompressedSize = compressor.maxCompressedLength((int) out.limit());
        try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "rw");
                FileChannel channel = raf.getChannel()) {
            @SuppressWarnings("resource") final UnsafeWrappedBuffer compressed = UnsafeBuffer
                    .wrap(channel.map(MapMode.READ_WRITE, 0,
                            maxCompressedSize + Integer.BYTES));
            compressed.putLong(out.remaining()); // original length not compressed
            compressor.compress(out.wrapped(), compressed.wrapped());
            final long newSize = compressed.position();
            out.free();
            compressed.free();
            channel.truncate(newSize);
        }
    }
    
    public default void serializeCompressedTruncatedUnsafe(final Path path) throws IOException {
        serializeCompressedUnknownSizeUnsafe(path, serializedLength());
    }
    
}
