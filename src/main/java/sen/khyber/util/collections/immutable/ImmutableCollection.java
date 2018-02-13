package sen.khyber.util.collections.immutable;

import java.util.Collection;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/11/2018.
 *
 * @author Khyber Sen
 */
public abstract class ImmutableCollection<E> extends ImmutableIterable<E>
        implements Collection<E>, Immutable {
    
    static final UnsupportedOperationException uoe() {
        return Immutable.uoe();
    }
    
    @Override
    public final boolean add(final E e) {
        throw uoe();
    }
    
    @Override
    public final boolean remove(final Object o) {
        throw uoe();
    }
    
    @Override
    public final boolean addAll(final @NotNull Collection<? extends E> c) {
        throw uoe();
    }
    
    @Override
    public final boolean removeAll(final @NotNull Collection<?> c) {
        throw uoe();
    }
    
    @Override
    public final boolean removeIf(final Predicate<? super E> filter) {
        throw uoe();
    }
    
    @Override
    public final boolean retainAll(final @NotNull Collection<?> c) {
        throw uoe();
    }
    
    @Override
    public final void clear() {
        throw uoe();
    }
    
    @Override
    public Spliterator<E> spliterator() {
        return Spliterators.spliterator(this, Spliterator.IMMUTABLE);
    }
    
}