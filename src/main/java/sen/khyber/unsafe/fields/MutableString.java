package sen.khyber.unsafe.fields;

import java.nio.charset.Charset;

/**
 * Created by Khyber Sen on 1/29/2018.
 *
 * @author Khyber Sen
 */
public class MutableString /*implements CharSequence, Comparable<String>, Iterable<Character>*/ {
    
    public static final boolean PRE_JAVA_9 = StringUtils.PRE_JAVA_9;
    public static final boolean COMPACT_STRINGS = StringUtils.COMPACT_STRINGS;
    
    public static final byte LATIN1 = StringUtils.LATIN1;
    public static final byte UTF16 = StringUtils.UTF16;
    
    private byte[] value;
    private byte coder;
    private int hash;
    
    private String s;
    
    public MutableString(final byte[] bytes) {
        value = bytes;
        coder = LATIN1;
        hash = 0;
        s = StringUtils.newString();
        if (PRE_JAVA_9) {
            
        } else {
            //            StringUtils.setValue(s, value);
            //            StringUtils.setCoder(s, coder);
        }
    }
    
    public MutableString(final byte[] value, final Charset charset) {
        
    }
    
    public MutableString(final String string) {
        s = string;
    }
    
}