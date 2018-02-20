package sen.khyber.web.subway.client.status;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/18/2018.
 *
 * @author Khyber Sen
 */
@Accessors(fluent = true)
public enum LIRRLine implements MTALine<LIRRLine> {
    
    Babylon("Babylon"),
    CityTerminalZone("City Terminal Zone"),
    FarRockaway("Far Rockaway"),
    Hempstead("Hempstead"),
    LongBeach("Long Beach"),
    Montauk("Montauk"),
    OysterBay("Oyster Bay"),
    PortJefferson("Port Jefferson"),
    PortWashington("Port Washington"),
    Ronkonkoma("Ronkonkoma"),
    WestHempstead("West Hempstead"),
    //
    ;
    
    private @Getter @NotNull final MTALineStatusRef lineStatus = new MTALineStatusRef(this);
    
    private final @Getter @NotNull String officialName;
    
    private LIRRLine(final @NotNull String officialName) {
        Objects.requireNonNull(officialName);
        this.officialName = officialName;
    }
    
    @Override
    public final @NotNull MTAType type() {
        return MTAType.LIRR;
    }
    
    @Override
    public final @NotNull String toString() {
        return append(new StringBuilder()).toString();
    }
    
}