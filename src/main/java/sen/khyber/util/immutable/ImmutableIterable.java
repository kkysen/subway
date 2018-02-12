package sen.khyber.util.immutable;

import java.util.Spliterator;
import java.util.Spliterators;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/11/2018.
 *
 * @author Khyber Sen
 */
public abstract class ImmutableIterable<E> implements Iterable<E>, Immutable {
    
    @NotNull
    @Override
    public abstract ImmutableIterator<E> iterator();
    
    @Override
    public Spliterator<E> spliterator() {
        return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.IMMUTABLE);
    }
    
}