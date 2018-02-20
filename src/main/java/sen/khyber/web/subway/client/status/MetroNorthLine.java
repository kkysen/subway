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
public enum MetroNorthLine implements MTALine<MetroNorthLine> {
    
    Hudson("Hudson"),
    Harlem("Harlem"),
    Wassaic("Wassaic"),
    NewHaven("New Haven"),
    NewCanaan("New Canaan"),
    Danbury("Danbury"),
    Waterbury("Waterbury"),
    PascackValley("Pascack Valley"),
    PortJervis("Port Jervis"),
    //
    ;
    
    private @Getter @NotNull final MTALineStatusRef lineStatus = new MTALineStatusRef(this);
    
    private final @Getter @NotNull String officialName;
    
    private MetroNorthLine(final @NotNull String officialName) {
        Objects.requireNonNull(officialName);
        this.officialName = officialName;
    }
    
    @Override
    public final @NotNull MTAType type() {
        return MTAType.MetroNorth;
    }
    
    @Override
    public final @NotNull String toString() {
        return append(new StringBuilder()).toString();
    }
    
}