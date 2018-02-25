package sen.khyber.web.subway.client.status;

import sen.khyber.io.SerializeableEnum;
import sen.khyber.io.SizeType;
import sen.khyber.unsafe.buffers.UnsafeBuffer;
import sen.khyber.util.StringBuilderAppendable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.nio.ByteBuffer;
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
public enum MTAStatus implements StringBuilderAppendable, SerializeableEnum<MTAStatus> {
    
    GOOD_SERVICE("GOOD SERVICE"),
    PLANNED_WORK("PLANNED WORK"),
    DELAYS("DELAYS"),
    SERVICE_CHANGE("SERVICE CHANGE"),
    //
    ;
    
    private final @NotNull @Getter String officialName;
    
    @Override
    public @NotNull StringBuilder appendSelf(final @NotNull StringBuilder sb) {
        return sb.append(officialName);
    }
    
    @Override
    public final @NotNull StringBuilder append(@NotNull final StringBuilder sb) {
        sb.append("MTAStatus[");
        appendSelf(sb);
        sb.append(']');
        return sb;
    }
    
    @Override
    public final @NotNull String toString() {
        return defaultToString();
    }
    
    @Override
    public final @NotNull SizeType sizeType() {
        return SizeType.BYTE;
    }
    
    @SuppressWarnings("rawtypes")
    private static final @NotNull Map<String, MTAStatus> parserMap = Map.ofEntries(
            Arrays.stream(values())
                    .map(status -> Pair.of(status.officialName, status))
                    .toArray(Pair[]::new)
    );
    
    public static @Nullable MTAStatus parse(final String officialName) {
        return parserMap.get(officialName);
    }
    
    public static @NotNull MTAStatus deserialize(final @NotNull ByteBuffer in) {
        return SerializeableEnum.deserialize(MTAStatus.class, in);
    }
    
    public static @NotNull MTAStatus deserialize(final @NotNull UnsafeBuffer in) {
        return SerializeableEnum.deserialize(MTAStatus.class, in);
    }
    
}