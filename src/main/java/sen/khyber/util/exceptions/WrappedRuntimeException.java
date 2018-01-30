package sen.khyber.util.exceptions;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Created by Khyber Sen on 1/30/2018.
 *
 * @author Khyber Sen
 */
@Accessors(fluent = true, chain = false)
public class WrappedRuntimeException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    private final @Getter Throwable cause;
    
    public WrappedRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
        this.cause = cause;
    }
    
    public WrappedRuntimeException(final Throwable cause) {
        super(cause);
        this.cause = cause;
    }
    
}