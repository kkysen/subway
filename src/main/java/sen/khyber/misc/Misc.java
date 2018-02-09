package sen.khyber.misc;

import sen.khyber.io.IO;
import sen.khyber.proto.ProtoFileFormatter;
import sen.khyber.unsafe.reflectors.Reflectors;
import sen.khyber.util.RegexUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Khyber Sen on 2/1/2018.
 *
 * @author Khyber Sen
 */
public class Misc {
    
    @SuppressWarnings("unchecked")
    private static void testGenerics() {
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
    
    private static void nullCheck() {
        final String hello$World = null;
        if (hello$World == null) {
            throw new NullPointerException();
        }
    }
    
    private static void nullCheckRegex() throws IOException {
        final Pattern pattern = (Pattern) Objects.requireNonNull(
                Reflectors.forClass(ProtoFileFormatter.class)
                        .reflectedField("nullCheckPattern"))
                .getObject();
        final Path path = IO.ProjectJava.resolve("misc").resolve("Misc.java");
        System.out.println(path);
        final String s = IO.readString(path);
        final Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {
            System.out.println(matcher.groupCount());
            System.out.println(matcher.group(1));
            System.out.println(matcher.group());
        }
    }
    
    public static void main(final String[] args) throws IOException {
        //        testGenerics();
        //        nullCheckRegex();
        final String s = "             \n  abcsdffgg\n";
        System.out.println(RegexUtils.expandSpacesToVariableWhitespace("[ ]*abc[^ ]*")
                .matcher(s).matches());
        System.out.println(Pattern.compile("[\\s]*abc[^ ]*").matcher(s).matches());
    }
    
}