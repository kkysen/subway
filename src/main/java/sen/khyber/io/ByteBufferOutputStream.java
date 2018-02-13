package sen.khyber.io;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.jetbrains.annotations.NotNull;

/**
 * @author Khyber Sen
 */
@RequiredArgsConstructor
public class ByteBufferOutputStream extends OutputStream {
    
    private final ByteBuffer buffer;
    
    @Override
    public void write(final int b) {
        buffer.put((byte) b);
    }
    
    @Override
    public void write(final @NotNull byte[] bytes, final int offset, final int length) {
        buffer.put(bytes, offset, length);
    }
    
    @Override
    public void close() throws IOException {}
    
}