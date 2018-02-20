package sen.khyber.io;

import java.io.IOException;

/**
 * Created by Khyber Sen on 2/18/2018.
 *
 * @author Khyber Sen
 */
@FunctionalInterface
public interface IOSupplier<T> {
    
    public T get() throws IOException;
    
}