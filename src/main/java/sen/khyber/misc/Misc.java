package sen.khyber.misc;

import sen.khyber.io.IO;
import sen.khyber.proto.ProtoFileFormatter;
import sen.khyber.unsafe.reflect.ClassNames;
import sen.khyber.unsafe.reflect.ReflectedClass;
import sen.khyber.unsafe.reflect.Reflectors;
import sen.khyber.util.RegexUtils;
import sen.khyber.util.exceptions.ExceptionUtils;
import sen.khyber.web.subway.client.proto.NyctTripDescriptor;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
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
        final Pattern pattern = (Pattern) Reflectors.main()
                .get(ProtoFileFormatter.class, false)
                .fieldUnchecked("nullCheckPattern")
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
        final ReflectedClass<?> klass = Reflectors.main().get(NyctTripDescriptor.class, false);
        final Object proto = klass.allocateInstance();
        final Object ref = 0;
        klass.fieldUnchecked("trainId_").bindUnsafe(proto).setObject(ref);
        try {
            klass.rawMethodUnchecked("getTrainId").invoke(proto);
            throw new IllegalStateException(
                    "should've caused and caught a ClassCastException");
        } catch (final IllegalAccessException e) {
            throw ExceptionUtils.atRuntime(e);
        } catch (final InvocationTargetException e) {
            Stream.of(e.getTargetException().getStackTrace()).forEach(System.out::println);
        }
    }
    
    private static void regexUtilsExpandSpacesToVariableWhitespace() {
        final String s = "             \n  abcsdffgg\n";
        System.out.println(RegexUtils.expandSpacesToVariableWhitespace("[ ]*abc[^ ]*")
                .matcher(s).matches());
        System.out.println(Pattern.compile("[\\s]*abc[^ ]*").matcher(s).matches());
    }
    
    private static void reflectedMemberToStringUsingClassNames() {
        ClassNames.useSimpleNameInToString(true);
        System.out.println(ClassNames.isUsingSimpleNameInToString());
        System.out.println(Reflectors.main()
                .get(ProtoFileFormatter.class, false)
                .field("nullCheckPattern"));
    }
    
    private static void comodification() {
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
    
    @SuppressWarnings("OverlyComplexMethod")
    private static void chicagoCrimeJson(final ByteBuffer buffer, final int rowNum)
            throws IOException {
        System.out.println("\n\t" + rowNum + '\n');
        byte b;
        while ((b = buffer.get()) != '{' && b != '[') { }
        System.out.println("start = " + buffer.position());
        final int start = buffer.position() - 1;
        {
            System.out.println((char) b);
            int objectDepth = b == '{' ? 1 : 0;
            int arrayDepth = b == '[' ? 1 : 0;
            while (objectDepth + arrayDepth > 0) {
                if (buffer.position() % 1000 == 0) {
                    System.out.println(buffer.position());
                }
                switch (buffer.get()) {
                    case '{':
                        objectDepth++;
                        break;
                    case '}':
                        objectDepth--;
                        break;
                    case '[':
                        arrayDepth++;
                        break;
                    case ']':
                        arrayDepth--;
                        break;
                    default:
                        break;
                }
            }
        }
        System.out.println("buffer = " + buffer);
        final int end = buffer.position();
        buffer.limit(end);
        buffer.position(start);
        System.out.println("buffer = " + buffer);
        final ByteBuffer out =
                IO.mmap(IO.Downloads.resolve("row" + rowNum + ".json"), buffer.remaining());
        System.out.println("out = " + out);
        out.put(buffer);
        // reset to normal size
        buffer.limit(buffer.capacity());
        buffer.position(end);
    }
    
    private static void chicagoCrimeJson() throws IOException {
        final ByteBuffer buffer = IO.mmap(IO.Downloads.resolve("rows.json"));
        final int startDepth = 2;
        {
            int depth = 0;
            while (depth < startDepth) {
                if (buffer.get() == '{') {
                    depth++;
                }
            }
        }
        buffer.position(buffer.position() - 1);
        for (int i = 0; i < 10; i++) {
            chicagoCrimeJson(buffer, i);
        }
    }
    
    private static void testNonExportedClassReflection() {
        ClassNames.useSimpleNameInToString(true);
        Reflectors.main()
                .get("sun.nio.ch.NativeThreadSet")
                .orElseThrow(AssertionError::new)
                .methods()
                .forEach(System.out::println);
    }
    
    public static void main(final String[] args) throws IOException {
        testNonExportedClassReflection();
    }
    
}