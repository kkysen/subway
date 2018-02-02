package sen.khyber.misc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Khyber Sen on 2/1/2018.
 *
 * @author Khyber Sen
 */
public class Misc {
    
    @SuppressWarnings("unchecked")
    public static void main(final String[] args) {
        final List<String> strings = new ArrayList<>();
        strings.add("Hello");
        System.out.println(strings);
        strings.clear();
        final List<Integer> ints = (List<Integer>) (List<?>) strings;
        ints.add(5);
        System.out.println(ints);
        System.out.println(strings);
        strings.add("World");
        System.out.println(ints);
        System.out.println(strings);
    }
    
}