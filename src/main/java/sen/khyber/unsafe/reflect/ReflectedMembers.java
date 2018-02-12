package sen.khyber.unsafe.reflect;

import sen.khyber.util.exceptions.ExceptionUtils;
import sen.khyber.util.immutable.ImmutableArrayList;
import sen.khyber.util.immutable.ImmutableList;

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
public abstract class ReflectedMembers<T extends AccessibleObject & Member, Handle> {
    
    private static final @NotNull ReflectedField immutableMapTable;
    
    static {
        //noinspection OverwrittenKey
        final Class<?> immutableMapClass = Map.of("", "", "", "").getClass();
        try {
            // use Class#getDeclaredField b/c this must not use any of the ReflectedMembers code
            immutableMapTable = new ReflectedField(immutableMapClass.getDeclaredField("table"));
        } catch (final NoSuchFieldException e) {
            throw ExceptionUtils.atRuntime(e);
        }
    }
    
    private final @NotNull @Getter(onMethod = @__(@NotNull)) Class<?> klass;
    
    private final @NotNull T[] rawMembers;
    private final @NotNull ReflectedMember<T, Handle>[] mutableMembers;
    private final @NotNull ImmutableList<? extends ReflectedMember<T, Handle>> members;
    
    /**
     * The JDK impl. of ImmutableList isn't that performant, so I'm using my own,
     * but the JDK impl. of ImmutableMap is much better
     */
    private final @NotNull Map<String, ? extends ReflectedMember<T, Handle>> membersMap;
    
    private @Getter boolean cleared = false;
    
    @SuppressWarnings("unchecked")
    ReflectedMembers(final @NotNull Class<?> klass, final @NotNull MemberType memberType) {
        this.klass = klass;
        rawMembers = memberType.rawMembers(klass);
        mutableMembers = Stream.of(rawMembers)
                .map(this::reflectMember)
                .toArray(ReflectedMember[]::new);
        members = new ImmutableArrayList<>(mutableMembers);
        membersMap = Map.ofEntries(
                members.stream()
                        .map(member -> Pair.of(member.name(), member))
                        .toArray(Pair[]::new)
        );
    }
    
    private void checkCleared() {
        if (cleared) {
            throw new IllegalStateException("cleared");
        }
    }
    
    @NotNull
    abstract ReflectedMember<T, Handle> reflectMember(T member);
    
    @NotNull
    public final T[] rawMembers() {
        checkCleared();
        return rawMembers;
    }
    
    @NotNull
    public ImmutableList<? extends ReflectedMember<T, Handle>> members() {
        checkCleared();
        return members;
    }
    
    @NotNull
    public Map<String, ? extends ReflectedMember<T, Handle>> membersMap() {
        checkCleared();
        return membersMap;
    }
    
    @Nullable
    public ReflectedMember<T, Handle> member(final @NotNull String name) {
        Objects.requireNonNull(name);
        return membersMap().get(name);
    }
    
    @Nullable
    public final T rawMember(final @NotNull String name) {
        final ReflectedMember<T, Handle> reflectedMember = member(name);
        return reflectedMember == null ? null : reflectedMember.member();
    }
    
    public final boolean hasMember(final @NotNull String name) {
        return member(name) != null;
    }
    
    void clear() {
        cleared = true;
        Arrays.fill(rawMembers, null);
        Arrays.fill(mutableMembers, null);
        final VarHandle handle = immutableMapTable.handle();
        final Object[] table = (Object[]) immutableMapTable.bind(membersMap).getObject();
        Arrays.fill(table, null);
    }
    
}