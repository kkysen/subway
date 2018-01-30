package sen.khyber.util.exceptions;

/**
 * Created by Khyber Sen on 1/30/2018.
 *
 * @author Khyber Sen
 */
public final class ExceptionUtils {
    
    private ExceptionUtils() {}
    
    public static WrappedRuntimeException atRuntime(final Throwable cause) {
        if (cause instanceof WrappedRuntimeException) {
            return (WrappedRuntimeException) cause;
        }
        return new WrappedRuntimeException(cause);
    }
    
    public static WrappedRuntimeException atRuntime(final Throwable cause, final String message) {
        return new WrappedRuntimeException(message, cause);
    }
    
}