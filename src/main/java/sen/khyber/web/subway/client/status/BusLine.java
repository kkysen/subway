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
public enum BusLine implements MTALine<BusLine> {
    
    B1_84("B1 - B84"),
    B100_103("B100 - B103"),
    BM1_5("BM1 - BM5"),
    BX1_55("BX1 - BX55"),
    BXM1_18("BXM1 - BXM18"),
    M1_116("M1 - M116"),
    Q1_113("Q1 - Q113"),
    QM1_44("QM1 - QM44"),
    S40_98("S40 - S98"),
    X1_68("x1 - x68"),
    //
    ;
    
    private @Getter @NotNull final MTALineStatusRef lineStatus = new MTALineStatusRef(this);
    
    private final @Getter @NotNull String officialName;
    
    private BusLine(final @NotNull String officialName) {
        Objects.requireNonNull(officialName);
        this.officialName = officialName;
    }
    
    @Override
    public final @NotNull MTAType type() {
        return MTAType.Bus;
    }
    
    @Override
    public final @NotNull String toString() {
        return append(new StringBuilder()).toString();
    }
    
}