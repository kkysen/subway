package sen.khyber.util;

import java.util.regex.Pattern;

/**
 * Created by Khyber Sen on 2/3/2018.
 *
 * @author Khyber Sen
 */
public class RegexUtils {
    
    public static final Pattern expandSpacesToVariableWhitespace(final String pattern) {
        final int length = pattern.length();
        final StringBuilder sb = new StringBuilder(length); // slight underestimate
        int last = 0;
        int i = -1;
        int j;
        while ((j = pattern.indexOf(' ', i)) != -1) {
            i = j;
            sb.append(pattern.substring(last, i));
            last = ++i;
            sb.append("[\\s]*");
        }
        sb.append(pattern.substring(last, length));
        return Pattern.compile(sb.toString());
    }
    
}