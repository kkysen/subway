package sen.khyber.io;

import sen.khyber.unsafe.fields.StringBuilderUtils;
import sen.khyber.unsafe.fields.StringUtils;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;

import org.jetbrains.annotations.NotNull;

/**
 * @author Khyber Sen
 */
@RequiredArgsConstructor
public class CharBufferWriter extends Writer {
    
    private final CharBuffer buffer;
    
    @Override
    public void write(final int c) {
        buffer.put((char) c);
    }
    
    @Override
    public void write(final @NotNull char[] chars, final int offset, final int length)
            throws IOException {
        buffer.put(chars, offset, length);
    }
    
    @Override
    public void write(final @NotNull String s, final int offset, final int length)
            throws IOException {
        write(StringUtils.getCharArray(s), offset, length);
    }
    
    @Override
    public Writer append(final CharSequence chars) throws IOException {
        return append(chars, 0, chars.length());
    }
    
    @Override
    public Writer append(final CharSequence chars, final int start, final int end)
            throws IOException {
        if (chars == null) {
            write("null");
        } else if (chars instanceof CharBuffer) {
            buffer.put((CharBuffer) chars);
        } else {
            final int length = end - start;
            final char[] charArray;
            if (chars instanceof StringBuilder) {
                final StringBuilder sb = (StringBuilder) chars;
                if (end > sb.length()) {
                    throw new IndexOutOfBoundsException();
                }
                charArray = StringBuilderUtils.getCharArray(sb);
            } else {
                final String s;
                if (chars instanceof String) {
                    s = (String) chars;
                } else {
                    s = chars.toString();
                }
                charArray = StringUtils.getCharArray(s);
            }
            write(charArray, start, length);
        }
        return this;
    }
    
    @Override
    public void flush() throws IOException {}
    
    @Override
    public void close() throws IOException {}
    
}
