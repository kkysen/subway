package sen.khyber.misc;

import sen.khyber.io.IO;
import sen.khyber.proto.ProtoFileFormatter;
import sen.khyber.unsafe.reflect.ReflectedClass;
import sen.khyber.unsafe.reflect.ReflectedMember;
import sen.khyber.unsafe.reflect.Reflector;
import sen.khyber.util.RegexUtils;
import sen.khyber.util.exceptions.ExceptionUtils;
import sen.khyber.web.subway.client.proto.NyctTripDescriptor;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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
        //noinspection ConstantConditions
        final Pattern pattern = (Pattern) Reflector.get()
                .forClass(ProtoFileFormatter.class)
                .field("nullCheckPattern")
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
    
    private static void getMethodLineNumber() {
        final ReflectedClass<?> klass = Reflector.get().forClass(NyctTripDescriptor.class);
        final Object proto = klass.allocateInstance();
        final Object ref = 0;
        klass.field("trainId_").bindUnsafe(proto).setObject(ref);
        try {
            klass.rawMethod("getTrainId").invoke(proto);
            throw new IllegalStateException(
                    "should've caused and caught a ClassCastException");
        } catch (final IllegalAccessException e) {
            throw ExceptionUtils.atRuntime(e);
        } catch (final InvocationTargetException e) {
            Stream.of(e.getTargetException().getStackTrace()).forEach(System.out::println);
        }
    }
    
    public static void main(final String[] args) throws IOException {
        //        testGenerics();
        //        nullCheckRegex();
        final String s = "             \n  abcsdffgg\n";
        System.out.println(RegexUtils.expandSpacesToVariableWhitespace("[ ]*abc[^ ]*")
                .matcher(s).matches());
        System.out.println(Pattern.compile("[\\s]*abc[^ ]*").matcher(s).matches());
        
        ReflectedMember.useSimpleNameInToString(true);
        System.out.println(ReflectedMember.isUsingSimpleNameInToString());
        System.out.println(Reflector.get()
                .forClass(ProtoFileFormatter.class)
                .field("nullCheckPattern"));
        
        //        getMethodLineNumber();
        
        final List<Integer> list = new ArrayList<>();
        list.add(5);
        list.add(10);
        final List<Integer> subList = list.subList(0, 1);
        System.out.println("list: " + list);
        System.out.println("sub: " + subList);
        list.add(0, 0);
        System.out.println("list: " + list);
        System.out.println("sub: " + subList);
        subList.add(15);
        System.out.println("list: " + list);
        System.out.println("sub: " + subList);
    }
    
}