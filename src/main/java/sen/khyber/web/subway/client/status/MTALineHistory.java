package sen.khyber.web.subway.client.status;

import sen.khyber.io.IO;
import sen.khyber.io.LZ4;
import sen.khyber.unsafe.UnsafeUtils;
import sen.khyber.unsafe.buffers.UnsafeBuffer;
import sen.khyber.unsafe.buffers.UnsafeWrappedBuffer;
import sen.khyber.util.ObjectUtils;
import sen.khyber.util.StringBuilderAppendable;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;

import org.jetbrains.annotations.NotNull;

import net.jpountz.lz4.LZ4Compressor;

/**
 * Created by Khyber Sen on 2/25/2018.
 *
 * @author Khyber Sen
 */
@Accessors(fluent = true)
public final class MTALineHistory
        implements MTAUpdateProcessor<IOException>, StringBuilderAppendable {
    
    private static final String EXTENSION = ".txt";
    
    private static final int BUFFER_LENGTH;
    
    static {
        // ensure page aligned
        int bufferLength = 128 * IO.KB;
        final int pageSize = UnsafeUtils.getUnsafe().pageSize();
        if (bufferLength % pageSize != 0) {
            bufferLength = (bufferLength + pageSize - 1) / pageSize;
        }
        BUFFER_LENGTH = bufferLength;
    }
    
    private static @NotNull String lineToFileName(final @NotNull MTALine<?> line) {
        return line.type().fileName() + " - " + line.fileName();
    }
    
    private final @Getter @NotNull MTALine<?> line;
    private final @Getter @NotNull Path path;
    private @Getter long size;
    private final @Getter @NotNull Path bufferPath;
    
    private final @NotNull UnsafeWrappedBuffer buffer;
    
    public MTALineHistory(final @NotNull MTALine<?> line, final @NotNull Path dir)
            throws IOException {
        ObjectUtils.requireNonNull(line, dir);
        this.line = line;
        final String fileName = lineToFileName(line);
        final String bufferFileName = fileName + " (Buffer)";
        path = dir.resolve(fileName + EXTENSION);
        size = path.toFile().length();
        bufferPath = dir.resolve(bufferFileName + EXTENSION);
        buffer = UnsafeBuffer.wrap(IO.mmap(bufferPath, BUFFER_LENGTH));
        final long bufferStart = bufferPath.toFile().length();
        if (bufferStart > buffer.limit()) {
            throw new IllegalStateException(bufferPath + " is already too full");
        }
        buffer.position(bufferStart);
    }
    
    // flush buffer to main path and compress contents
    private void flush() throws IOException {
        final long length = Long.BYTES + buffer.position();
        try (UnsafeWrappedBuffer history = UnsafeBuffer.wrap(IO.mmap(path, size, length))) {
            final LZ4Compressor compressor = LZ4.compressor();
            history.putLong(buffer.position());
            buffer.rewind();
            final ByteBuffer src = buffer.wrapped();
            src.rewind();
            compressor.compress(src, history.wrapped());
            size += length;
        }
    }
    
    private void tryFlush(final @NotNull MTALineStatus status) throws IOException {
        final long length = status.serializedLongLength();
        if (buffer.remaining() < length) {
            flush();
        }
    }
    
    @Override
    public final void update(final @NotNull MTALineStatus status) throws IOException {
        tryFlush(status);
        buffer.put(status);
    }
    
    @Override
    public final @NotNull StringBuilder append(@NotNull final StringBuilder sb) {
        sb.append("MTALineHistory[")
                .append(line.type().officialName())
                .append(": ")
                .append(line.officialName())
                .append(": ")
                .append("path = ")
                .append(path)
                .append(", bufferPath = ")
                .append(bufferPath)
                .append(", buffer = ");
        buffer.append(sb);
        return sb.append(']');
    }
    
    @Override
    public final @NotNull String toString() {
        return defaultToString();
    }
    
}