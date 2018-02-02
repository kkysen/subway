package sen.khyber.util;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/1/2018.
 *
 * @author Khyber Sen
 */
@Accessors(fluent = true)
public class MappedList<T, R> extends Mapper<T, R> implements List<R> {
    
    private static UnsupportedOperationException uoe() {
        return new UnsupportedOperationException();
    }
    
    private final @Getter List<? extends T> delegate;
    
    public MappedList(final @NotNull List<? extends T> delegate,
            final @NotNull Function<? super T, ? extends R> mapper) {
        super(mapper);
        Objects.requireNonNull(delegate);
        this.delegate = delegate;
    }
    
    /**
     * Creates a new MappedList mapping the result of this MappedList with another mapper.
     */
    public <S> MappedList<T, S> map(final Function<? super R, ? extends S> mapper) {
        return new MappedList<>(delegate, this.mapper.andThen(mapper));
    }
    
    /**
     * If a list is RandomAccess.
     */
    private static <T> boolean isRam(final List<T> list) {
        return list instanceof RandomAccess;
    }
    
    /**
     * An iterator if not RandomAccess, else null.
     */
    private static <T> Iterator<T> iterator(final List<T> list) {
        return isRam(list) ? null : list.iterator();
    }
    
    /**
     * The next element, using List#get(int) if RandomAccess, else using using Iterator#next()
     */
    private static <T> T next(
            final boolean ram, final int i, final List<? extends T> list,
            final Iterator<? extends T> iter) {
        return ram ? list.get(i) : iter.next();
    }
    
    private static boolean notEquals(final Object a, final Object b) {
        return a != b && (a == null || b == null || !a.equals(b));
    }
    
    public boolean equals(final List<?> list) {
        final int size = delegate.size();
        if (list.size() != size) {
            return false;
        }
        final List<? extends T> thisList = delegate;
        //noinspection UnnecessaryLocalVariable
        final List<?> thatList = list;
        final boolean thisRam = isRam(thisList);
        final boolean thatRam = isRam(thatList);
        final Iterator<? extends T> thisIter = iterator(thisList);
        final Iterator<?> thatIter = iterator(thatList);
        for (int i = 0; i < size; i++) {
            final T t = next(thisRam, i, thisList, thisIter);
            final R r = map(t);
            final Object e = next(thatRam, i, thatList, thatIter);
            if (notEquals(r, e)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Iterable)) {
            return false;
        }
        final Iterable<?> iterable = (Iterable<?>) o;
        final int size = delegate.size();
        if (iterable instanceof Collection) {
            final Collection<?> collection = (Collection<?>) iterable;
            if (collection.size() != size) {
                return false;
            }
            if (collection instanceof List) {
                return equals((List<?>) collection);
            }
        }
        final List<? extends T> thisList = delegate;
        final boolean thisRam = isRam(thisList);
        final Iterator<? extends T> thisIter = iterator(thisList);
        final Iterator<?> thatIter = iterable.iterator();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < size; i++) {
            if (!thatIter.hasNext()) {
                return false; // not same size
            }
            final T t = next(thisRam, i, thisList, thisIter);
            final R r = map(t);
            final Object e = thatIter.next();
            if (notEquals(r, e)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        final List<? extends T> list = delegate;
        final int size = list.size();
        int hash = size;
        final boolean ram = isRam(list);
        final Iterator<? extends T> iter = iterator(list);
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < size; i++) {
            final T t = next(ram, i, list, iter);
            final R r = map(t);
            if (r != null) {
                hash = hash * prime + r.hashCode();
            }
        }
        return hash;
    }
    
