package sen.khyber.util.collections;

import java.util.Deque;
import java.util.List;

/**
 * Created by Khyber Sen on 2/13/2018.
 *
 * @author Khyber Sen
 */
public interface ListDeque<E> extends List<E>, Deque<E> {
    
    public boolean isRandomAccess();
    
}