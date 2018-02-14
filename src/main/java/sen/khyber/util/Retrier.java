package sen.khyber.util;

import sen.khyber.util.collections.mapped.MappedList;
import sen.khyber.util.function.IntBinaryPredicate;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import java.util.logging.Logger;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Created by Khyber Sen on 1/31/2018.
 *
 * @author Khyber Sen
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Retrier<T, R> {
    
    private final BiConsumer<List<T>, List<R>> trier;
    private final Function<R, T> reconverter;
    
    private final IntBinaryPredicate stopTrying;
    private final IntUnaryOperator sleepLength;
    
    private final Logger log;
    
    private boolean tryOnce(final List<T> unfinished, final List<R> failedSynced, int attemptNum) {
        Parallelism.set(unfinished.size());
        if (log != null) {
            log.info("attempt " + (attemptNum + 1) + ", "
                    + unfinished.size() + " left");
        }
        trier.accept(unfinished, failedSynced);
        return stopTrying.applyAsInt(++attemptNum, failedSynced.size());
    }
    
    private void sleep(final int attemptNum) {
        try {
            Thread.sleep(sleepLength.applyAsInt(attemptNum));
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    // identity case can be more efficient by reducing copying between unfinished and failed lists
    private List<T> keepTryingIdentity(List<T> unfinished, List<T> failed,
            final Supplier<List<T>> newListSupplier) {
        //noinspection unchecked
        final BiConsumer<List<T>, List<T>> trier =
                (BiConsumer<List<T>, List<T>>) (BiConsumer<?, ?>) this.trier;
        List<T> failedSynced = Collections.synchronizedList(failed);
        int attemptNum = 0;
        while (!tryOnce(unfinished, (List<R>) failedSynced, attemptNum)) {
            //noinspection unchecked
            if (newListSupplier != null && attemptNum == 1) {
                unfinished = failed;
                //noinspection unchecked
                failed = newListSupplier.get();
                failedSynced = Collections.synchronizedList(failed);
            } else {
                final List<T> temp = unfinished;
                unfinished = failed;
                temp.clear();
                failed = temp;
                failedSynced = Collections.synchronizedList(failed);
            }
            sleep(attemptNum++);
        }
        Parallelism.reset();
        return failed;
    }
    
    private List<R> keepTrying(List<T> unfinished, List<R> failed,
            final Supplier<List<R>> newListSupplier) {
        if (reconverter == Function.identity()) {
            //noinspection unchecked
            return (List<R>) keepTryingIdentity(unfinished, (List<T>) failed,
                    (Supplier<List<T>>) (Supplier<?>) newListSupplier);
        }
        final List<T> mappedFailed = new MappedList<>(failed, reconverter);
        List<R> failedSynced = Collections.synchronizedList(failed);
        int attemptNum = 0;
        while (!tryOnce(unfinished, failedSynced, attemptNum)) {
            if (newListSupplier != null && attemptNum == 1) {
                unfinished = new MappedList<>(failed, reconverter);
                //noinspection unchecked
                failed = newListSupplier.get();
                failedSynced = Collections.synchronizedList(failed);
            } else {
                final List<T> temp = unfinished;
                unfinished = new MappedList<>(failed, reconverter);
                temp.clear();
                //noinspection unchecked
                failed = (List<R>) temp;
                failedSynced = Collections.synchronizedList(failed);
            }
            sleep(attemptNum++);
        }
        Parallelism.reset();
        return failed;
    }
    
    public List<R> keepTrying(final List<T> unfinished, final List<R> failed) {
        return keepTrying(unfinished, failed, null);
    }
    
    public List<R> keepTrying(final List<T> unfinished) {
        return keepTrying(unfinished, new ArrayList<>());
    }
    
    public List<R> keepTrying(final T[] unfinished) {
        return keepTrying(Arrays.asList(unfinished), new ArrayList<>(), ArrayList::new);
    }
    
    private static final RetrierBuilders<?> BUILDERS = new RetrierBuilders();
    
    @SuppressWarnings("unchecked")
    public static <T> RetrierBuilders<T> builders() {
        return (RetrierBuilders<T>) BUILDERS;
    }
    
    public static <T> RetrierBuilders<T> builders(final Class<T> klass) {
        return builders();
    }
    
    public static final class RetrierBuilders<T> {
        
        private RetrierBuilders() {}
        
        public <R> RetrierBuilder<T, R> simple(final BiConsumer<List<T>, List<R>> trier,
                final Function<R, T> reconverter) {
            return new RetrierBuilder<>(trier, reconverter);
        }
        
        public RetrierBuilder<T, T> identity(final BiConsumer<List<T>, List<T>> trier) {
            return new RetrierBuilder<>(trier, Function.identity());
        }
        
        public <Ex extends Throwable> RetrierBuilder<T, Pair<T, Ex>> exceptional(
                final BiConsumer<List<T>, List<Pair<T, Ex>>> trier) {
            return new RetrierBuilder<>(trier, Pair::getLeft);
        }
        
        public RetrierBuilder<T, Pair<T, Exception>> allExceptional(
                final BiConsumer<List<T>, List<Pair<T, Exception>>> trier) {
            return exceptional(trier);
        }
        
        public RetrierBuilder<T, Pair<T, RuntimeException>> runtimeExceptional(
                final BiConsumer<List<T>, List<Pair<T, RuntimeException>>> trier) {
            return exceptional(trier);
        }
        
        public RetrierBuilder<T, Pair<T, IOException>> exceptionalIO(
                final BiConsumer<List<T>, List<Pair<T, IOException>>> trier) {
            return exceptional(trier);
        }
        
    }
    
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class RetrierBuilder<T, R> {
        
        @NonNull private final BiConsumer<List<T>, List<R>> trier;
        
        @NonNull private final Function<R, T> reconverter;
        
        private IntBinaryPredicate stopTrying;
        private IntUnaryOperator sleepLength;
        
        private Integer maxAttempts;
        private Integer acceptableNumFailures;
        
        private Logger log;
        
        private static void checkNonNegative(final int i, final String name) {
            if (i < 0) {
                throw new IllegalArgumentException(name + " can't be negative");
            }
        }
        
        public RetrierBuilder<T, R> stopTrying(final IntBinaryPredicate stopTrying) {
            Objects.requireNonNull(stopTrying);
            this.stopTrying = stopTrying;
            return this;
        }
        
        public RetrierBuilder<T, R> stopTrying(final int maxAttempts,
                final int acceptableNumFailures) {
            checkNonNegative(maxAttempts, "maxAttempts");
            checkNonNegative(acceptableNumFailures, "acceptableNumFailures");
            return stopTrying((numAttempts, numFailures) ->
                    numAttempts >= maxAttempts || numFailures <= acceptableNumFailures);
        }
        
        public RetrierBuilder<T, R> maxAttempts(final int maxAttempts) {
            return stopTrying(maxAttempts, 0);
        }
        
        public RetrierBuilder<T, R> acceptableNumFailures(final int acceptableNumFailures) {
            return stopTrying(Integer.MAX_VALUE, acceptableNumFailures);
        }
        
        public RetrierBuilder<T, R> sleepLength(final IntUnaryOperator sleepLength) {
            Objects.requireNonNull(sleepLength);
            this.sleepLength = sleepLength;
            return this;
        }
        
        public RetrierBuilder<T, R> sleepLength(final int sleepLength) {
            checkNonNegative(sleepLength, "sleepLength");
            return sleepLength(i -> sleepLength);
        }
        
        public RetrierBuilder<T, R> sleepLengthMultiplied(final int sleepLength) {
            checkNonNegative(sleepLength, "sleepLength");
            return sleepLength(i -> i * sleepLength);
        }
        
        public RetrierBuilder<T, R> log(final Logger log) {
            this.log = log;
            return this;
        }
        
        public Retrier<T, R> build() {
            Objects.requireNonNull(trier);
            if (sleepLength == null) {
                sleepLength(1000);
            }
            if (maxAttempts == null) {
                maxAttempts = Integer.MAX_VALUE;
            }
            if (acceptableNumFailures == null) {
                acceptableNumFailures = Integer.MIN_VALUE;
            }
            return new Retrier<>(trier, reconverter, stopTrying, sleepLength, log);
        }
        
    }
    
}