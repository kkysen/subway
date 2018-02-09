package sen.khyber.io;

import java.nio.Buffer;

/**
 * Created by Khyber Sen on 2/2/2018.
 *
 * @author Khyber Sen
 */
public final class BufferUtils {
    
    private BufferUtils() {}
    
    public static long skip(final Buffer buffer, final long n) {
        final int pos = buffer.position();
        final int toSkip = (int) n;
        final int skipped;
        if (toSkip != n || pos + toSkip != pos + n) {
            skipped = buffer.remaining();
            buffer.position(buffer.limit());
            return skipped;
        }
        skipped = Math.min(toSkip, buffer.remaining());
        buffer.position(pos + skipped);
        return skipped;
    }
    
}