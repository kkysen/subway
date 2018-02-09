package sen.khyber.unsafe.fields;

/**
 * Created by Khyber Sen on 2/2/2018.
 *
 * @author Khyber Sen
 */
public final class StringBuilderUtils {
    
    private StringBuilderUtils() {}
    
    public static final char[] getCharArray(final StringBuilder sb) {
        final int length = sb.length();
        final char[] chars = new char[length];
        sb.getChars(0, length, chars, 0);
        return chars;
        // FIXME use unsafe reflection
    }
    
}