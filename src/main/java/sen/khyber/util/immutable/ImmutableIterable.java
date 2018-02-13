package sen.khyber.util.immutable;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.StringJoiner;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/11/2018.
 *
 * @author Khyber Sen
 */
public abstract class ImmutableIterable<E> implements Iterable<E>, Immutable {
    
    @Override
    public abstract @NotNull ImmutableIterator<E> iterator();
    
    @Override
    public Spliterator<E> spliterator() {
        return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.IMMUTABLE);
    }
    
    @Override
    public String toString() {
        final StringJoiner sj = new StringJoiner(", \n", "[", "]");
        for (final E e : this) {
            sj.add(String.valueOf(e));
        }
        return sj.toString();
    }
    
}