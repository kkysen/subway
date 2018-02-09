package sen.khyber.unsafe.fields;

import sen.khyber.unsafe.UnsafeUtils;
import sen.khyber.unsafe.reflectors.ReflectedField;
import sen.khyber.unsafe.reflectors.Reflectors;
import sen.khyber.util.exceptions.ExceptionUtils;

import sun.misc.Unsafe;

/**
 * Created by Khyber Sen on 1/29/2018.
 *
 * @author Khyber Sen
 */
public class StringUtils {
    
    public static final byte LATIN1 = 0;
    public static final byte UTF16 = 1;
    
    public static final boolean PRE_JAVA_9;
    public static final boolean COMPACT_STRINGS;
    
    private static final Unsafe unsafe = UnsafeUtils.getUnsafe();
    
    static {
        final ReflectedField field =
                Reflectors.forClass(String.class).reflectedField("COMPACT_STRINGS");
        PRE_JAVA_9 = field == null;
        //noinspection SimplifiableConditionalExpression, ConstantConditions
        COMPACT_STRINGS = PRE_JAVA_9 ? false : field.getBoolean();
    }
    
    public static final byte coder(final String s) {
        return 0; // FIXME
    }
    
    public static final boolean isLatin1(final String s) {
        return true; // FIXME
    }
    
    public static final boolean isUTF16(final String s) {
        return true; // FIXME
    }
    
    public static final byte[] getByteArray(final String s) {
        return s.getBytes(); // FIXME
    }
    
    public static final char[] getCharArray(final String s) {
        return s.toCharArray(); // FIXME
    }
    
    public static final String newString() {
        try {
            return (String) unsafe.allocateInstance(String.class);
        } catch (final InstantiationException e) {
            throw ExceptionUtils.atRuntime(e);
        }
    }
    
    public static final String newString(final char[] chars) {
        //        final String s = newString();
        //        // FIXME set chars
        //        return s;
        // FIXME
        return new String(chars);
    }
    
}