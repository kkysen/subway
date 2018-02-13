package sen.khyber.util;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Created by Khyber Sen on 1/31/2018.
 *
 * @author Khyber Sen
 */
@Deprecated
@RequiredArgsConstructor
public class Retrier2<T> {
    
    private final BiConsumer<List<T>, List<T>> trier;
    
    private final int maxAttempts;
    private final int sleepMultiplier;
    private final int failedThreshold;
    
    public Retrier2(final BiConsumer<List<T>, List<T>> tryer,
            final int maxAttempts, final int sleepMultiplier) {
        this(tryer, maxAttempts, sleepMultiplier, 0);
    }
    
    public Retrier2(final BiConsumer<List<T>, List<T>> tryer,
            final int maxAttempts) {
        this(tryer, maxAttempts, 1000);
    }
    
    private List<? extends T> keepTrying(List<T> unfinished, List<T> failed,
            final Supplier<List<T>> newListSupplier) {
        List<T> failedSynced = Collections.synchronizedList(failed);
        int attemptNum = 0;
        for (; ; ) {
            Parallelism.set(unfinished.size());
            System.out.println("attempt " + (attemptNum + 1) + " out of " + maxAttempts + ", "
                    + unfinished.size() + " left");
            trier.accept(unfinished, failedSynced);
            if (++attemptNum == maxAttempts || failed.size() <= failedThreshold) {
                break;
            }
            if (newListSupplier != null && attemptNum == 1) {
                unfinished = failed;
                failed = newListSupplier.get();
                failedSynced = Collections.synchronizedList(failed);
            } else {
                final List<T> temp = unfinished;
                unfinished = failed;
                temp.clear();
                failed = temp;
                failedSynced = Collections.synchronizedList(failed);
            }
            try {
                Thread.sleep(attemptNum++ * sleepMultiplier);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
        Parallelism.reset();
        return failed;
    }
    
    public List<? extends T> keepTrying(final List<T> unfinished, final List<T> failed) {
        return keepTrying(unfinished, failed, null);
    }
    
    public List<? extends T> keepTrying(final List<T> unfinished) {
        return keepTrying(unfinished, new ArrayList<>());
    }
    
    public List<? extends T> keepTrying(final T[] unfinished) {
        return keepTrying(Arrays.asList(unfinished), new ArrayList<>(), ArrayList::new);
    }
    
}