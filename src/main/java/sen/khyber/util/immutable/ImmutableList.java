package sen.khyber.util.immutable;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/11/2018.
 *
 * @author Khyber Sen
 */
public abstract class ImmutableList<E> extends ImmutableCollection<E> implements List<E> {
    
    @Override
    public final boolean addAll(final int index, @NotNull final Collection<? extends E> c) {
        throw uoe();
    }
    
    @Override
    public final void replaceAll(final UnaryOperator<E> operator) {
        throw uoe();
    }
    
    @Override
    public final void sort(final Comparator<? super E> c) {
        throw uoe();
    }
    
    @Override
    public final E set(final int index, final E element) {
        throw uoe();
    }
    
    @Override
    public final void add(final int index, final E element) {
        throw uoe();
    }
    
    @Override
    public final E remove(final int index) {
        throw uoe();
    }
    
    @NotNull
    @Override
    public ImmutableListIterator<E> listIterator() {
        return listIterator(0);
    }
    
    @NotNull
    @Override
    public abstract ImmutableListIterator<E> listIterator(int index);
    
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }
    
    @Override
    public boolean contains(final Object o) {
        return indexOf(o) != -1;
    }
    
    @Override
    public boolean containsAll(@NotNull final Collection<?> c) {
        Objects.requireNonNull(c);
        for (final Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public Stream<E> parallelStream() {
        return stream().parallel();
    }
    
}