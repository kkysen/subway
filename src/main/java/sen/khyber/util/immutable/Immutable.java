package sen.khyber.util.immutable;

/**
 * Created by Khyber Sen on 2/11/2018.
 *
 * @author Khyber Sen
 */
public interface Immutable {
    
    static UnsupportedOperationException uoe() {
        return new UnsupportedOperationException();
    }
    
}