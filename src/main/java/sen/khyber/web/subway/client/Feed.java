package sen.khyber.web.subway.client;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Created by Khyber Sen on 1/30/2018.
 *
 * @author Khyber Sen
 */
@Accessors(fluent = true)
@Getter
public enum Feed {
    
    S123456(1),
    L(2),
    NQRW(16),
    BDFM(21),
    ACE(26),
    G(31),
    JZ(36),
    _7(51),;
    
    private final int feedNumber;
    private final String url;
    private final Train[] trains;
    
    private Feed(final int feedNumber, final Train... trains) {
        this.feedNumber = feedNumber;
        url = "http://datamine.mta.info/mta_esi.php?feed_id=" + feedNumber;
        this.trains = trains;
    }
    
    public final String url(final String apiKey) {
        return url + "&key=" + apiKey;
    }
    
}