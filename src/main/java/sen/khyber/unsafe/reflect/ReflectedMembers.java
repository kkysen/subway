package sen.khyber.unsafe.reflect;

import sen.khyber.util.collections.immutable.ImmutableArrayList;
import sen.khyber.util.collections.immutable.ImmutableList;
import sen.khyber.util.exceptions.ExceptionUtils;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.lang.invoke.VarHandle;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Khyber Sen on 2/11/2018.
 *
 * @author Khyber Sen
 */
@Accessors(fluent = true)
public abstract class ReflectedMembers<T extends AccessibleObject & Member, Signature, Handle> {
    
    private static final @NotNull ReflectedField immutableMapTable;
    
    static {
        //noinspection OverwrittenKey
        final Class<?> immutableMapClass = Map.of("1", "", "2", "").getClass();
        try {
            // use Class#getDeclaredField b/c this must not use any of the ReflectedMembers code
            immutableMapTable = new ReflectedField(immutableMapClass.getDeclaredField("table"));
        } catch (final NoSuchFieldException e) {
            throw ExceptionUtils.atRuntime(e);
        }
    }
    
    private final @Getter(onMethod = @__(@NotNull)) @NotNull Class<?> klass;
    
    private final @NotNull T[] rawMembers;
    private final @NotNull ReflectedMember<T, Signature, Handle>[] mutableMembers;
    private final @NotNull ImmutableList<? extends ReflectedMember<T, Signature, Handle>> members;
    
    /**
     * The JDK impl. of ImmutableList isn't that performant, so I'm using my own,
     * but the JDK impl. of ImmutableMap is much better
     */
    private final @NotNull Map<Signature, ? extends ReflectedMember<T, Signature, Handle>>
            membersMap;
    
    private @Getter boolean cleared = false;
    
    @SuppressWarnings("unchecked")
    ReflectedMembers(final @NotNull Class<?> klass, final @NotNull MemberType memberType) {
        Objects.requireNonNull(klass);
        Objects.requireNonNull(memberType);
        this.klass = klass;
        rawMembers = memberType.rawMembers(klass);
        //noinspection rawtypes
        mutableMembers = Stream.of(rawMembers)
                .map(this::reflectMember)
                .toArray(ReflectedMember[]::new);
        members = new ImmutableArrayList<>(mutableMembers);
        //noinspection rawtypes
        membersMap = Map.ofEntries(
                members.stream()
                        .map(member -> Pair.of(member.signature(), member))
                        .toArray(Pair[]::new)
        );
    }
    
    private void checkCleared() {
        if (cleared) {
            throw new IllegalStateException("cleared");
        }
    }
    
    abstract @NotNull ReflectedMember<T, Signature, Handle> reflectMember(T member);
    
    public final @NotNull T[] rawMembers() {
        checkCleared();
        return rawMembers;
    }
    
    public @NotNull ImmutableList<? extends ReflectedMember<T, Signature, Handle>> members() {
        checkCleared();
        return members;
    }
    
    public @NotNull Map<Signature, ? extends ReflectedMember<T, Signature, Handle>> membersMap() {
        checkCleared();
        return membersMap;
    }
    
    public @Nullable ReflectedMember<T, Signature, Handle> member(
            final @NotNull Signature signature) {
        Objects.requireNonNull(signature);
        return membersMap().get(signature);
    }
    
    public final @Nullable T rawMember(final @NotNull Signature signature) {
        final ReflectedMember<T, Signature, Handle> reflectedMember = member(signature);
        return reflectedMember == null ? null : reflectedMember.member();
    }
    
    public final boolean hasMember(final @NotNull Signature signature) {
        return member(signature) != null;
    }
    
    final void clear() {
        cleared = true;
        Arrays.fill(rawMembers, null);
        Arrays.fill(mutableMembers, null);
        final VarHandle handle = immutableMapTable.handle();
        final Object[] table = (Object[]) immutableMapTable.bind(membersMap).getObject();
        Arrays.fill(table, null);
    }
    
}