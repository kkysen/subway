package sen.khyber.io;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

import org.jetbrains.annotations.NotNull;

/**
 * @author Khyber Sen
 */
public class CharBufferReader extends Reader {
    
    private final CharBuffer buffer;
    private final int originalLimit;
    
    public CharBufferReader(final CharBuffer buffer) {
        this.buffer = buffer;
        buffer.mark();
        originalLimit = buffer.limit();
    }
    
    @Override
    public boolean markSupported() {
        return true;
    }
    
    @Override
    public void mark(final int readAheadLimit) {
        buffer.mark();
        if (readAheadLimit < buffer.remaining()) {
            buffer.limit(buffer.position() + readAheadLimit);
        } else {
            buffer.limit(originalLimit);
        }
        // FIXME TODO what does readAheadLimit mean
    }
    
    @Override
    public int read() {
        if (buffer.remaining() == 0) {
            return -1;
        }
        return buffer.get();
    }
    
    @Override
    public int read(final @NotNull char[] charBuffer, final int offset, final int length)
            throws IOException {
        if (buffer.remaining() == 0) {
            return -1;
        }
        final int start = buffer.position();
        buffer.get(charBuffer, offset, Math.min(length, buffer.remaining()));
        return buffer.position() - start;
    }
    
    @Override
    public int read(final @NotNull CharBuffer target) throws IOException {
        if (buffer.remaining() == 0) {
            return -1;
        }
        final int start = target.position();
        target.put(buffer);
        return target.position() - start;
    }
    
    @Override
    public boolean ready() {
        return true;
    }
    
    @Override
    public void reset() {
        buffer.reset();
    }
    
    @Override
    public long skip(final long n) {
        return BufferUtils.skip(buffer, n);
    }
    
    @Override
    public void close() throws IOException {}
    
}
