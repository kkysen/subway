package sen.khyber.util;

/**
 * Created by Khyber Sen on 1/31/2018.
 *
 * @author Khyber Sen
 */
public final class Parallelism {
    
    private Parallelism() {}
    
    private static final String PROPERTY = "java.util.concurrent.ForkJoinPool.common.parallelism";
    
    private static final int ABSOLUTE_MAX_NUM_THREADS = 256 + 128;
    
    private static int maxNumThreads = ABSOLUTE_MAX_NUM_THREADS;
    
    private static final int DEFAULT_PARALLELISM = Runtime.getRuntime().availableProcessors();
    
    private static int between(final int min, final int value, final int max) {
        return Math.min(Math.max(min, value), max);
    }
    
    public static final void setMax(final int numThreads) {
        maxNumThreads = between(DEFAULT_PARALLELISM, numThreads, ABSOLUTE_MAX_NUM_THREADS);
    }
    
    public static final void set(final int numThreads) {
        System.setProperty(PROPERTY,
                String.valueOf(between(DEFAULT_PARALLELISM, numThreads, maxNumThreads)));
    }
    
    public static final void reset() {
        set(DEFAULT_PARALLELISM);
    }
    
    public static final int absoluteMax() {
        return ABSOLUTE_MAX_NUM_THREADS;
    }
    
    public static final int min() {
        return DEFAULT_PARALLELISM;
    }
    
}