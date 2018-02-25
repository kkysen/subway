package sen.khyber.io;

import sen.khyber.unsafe.buffers.UnsafeBuffer;
import sen.khyber.unsafe.buffers.UnsafeSerializable;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/23/2018.
 *
 * @author Khyber Sen
 */
public interface SerializeableEnum<T extends Enum<T>> extends UnsafeSerializable {
    
    public static final class EnumSizeType {
        
        private EnumSizeType() {}
        
        private static final Map<Class<? extends Enum<?>>, SizeType> cache = new HashMap<>();
        
        private static int mapToSize(final @NotNull Class<? extends Enum<?>> enumClass) {
            return enumClass.getEnumConstants().length;
        }
        
        private static @NotNull SizeType map(
                final @NotNull Class<? extends Enum<?>> enumClass) {
            return SizeType.map(enumClass.getEnumConstants().length);
        }
        
        public static final @NotNull SizeType get(
                final @NotNull Class<? extends Enum<?>> enumClass) {
            return cache.computeIfAbsent(enumClass, EnumSizeType::map);
        }
        
    }
    
    public static <T extends Enum<T>> @NotNull T deserialize(
            final @NotNull Class<T> enumClass,
            final @NotNull ByteBuffer buffer) {
        return enumClass.getEnumConstants()[EnumSizeType.map(enumClass).deserializeOrdinal(buffer)];
    }
    
    public static <T extends Enum<T>> @NotNull T deserialize(
            final @NotNull Class<T> enumClass,
            final @NotNull UnsafeBuffer buffer) {
        return enumClass.getEnumConstants()[EnumSizeType.map(enumClass).deserializeOrdinal(buffer)];
    }
    
    public default @NotNull Class<T> enumClass() {
        return (Class<T>) getClass();
    }
    
    public default @NotNull SizeType sizeType() {
        return EnumSizeType.get(enumClass());
    }
    
    @Override
    public default long serializedLongLength() {
        return sizeType().numBytes();
    }
    
    @Override
    public default void serialize(final @NotNull ByteBuffer out) {
        sizeType().serialize(out, EnumSizeType.mapToSize(enumClass()));
    }
    
    @Override
    public default void serializeUnsafe(final @NotNull UnsafeBuffer out) {
        sizeType().serialize(out, EnumSizeType.mapToSize(enumClass()));
    }
    
}