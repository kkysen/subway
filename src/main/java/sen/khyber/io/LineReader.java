package sen.khyber.io;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * Created by Khyber Sen on 2/2/2018.
 *
 * @author Khyber Sen
 */
public interface LineReader {
    
    public Charset charset();
    
    public ByteBuffer readLineAsByteBuffer();
    
    public CharBuffer readLineAsCharBuffer();
    
    public byte[] readLineAsBytes();
    
    public char[] readLineAsChars();
    
    public StringBuilder readLineAsStringBuilder();
    
    public String readLineAsString();
    
    public ByteBuffer[] readLinesAsByteBuffers();
    
    // TODO
    
}