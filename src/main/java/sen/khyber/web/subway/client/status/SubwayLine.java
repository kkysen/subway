package sen.khyber.web.subway.client.status;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/17/2018.
 *
 * @author Khyber Sen
 */
@Accessors(fluent = true)
public enum SubwayLine implements MTALine<SubwayLine> {
    
    _123("123"),
    _456("456"),
    _7("7"),
    ACE("ACE"),
    BDFM("BDFM"),
    G("G"),
    JZ("JZ"),
    L("L"),
    NQR("NQR"),
    // FIXME in serviceStatus.txt this is just NQR, but in the API, it's NQRW
    S("S"),
    SIR("SIR"),
    //
    ;
    
    private @Getter @NotNull final MTALineStatusRef lineStatus = new MTALineStatusRef(this);
    
    private final @Getter @NotNull String officialName;
    
    private SubwayLine(final @NotNull String officialName) {
        Objects.requireNonNull(officialName);
        this.officialName = officialName;
    }
    
    @Override
    public final @NotNull MTAType type() {
        return MTAType.Subway;
    }
    
    @Override
    public final @NotNull String toString() {
        return append(new StringBuilder()).toString();
    }
    
}