package sen.khyber.util.collections.mapped;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/1/2018.
 *
 * @author Khyber Sen
 */
@Accessors(fluent = true)
public class MappedSpliterator<T, R> extends Mapper<T, R> implements Spliterator<R> {
    
    private final @Getter Spliterator<? extends T> delegate;
    
    public MappedSpliterator(final @NotNull Spliterator<? extends T> delegate,
            final @NotNull Function<? super T, ? extends R> mapper) {
        super(mapper);
        Objects.requireNonNull(delegate);
        this.delegate = delegate;
    }
    
    public <S> MappedSpliterator<T, S> map(final Function<? super R, ? extends S> mapper) {
        return new MappedSpliterator<>(delegate, this.mapper.andThen(mapper));
    }
    
    @Override
    public boolean tryAdvance(final Consumer<? super R> action) {
        return delegate.tryAdvance(accepting(action));
    }
    
    @Override
    public void forEachRemaining(final Consumer<? super R> action) {
        delegate.forEachRemaining(accepting(action));
    }
    
    @Override
    public Spliterator<R> trySplit() {
        return new MappedSpliterator<>(delegate.trySplit(), mapper);
    }
    
    @Override
    public long estimateSize() {
        return delegate.estimateSize();
    }
    
    @Override
    public int characteristics() {
        return delegate.characteristics();
    }
    
}