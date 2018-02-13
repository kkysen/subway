package sen.khyber.util.collections;

/**
 * Created by Khyber Sen on 2/13/2018.
 *
 * @author Khyber Sen
 */
public interface SequentialListDeque<E> extends ListDeque<E> {
    
    public default boolean isRandomAccess() {
        return false;
    }
    
}