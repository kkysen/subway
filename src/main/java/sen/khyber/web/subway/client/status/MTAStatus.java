package sen.khyber.web.subway.client.status;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Created by Khyber Sen on 2/17/2018.
 *
 * @author Khyber Sen
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true)
public enum MTAStatus {
    
    GOOD_SERVICE("GOOD SERVICE"),
    PLANNED_WORK("PLANNED WORK"),
    DELAYS("DELAYS"),
    SERVICE_CHANGE("SERVICE CHANGE"),
    //
    ;
    
    private final @NotNull @Getter String officialName;
    
    @SuppressWarnings("rawtypes")
    private static final @NotNull Map<String, MTAStatus> parserMap = Map.ofEntries(
            Arrays.stream(values())
                    .map(status -> Pair.of(status.officialName, status))
                    .toArray(Pair[]::new)
    );
    
    public static @Nullable MTAStatus parse(final String officialName) {
        return parserMap.get(officialName);
    }
    
    @Override
    public final String toString() {
        return "MTAStatus[" + officialName + ']';
    }
    
    private void writeObject(final ObjectOutputStream out) throws NotSerializableException {
        throw new NotSerializableException(getClass().getName());
    }
    
}