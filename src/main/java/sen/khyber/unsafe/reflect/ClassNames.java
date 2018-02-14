package sen.khyber.unsafe.reflect;

/**
 * Created by Khyber Sen on 2/13/2018.
 *
 * @author Khyber Sen
 */
public final class ClassNames {
    
    private ClassNames() {}
    
    private static boolean useSimpleNameInToString = false;
    
    public static final boolean isUsingSimpleNameInToString() {
        return useSimpleNameInToString;
    }
    
    public static final void useSimpleNameInToString(final boolean useSimpleNameInToString) {
        ClassNames.useSimpleNameInToString = useSimpleNameInToString;
    }
    
    public static String classToName(final Class<?> klass) {
        return useSimpleNameInToString
                ? klass.getSimpleName()
                : klass.getName();
    }
    
}