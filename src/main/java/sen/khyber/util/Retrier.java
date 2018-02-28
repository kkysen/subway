package sen.khyber.util;

import sen.khyber.unsafe.fields.ArrayListUtils;
import sen.khyber.util.collections.mapped.MappedList;
import sen.khyber.util.function.IntBinaryPredicate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import java.util.logging.Logger;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Khyber Sen on 1/31/2018.
 *
 * @author Khyber Sen
 */
public final class Retrier<T, R> {
    
    private final @NotNull BiConsumer<List<T>, List<R>> trier;
    private final @NotNull Function<R, T> reconverter;
    
    private final @NotNull IntBinaryPredicate stopTrying;
    private final @NotNull IntUnaryOperator sleepLength;
    
    private final @Nullable Logger log;
    
    private Retrier(
            final @NotNull BiConsumer<List<T>, List<R>> trier,
            final @NotNull Function<R, T> reconverter,
            final @NotNull IntBinaryPredicate stopTrying,
            final @NotNull IntUnaryOperator sleepLength,
            final @Nullable Logger log) {
        ObjectUtils.requireNonNull(trier, reconverter, stopTrying, sleepLength);
        this.trier = trier;
        this.reconverter = reconverter;
        this.stopTrying = stopTrying;
        this.sleepLength = sleepLength;
        this.log = log;
    }
    