    @Override
    public int size() {
        return delegate.size();
    }
    
    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }
    
    @Override
    public int indexOf(final Object o) {
        final List<? extends T> list = delegate;
        if (o == null) {
            return list.indexOf(null);
        }
        if (isRam(list)) {
            final int size = list.size();
            for (int i = 0; i < size; i++) {
                final T t = list.get(i);
                if (o.equals(map(t))) {
                    return i;
                }
            }
        } else {
            int i = 0;
            for (final T t : list) {
                if (o.equals(map(t))) {
                    return i;
                }
                i++;
            }
        }
        return -1;
    }
    
    @Override
    public int lastIndexOf(final Object o) {
        final List<? extends T> list = delegate;
        if (o == null) {
            return list.indexOf(null);
        }
        if (isRam(list)) {
            for (int i = list.size() - 1; i >= 0; i--) {
                final T t = list.get(i);
                if (o.equals(map(t))) {
                    return i;
                }
            }
        } else {
            final int i = list.size() - 1;
            final ListIterator<? extends T> iter = list.listIterator(i);
            while (iter.hasPrevious()) {
                final T t = iter.previous();
                if (o.equals(map(t))) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    @NotNull
    @Override
    public ListIterator<R> listIterator() {
        return listIterator(0);
    }
    
    @NotNull
    @Override
    public ListIterator<R> listIterator(final int index) {
        return new ListIterator<>() {
            
            private final ListIterator<? extends T> iter = delegate.listIterator(index);
            
            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }
            
            @Override
            public R next() {
                return map(iter.next());
            }
            
            @Override
            public boolean hasPrevious() {
                return iter.hasNext();
            }
            
            @Override
            public R previous() {
                return map(iter.previous());
            }
            
            @Override
            public int nextIndex() {
                return iter.nextIndex();
            }
            
            @Override
            public int previousIndex() {
                return iter.nextIndex();
            }
            
            @Override
            public void remove() {
                iter.remove();
            }
            
            @Override
            public void set(final R r) {
                throw uoe();
            }
            
            @Override
            public void add(final R r) {
                throw uoe();
            }
            
            @Override
            public void forEachRemaining(final Consumer<? super R> action) {
                iter.forEachRemaining(accepting(action));
            }
            
        };
    }
    
    @NotNull
    @Override
    public List<R> subList(final int fromIndex, final int toIndex) {
        return new MappedList<>(delegate.subList(fromIndex, toIndex), mapper);
    }
    
    @Override
    public boolean contains(final Object o) {
        return indexOf(o) != -1;
    }
    
    @NotNull
    @Override
    public Iterator<R> iterator() {
        return new Iterator<>() {
            
            private final Iterator<? extends T> iter = delegate.iterator();
            
            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }
            
            @Override
            public R next() {
                return map(iter.next());
            }
            
            @Override
            public void remove() {
                iter.remove();
            }
            
            @Override
            public void forEachRemaining(final Consumer<? super R> action) {
                iter.forEachRemaining(accepting(action));
            }
            
        };
    }
    
    @NotNull
    @Override
    public Object[] toArray() {
        final Object[] a = delegate.toArray();
        for (int i = 0; i < a.length; i++) {
            //noinspection unchecked
            a[i] = map((T) a[i]);
        }
        return a;
    }
    
    @SuppressWarnings("NullableProblems")
    @NotNull
    @Override
    public <S> S[] toArray(final S[] a) {
        Objects.requireNonNull(a);
        final Object[] b = delegate.toArray();
        for (int i = 0; i < a.length; i++) {
            //noinspection unchecked
            a[i] = (S) map((T) b[i]);
        }
        return a;
    }
    
    @Override
    public R get(final int index) {
        return map(delegate.get(index));
    }
    
    @Override
    public boolean add(final R r) {
        throw uoe();
    }
    
    @Override
    public void add(final int index, final R r) {
        throw uoe();
    }
    
    @Override
    public R set(final int index, final R r) {
        throw uoe();
    }
    
    @Override
    public R remove(final int index) {
        return map(delegate.remove(index));
    }
    
    @Override
    public boolean remove(final Object o) {
        if (o == null) {
            return delegate.remove(null);
        }
        if (delegate instanceof RandomAccess) {
            final int index = indexOf(o);
            if (index != -1) {
                remove(index);
                return true;
            } else {
                return false;
            }
        } else {
            final Iterator<? extends T> iter = delegate.iterator();
            while (iter.hasNext()) {
                if (o.equals(map(iter.next()))) {
                    iter.remove();
                    return true;
                }
            }
            return false;
        }
    }
    
    @Override
    public boolean containsAll(@NotNull final Collection<?> c) {
        for (final Object e : c) {
            if (!contains(e)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean addAll(@NotNull final Collection<? extends R> c) {
        return addAll(0, c);
    }
    
    @Override
    public boolean addAll(final int index, @NotNull final Collection<? extends R> c) {
        throw uoe();
    }
    
    @Override
    public boolean removeAll(@NotNull final Collection<?> c) {
        Objects.requireNonNull(c);
        return removeIf(c::contains);
    }
    
    @Override
    public boolean retainAll(@NotNull final Collection<?> c) {
        Objects.requireNonNull(c);
        return retainIf(c::contains);
    }
    
    @Override
    public boolean removeIf(final @NotNull Predicate<? super R> filter) {
        Objects.requireNonNull(filter);
        final Predicate<? super T> mappedFilter = t -> filter.test(map(t));
        boolean removed = false;
        final List<? extends T> list = delegate;
        final boolean ram = isRam(list);
        final Iterator<? extends T> iter = iterator(list);
        final int size = list.size();
        for (int i = 0; i < size; i++) {
            if (mappedFilter.test(next(ram, i, list, iter))) {
                if (ram) {
                    list.remove(i);
                } else {
                    //noinspection ConstantConditions
                    iter.remove();
                }
                removed = true;
            }
        }
        return removed;
    }
    
    public boolean retainIf(final @NotNull Predicate<? super R> filter) {
        Objects.requireNonNull(filter);
        return removeIf(filter.negate());
    }
    
    @Override
    public void replaceAll(final UnaryOperator<R> operator) {
        mapper = mapper.andThen(operator);
    }
    
    @Override
    public void sort(final Comparator<? super R> c) {
        delegate.sort(Comparator.comparing(mapper, c));
    }
    
    @Override
    public void clear() {
        delegate.clear();
    }
    
    @Override
    public Spliterator<R> spliterator() {
        return new MappedSpliterator<>(delegate.spliterator(), mapper);
    }
    
    @Override
    public Stream<R> stream() {
        return delegate.stream().map(mapper);
    }
    
    @Override
    public Stream<R> parallelStream() {
        return delegate.parallelStream().map(mapper);
    }
    
    public StringBuilder append(final StringBuilder sb) {
        final List<? extends T> list = delegate;
        final int size = list.size();
        if (size == 0) {
            sb.append("[]");
            return sb;
        }
        final boolean ram = isRam(list);
        final Iterator<? extends T> iter = iterator(list);
        //noinspection ForLoopReplaceableByForEach
        sb.append('[');
        for (int i = 0; ; i++) {
            final T t = next(ram, i, list, iter);
            sb.append(map(t));
            if (i == size - 1) {
                sb.append(']');
                return sb;
            }
            sb.append(',');
            sb.append(' ');
        }
    }
    
    @Override
    public String toString() {
        return append(new StringBuilder()).toString();
    }
    
}