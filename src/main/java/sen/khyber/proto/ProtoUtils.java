package sen.khyber.proto;

import java.util.function.Consumer;

import com.google.protobuf.ByteString;

/**
 * Created by Khyber Sen on 1/31/2018.
 *
 * @author Khyber Sen
 */
public final class ProtoUtils {
    
    public ProtoUtils() {}
    
    public static final String asString(final Object ref, final Consumer<Object> setter) {
        if (ref instanceof String) {
            return (String) ref;
        } else {
            final ByteString bs = (ByteString) ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                setter.accept(s);
            }
            return s;
        }
    }
    
    public static final ByteString asBytes(final Object ref, final Consumer<Object> setter) {
        if (ref instanceof String) {
            final ByteString b = ByteString.copyFromUtf8((String) ref);
            setter.accept(b);
            return b;
        } else {
            return (ByteString) ref;
        }
    }
    
}