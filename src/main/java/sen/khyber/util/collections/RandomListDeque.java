package sen.khyber.util.collections;

import java.util.RandomAccess;

/**
 * Created by Khyber Sen on 2/13/2018.
 *
 * @author Khyber Sen
 */
public interface RandomListDeque<E> extends ListDeque<E>, RandomAccess {
    
    @Override
    public default boolean isRandomAccess() {
        return true;
    }
    
}