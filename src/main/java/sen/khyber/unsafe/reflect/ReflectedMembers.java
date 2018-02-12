package sen.khyber.unsafe.reflect;

import sen.khyber.util.immutable.ImmutableArrayList;
import sen.khyber.util.immutable.ImmutableList;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
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
@Getter(onMethod = @__(@NotNull))
public abstract class ReflectedMembers<T extends AccessibleObject & Member, Handle> {
    
    private final @NotNull Class<?> klass;
    private final @NotNull T[] rawMembers;
    private final @NotNull ImmutableList<? extends ReflectedMember<T, Handle>> members;
    // TODO use ImmutableType
    protected final @NotNull Map<String, ? extends ReflectedMember<T, Handle>> membersMap;
    
    @NotNull
    abstract ReflectedMember<T, Handle> reflectMember(T member);
    
    @SuppressWarnings("unchecked")
    ReflectedMembers(final @NotNull Class<?> klass, final @NotNull MemberType memberType) {
        this.klass = klass;
        rawMembers = memberType.rawMembers(klass);
        members = new ImmutableArrayList<ReflectedMember<T, Handle>>(
                Stream.of(rawMembers)
                        .map(this::reflectMember)
                        .toArray(ReflectedMember[]::new)
        );
        membersMap = Map.ofEntries(
                members.stream()
                        .map(member -> Pair.of(member.name(), member))
                        .toArray(Pair[]::new)
        );
        members();
    }
    
    @Nullable
    public ReflectedMember<T, Handle> member(final @NotNull String name) {
        Objects.requireNonNull(name);
        return membersMap.get(name);
    }
    
    @Nullable
    public final T rawMember(final @NotNull String name) {
        final ReflectedMember<T, Handle> reflectedMember = member(name);
        return reflectedMember == null ? null : reflectedMember.member();
    }
    
    public final boolean hasMember(final @NotNull String name) {
        Objects.requireNonNull(name);
        return member(name) != null;
    }
    
}