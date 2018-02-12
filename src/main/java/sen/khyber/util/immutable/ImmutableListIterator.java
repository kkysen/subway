package sen.khyber.util.immutable;

import java.util.ListIterator;

/**
 * Created by Khyber Sen on 2/11/2018.
 *
 * @author Khyber Sen
 */
public abstract class ImmutableListIterator<E> extends ImmutableIterator<E>
        implements ListIterator<E> {
    
    private static UnsupportedOperationException uoe() {
        return Immutable.uoe();
    }
    
    @Override
    public void set(final E e) {
        throw uoe();
    }
    
    @Override
    public void add(final E e) {
        throw uoe();
    }
    
}