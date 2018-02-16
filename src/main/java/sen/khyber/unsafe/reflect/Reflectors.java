package sen.khyber.unsafe.reflect;

/**
 * Created by Khyber Sen on 2/16/2018.
 *
 * @author Khyber Sen
 */
public final class Reflectors {
    
    private Reflectors() {}
    
    private static final Reflector MAIN = new Reflector();
    
    public static final Reflector main() {
        return MAIN;
    }
    
    public static final Reflector create() {
        return new Reflector();
    }
    
}