package sen.khyber.web.subway.client.status;

import sen.khyber.io.SerializeableEnum;
import sen.khyber.io.SizeType;
import sen.khyber.unsafe.buffers.UnsafeBuffer;
import sen.khyber.util.ObjectUtils;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static sen.khyber.unsafe.fields.ByteBufferUtils.getUnsignedByte;

/**
 * Created by Khyber Sen on 2/18/2018.
 *
 * @author Khyber Sen
 */
@Accessors(fluent = true)
@Getter
public enum MTAType implements MTAEntity, SerializeableEnum<MTAType> {
    
    Subway("subway", SubwayLine.class),
    Bus("bus", BusLine.class),
    BridgeOrTunnel("BT", BridgeOrTunnelLine.class),
    LIRR("LIRR", LIRRLine.class),
    MetroNorth("MetroNorth", MetroNorthLine.class),;
    
    private final @NotNull String officialName;
    private final @NotNull Class<? extends MTALine<?>> lineClass;
    private final @NotNull MTALine<?>[] lines;
    private final int numLines;
    private final int lineOrdinalOffset;
    private final Map<String, MTALine<?>> officialNameMap;
    
    private MTAType(final @NotNull String officialName,
            final @NotNull Class<? extends MTALine<?>> lineClass) {
        ObjectUtils.requireNonNull(officialName, lineClass);
        this.officialName = officialName;
        
        this.lineClass = lineClass;
        lines = lineClass.getEnumConstants();
        numLines = lines.length;
        lineOrdinalOffset = CurrentLineOrdinalOffset.value;
        //noinspection AssignmentToStaticFieldFromInstanceMethod
        CurrentLineOrdinalOffset.value += numLines;
        
        //noinspection rawtypes
        officialNameMap = Map.ofEntries(
                Arrays.stream(lines)
                        .map(line -> Pair.of(line.officialName(), line))
                        .toArray(Pair[]::new)
        );
    }
    
    public final @NotNull MTALine<?> line(final int ordinal) {
        return lines[ordinal];
    }
    
    public final @Nullable MTALine<?> parseLine(final @NotNull String officialName) {
        Objects.requireNonNull(officialName);
        return officialNameMap.get(officialName);
    }
    
    @SuppressWarnings("ClassWithoutConstructor")
    private static final class CurrentLineOrdinalOffset {
        
        private static int value = 0;
        
    }
    
    @Override
    public final @NotNull String toString() {
        return "MTAType[" + name() + ']';
    }
    
    @SuppressWarnings("rawtypes")
    private static final Map<String, MTAType> officialNameToMTAType = Map.ofEntries(
            Arrays.stream(values())
                    .map(type -> Pair.of(type.officialName, type))
                    .toArray(Pair[]::new)
    );
    
    public static final @NotNull MTAType get(final int ordinal) {
        return values()[ordinal];
    }
    
    @Override
    public final @NotNull SizeType sizeType() {
        return SizeType.BYTE;
    }
    
    public static @NotNull MTAType deserialize(final @NotNull ByteBuffer in) {
        return get(getUnsignedByte(in));
    }
    
    public static @NotNull MTAType deserialize(final @NotNull UnsafeBuffer in) {
        return get(in.getUnsignedByte());
    }
    
    public static final @Nullable MTAType parse(final @NotNull String officialName) {
        Objects.requireNonNull(officialName);
        return officialNameToMTAType.get(officialName);
    }
    
    public static final int numTypes() {
        return values().length;
    }
    
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean fitsInByte(final int value) {
        return value < (1 << Byte.SIZE);
    }
    
    private static boolean checkMaxOrdinalsFitInByte() {
        if (!fitsInByte(numTypes())) {
            return false;
        }
        for (final MTAType type : values()) {
            if (!fitsInByte(type.numLines)) {
                return false;
            }
        }
        return true;
    }
    
    static {
        assert checkMaxOrdinalsFitInByte();
    }
    
}