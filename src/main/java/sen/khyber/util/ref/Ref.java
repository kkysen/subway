package sen.khyber.util.ref;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/18/2018.
 *
 * @author Khyber Sen
 */
public class Ref<T> {
    
    @SuppressWarnings("NullableProblems")
    private @NotNull T value;
    
    public Ref(final @NotNull T value) {
        set(value);
    }
    
    public @NotNull T get() {
        return value;
    }
    
    public void set(final @NotNull T value) {
        Objects.requireNonNull(value);
        this.value = value;
    }
    
    public @NotNull T setGet(final @NotNull T value) {
        final T old = this.value;
        set(value);
        return old;
    }
    
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object obj) {
        return value.equals(obj);
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
    
}