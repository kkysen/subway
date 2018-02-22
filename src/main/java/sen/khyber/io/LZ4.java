package sen.khyber.io;

import sen.khyber.unsafe.fields.ByteBufferUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

/**
 * @author Khyber Sen
 */
public final class LZ4 {
    
    private LZ4() {}
    
    private static LZ4Factory FACTORY;
    private static LZ4Compressor COMPRESSOR;
    private static LZ4FastDecompressor DECOMPRESSOR;
    
    public static LZ4Factory factory() {
        return FACTORY == null ? FACTORY = LZ4Factory.fastestInstance() : FACTORY;
    }
    
    public static LZ4Compressor compressor() {
        return COMPRESSOR == null ? COMPRESSOR = factory().fastCompressor() : COMPRESSOR;
    }
    
    public static LZ4FastDecompressor decompressor() {
        return DECOMPRESSOR == null ? DECOMPRESSOR = factory().fastDecompressor() : DECOMPRESSOR;
    }
    
    public static ByteBuffer decompressed(final ByteBuffer compressed) {
        final int length = compressed.getInt();
        final ByteBuffer decompressed =
                ByteBufferUtils.useNativeOrder(ByteBuffer.allocateDirect(length));
        decompressor().decompress(compressed, decompressed);
        ByteBufferUtils.free(compressed);
        decompressed.rewind();
        return decompressed;
    }
    
    public static ByteBuffer decompressed(final Path path) throws IOException {
        return decompressed(IO.mmap(path));
    }
    
    public static void decompress(final ByteBuffer compressed, final Path path) throws IOException {
        final int length = compressed.getInt();
        final ByteBuffer decompressed = IO.mmap(path, length);
        decompressor().decompress(compressed, decompressed);
        ByteBufferUtils.free(compressed);
        ByteBufferUtils.free(decompressed);
    }
    
}
