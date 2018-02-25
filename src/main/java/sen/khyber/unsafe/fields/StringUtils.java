package sen.khyber.unsafe.fields;

import sen.khyber.unsafe.UnsafeUtils;
import sen.khyber.unsafe.reflect.ReflectedClass;
import sen.khyber.unsafe.reflect.ReflectedField;
import sen.khyber.unsafe.reflect.Reflectors;
import sen.khyber.util.exceptions.ExceptionUtils;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import sun.misc.Unsafe;

/**
 * Created by Khyber Sen on 1/29/2018.
 *
 * @author Khyber Sen
 */
public final class StringUtils {
    
    private StringUtils() {}
    
    private static final ReflectedClass<String> StringClass = Reflectors.main().get(String.class);
    
    private static final ReflectedField valueField = StringClass.fieldUnchecked("value");
    private static final ReflectedField coderField = StringClass.fieldUnchecked("coder");
    
    public static final byte LATIN1 = 0;
    public static final byte UTF16 = 1;
    
    public static final boolean PRE_JAVA_9;
    public static final boolean COMPACT_STRINGS;
    
    private static final Unsafe unsafe = UnsafeUtils.getUnsafe();
    
    static {
        final Optional<ReflectedField> field = StringClass.field("COMPACT_STRINGS");
        PRE_JAVA_9 = !field.isPresent();
        //noinspection SimplifiableConditionalExpression, ConstantConditions
        COMPACT_STRINGS = PRE_JAVA_9 ? false : field.get().getBoolean();
    }
    
    //    public static final byte coder(final String s) {
    //        return 0; // FIXME
    //    }
    
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
    
    public static final String newString(final byte[] bytes) {
        return new String(bytes);
    }
    
    public static final byte coder(final @NotNull String s) {
        return coderField.getByte(s);
    }
    
    public static final byte[] getRawValue(final @NotNull String s) {
        return (byte[]) valueField.getObject(s);
    }
    
    public static final int numBytes(final @NotNull String s) {
        return getRawValue(s).length;
    }
    
    public static final String ofRawValue(final @NotNull byte[] value, final byte coder) {
        final String s = StringClass.allocateInstance();
        valueField.setObject(s, value);
        coderField.setByte(s, coder);
        return s;
    }
    
    public static final int encodeLength(int length, final byte coder) {
        if (coder == UTF16) {
            length = -length;
        }
        return length;
    }
    
    public static final int decodeLength(int encodedLength) {
        if (encodedLength < 0) {
            encodedLength = -encodedLength;
        }
        return encodedLength;
    }
    
    public static final byte decodeCoder(final int encodedLength) {
        return encodedLength < 0 ? UTF16 : LATIN1;
    }
    
}