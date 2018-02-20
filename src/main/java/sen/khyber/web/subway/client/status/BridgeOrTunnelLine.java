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
public enum BridgeOrTunnelLine implements MTALine<BridgeOrTunnelLine> {
    
    Whitestone("Bronx-Whitestone"),
    CrossBay("Cross Bay"),
    GeorgeWashington("Henry Hudson"),
    BatteryTunnel("Hugh L. Carey"),
    MarineParkway("Marine Parkway"),
    MidtownTunnel("Queens Midtown"),
    Triboro("Robert F. Kennedy"),
    ThrogsNeck("Throgs Neck"),
    Verrazano("Verrazano-Narrows"),
    //
    ;
    
    private @Getter @NotNull final MTALineStatusRef lineStatus = new MTALineStatusRef(this);
    
    private final @Getter @NotNull String officialName;
    
    private BridgeOrTunnelLine(final @NotNull String officialName) {
        Objects.requireNonNull(officialName);
        this.officialName = officialName;
    }
    
    @Override
    public final @NotNull MTAType type() {
        return MTAType.BridgeOrTunnel;
    }
    
    @Override
    public final @NotNull String toString() {
        return append(new StringBuilder()).toString();
    }
    
}