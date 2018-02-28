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

import org.jetbrains.annotations.NotNull;

import net.jpountz.lz4.LZ4Compressor;

/**
 * @author Khyber Sen
 */
@SuppressWarnings("ClassReferencesSubclass")
public interface UnsafeSerializable extends FastSerializable {
    
    public long serializedLongLength();
    
    @Override
    public default int serializedLength() {
        final long length = serializedLongLength();
        if (length > Integer.MAX_VALUE) {
            throw new IllegalStateException("serializedLength integer overflow: " + length);
        }
        return (int) length;
    }
    
    public void serializeUnsafe(final @NotNull UnsafeBuffer out);
    
    @Override
    public default void serialize(final @NotNull ByteBuffer out) {
        Objects.requireNonNull(out);
        serializeUnsafe(UnsafeBuffer.wrap(out));
    }
    
    public default @NotNull UnsafeBuffer serializeUnsafe() {
        final UnsafeBuffer out = UnsafeBuffer.allocate(serializedLongLength());
        serializeUnsafe(out);
        return out;
    }
    
    public default void serializeUnsafe(final @NotNull Path path) throws IOException {
        Objects.requireNonNull(path);
        final UnsafeBuffer out = UnsafeBuffer.mmap(path, serializedLongLength());
        serializeUnsafe(out);
        out.free();
    }
    
    public default void serializeUnknownSizeUnsafe(final @NotNull Path path,
            final long overestimate)
            throws IOException {
        Objects.requireNonNull(path);
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
    
    public default void serializeTruncatedUnsafe(final @NotNull Path path) throws IOException {
        Objects.requireNonNull(path);
        serializeUnknownSizeUnsafe(path, serializedLongLength());
    }
    
    public default void serializeCompressedUnknownSizeUnsafe(final @NotNull Path path,
            final int overestimate)
            throws IOException {
        Objects.requireNonNull(path);
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
    
    public default void serializeCompressedTruncatedUnsafe(final @NotNull Path path)
            throws IOException {
        Objects.requireNonNull(path);
        serializeCompressedUnknownSizeUnsafe(path, serializedLength());
    }
    
}