    private boolean tryOnce(final @NotNull List<T> unfinished, final @NotNull List<R> failedSynced,
            int attemptNum) {
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
    private List<T> keepTryingIdentity(@NotNull List<T> unfinished, @NotNull List<T> failed,
            final @Nullable Supplier<List<T>> newListSupplier) {
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
    
    private List<R> keepTrying(@NotNull List<T> unfinished, @NotNull List<R> failed,
            final @Nullable Supplier<List<R>> newListSupplier) {
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
    
    public final List<R> keepTrying(final @NotNull List<T> unfinished,
            final @NotNull List<R> failed) {
        return keepTrying(unfinished, failed, null);
    }
    
    public final List<R> keepTrying(final @NotNull List<T> unfinished) {
        Objects.requireNonNull(unfinished);
        return keepTrying(unfinished, new ArrayList<>());
    }
    
    public final List<R> keepTrying(final T[] unfinished) {
        Objects.requireNonNull(unfinished);
        return keepTrying(ArrayListUtils.wrap(unfinished), new ArrayList<>(), ArrayList::new);
    }
    
    private static final RetrierBuilders<?> BUILDERS = new RetrierBuilders();
    
    @SuppressWarnings("unchecked")
    public static final <T> RetrierBuilders<T> builders() {
        return (RetrierBuilders<T>) BUILDERS;
    }
    
    public static final <T> RetrierBuilders<T> builders(final @NotNull Class<T> klass) {
        return builders();
    }
    
    public static final class RetrierBuilders<T> {
        
        private RetrierBuilders() {}
        
        public final <R> @NotNull RetrierBuilder<T, R> simple(
                final @NotNull BiConsumer<List<T>, List<R>> trier,
                final @NotNull Function<R, T> reconverter) {
            return new RetrierBuilder<>(trier, reconverter);
        }
        
        public final @NotNull RetrierBuilder<T, T> identity(
                final @NotNull BiConsumer<List<T>, List<T>> trier) {
            return new RetrierBuilder<>(trier, Function.identity());
        }
        
        public final <Ex extends Throwable> @NotNull RetrierBuilder<T, Pair<T, Ex>> exceptional(
                final @NotNull BiConsumer<List<T>, List<Pair<T, Ex>>> trier) {
            return new RetrierBuilder<>(trier, Pair::getLeft);
        }
        
        public final RetrierBuilder<T, Pair<T, Exception>> allExceptional(
                final @NotNull BiConsumer<List<T>, List<Pair<T, Exception>>> trier) {
            return exceptional(trier);
        }
        
        public final RetrierBuilder<T, Pair<T, RuntimeException>> runtimeExceptional(
                final @NotNull BiConsumer<List<T>, List<Pair<T, RuntimeException>>> trier) {
            return exceptional(trier);
        }
        
        public final RetrierBuilder<T, Pair<T, IOException>> exceptionalIO(
                final @NotNull BiConsumer<List<T>, List<Pair<T, IOException>>> trier) {
            return exceptional(trier);
        }
        
    }
    
    public static final class RetrierBuilder<T, R> {
        
        private final @NotNull BiConsumer<List<T>, List<R>> trier;
        
        private final @NotNull Function<R, T> reconverter;
        
        private IntBinaryPredicate stopTrying;
        private IntUnaryOperator sleepLengthMillis;
        
        private Integer maxAttempts;
        private Integer acceptableNumFailures;
        
        private Logger log;
        
        private RetrierBuilder(final @NotNull BiConsumer<List<T>, List<R>> trier,
                final @NotNull Function<R, T> reconverter) {
            Objects.requireNonNull(trier);
            Objects.requireNonNull(reconverter);
            this.trier = trier;
            this.reconverter = reconverter;
        }
        
        private static void checkNonNegative(final int i, final @NotNull String name) {
            if (i < 0) {
                throw new IllegalArgumentException(name + " can't be negative");
            }
        }
        
        public final @NotNull RetrierBuilder<T, R> stopTrying(
                final @NotNull IntBinaryPredicate stopTrying) {
            Objects.requireNonNull(stopTrying);
            if (this.stopTrying == null) {
                this.stopTrying = stopTrying;
            } else {
                this.stopTrying = this.stopTrying.or(stopTrying);
            }
            return this;
        }
        
        public final @NotNull RetrierBuilder<T, R> stopTrying(final int maxAttempts,
                final int acceptableNumFailures) {
            checkNonNegative(maxAttempts, "maxAttempts");
            checkNonNegative(acceptableNumFailures, "acceptableNumFailures");
            return stopTrying((numAttempts, numFailures) ->
                    numAttempts >= maxAttempts || numFailures <= acceptableNumFailures);
        }
        
        public final @NotNull RetrierBuilder<T, R> maxAttempts(final int maxAttempts) {
            return stopTrying(maxAttempts, 0);
        }
        
        public final @NotNull RetrierBuilder<T, R> acceptableNumFailures(
                final int acceptableNumFailures) {
            return stopTrying(Integer.MAX_VALUE, acceptableNumFailures);
        }
        
        public final @NotNull RetrierBuilder<T, R> sleepLengthMillis(
                final @NotNull IntUnaryOperator sleepLengthMillis) {
            Objects.requireNonNull(sleepLengthMillis);
            this.sleepLengthMillis = sleepLengthMillis;
            return this;
        }
        
        public final @NotNull RetrierBuilder<T, R> sleepLengthMillis(final int sleepLengthMillis) {
            checkNonNegative(sleepLengthMillis, "sleepLengthMillis");
            return sleepLengthMillis(i -> sleepLengthMillis);
        }
        
        public final @NotNull RetrierBuilder<T, R> sleepLengthMillisMultiplied(
                final int sleepLengthMillis) {
            checkNonNegative(sleepLengthMillis, "sleepLengthMillis");
            return sleepLengthMillis(i -> i * sleepLengthMillis);
        }
        
        public final @NotNull RetrierBuilder<T, R> log(final @Nullable Logger log) {
            this.log = log;
            return this;
        }
        
        public final @NotNull Retrier<T, R> build() {
            if (sleepLengthMillis == null) {
                sleepLengthMillis(1000);
            }
            if (stopTrying == null) {
                maxAttempts = Integer.MAX_VALUE;
                acceptableNumFailures = Integer.MIN_VALUE;
                stopTrying = (numAttempts, numFailures) ->
                        numAttempts >= maxAttempts || numFailures <= acceptableNumFailures;
            }
            return new Retrier<>(trier, reconverter, stopTrying, sleepLengthMillis, log);
        }
        
    }
    
}