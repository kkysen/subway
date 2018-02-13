package sen.khyber.util.collections.immutable;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/11/2018.
 *
 * @author Khyber Sen
 */
public class ImmutableArrayList<E> extends ImmutableList<E> {
    
    private final E[] elements;
    private final int offset;
    private final int length;
    
    public ImmutableArrayList(final E[] elements, final int offset, final int length) {
        Objects.checkFromIndexSize(offset, length, elements.length);
        this.elements = elements;
        this.offset = offset;
        this.length = length;
    }
    
    @SafeVarargs
    public ImmutableArrayList(final E... elements) {
        this(elements, 0, elements.length);
    }
    
    @Override
    public E get(final int index) {
        Objects.checkIndex(index, length);
        return elements[offset + index];
    }
    
    @Override
    public int indexOf(final Object o) {
        final E[] a = elements;
        final int start = offset;
        final int last = offset + length;
        if (o == null) {
            for (int i = start; i < last; i++) {
                if (a[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = start; i < last; i++) {
                if (o.equals(a[i])) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    @Override
    public int lastIndexOf(final Object o) {
        final E[] a = elements;
        final int start = offset;
        final int last = offset + length;
        if (o == null) {
            for (int i = last - 1; i >= start; i--) {
                if (a[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = last - 1; i >= start; i--) {
                if (o.equals(a[i])) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    private final class ListItr extends ImmutableListIterator<E> {
        
        private final ImmutableArrayList<E> parent = ImmutableArrayList.this;
        
        private final int last = offset + length;
        
        private int i;
        
        ListItr(final int index) {
            Objects.checkIndex(index, length);
            i = offset + index;
        }
        
        @Override
        public boolean hasPrevious() {
            return i > offset;
        }
        
        @Override
        public E previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            return elements[--i];
        }
        
        @Override
        public int nextIndex() {
            return i;
        }
        
        @Override
        public int previousIndex() {
            return i - 1;
        }
        
        @Override
        public boolean hasNext() {
            return i < last;
        }
        
        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return elements[i++];
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }
            //noinspection unchecked
            final ListItr iter = (ListItr) obj;
            return iter.i == i && parent.equals(iter.parent);
        }
        
        @Override
        public int hashCode() {
            return parent.hashCode() * 31 + i;
        }
        
        @Override
        public void forEachRemaining(final Consumer<? super E> action) {
            //noinspection UnnecessaryLocalVariable
            final E[] a = elements;
            final int last = this.last;
            for (int i = this.i; i < last; i++) {
                action.accept(a[i]);
            }
        }
        
        @Override
        public String toString() {
            final int prevIndex = i;
            final StringJoiner sj = new StringJoiner(", \n", "[", "]");
            while (hasNext()) {
                sj.add(String.valueOf(next()));
            }
            i = prevIndex; // return to original state before toString()
            return sj.toString();
        }
        
    }
    
    @Override
    public @NotNull ImmutableListIterator<E> listIterator(final int index) {
        return new ListItr(index);
    }
    
    @Override
    public @NotNull List<E> subList(final int fromIndex, final int toIndex) {
        return new ImmutableArrayList<>(elements, offset + fromIndex, offset + toIndex - fromIndex);
    }
    
    @Override
    public int size() {
        return length;
    }
    
    @Override
    public boolean isEmpty() {
        return length == 0;
    }
    
    @Override
    public @NotNull E[] toArray() {
        return Arrays.copyOfRange(elements, offset, offset + length);
    }
    
    @Override
    public @NotNull <T> T[] toArray(final @NotNull T[] a) {
        // let System.arraycopy take care of type check
        //noinspection SuspiciousSystemArraycopy
        System.arraycopy(elements, offset, a, 0, Math.min(a.length, length));
        return a;
    }
    
    @Override
    public Spliterator<E> spliterator() {
        return Arrays.spliterator(elements, offset, offset + length);
    }
    
    @Override
    public Stream<E> stream() {
        return Arrays.stream(elements, offset, offset + length);
    }
    
    @Override
    public @NotNull ImmutableIterator<E> iterator() {
        return listIterator();
    }
    
    @Override
    public void forEach(final Consumer<? super E> action) {
        Objects.requireNonNull(action);
        //noinspection UnnecessaryLocalVariable
        final E[] a = elements;
        final int last = offset + length;
        for (int i = offset; i < last; i++) {
            action.accept(a[i]);
        }
    }
    
}