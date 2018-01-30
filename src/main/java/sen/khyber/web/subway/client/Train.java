package sen.khyber.web.subway.client;

import lombok.RequiredArgsConstructor;

/**
 * Created by Khyber Sen on 1/30/2018.
 *
 * @author Khyber Sen
 */
@RequiredArgsConstructor
public enum Train {
    
    _1("1", Feed.S123456),
    _2("2", Feed.S123456),
    _3("3", Feed.S123456),
    _4("4", Feed.S123456),
    _5("5", Feed.S123456),
    _6("6", Feed.S123456),
    S("S", Feed.S123456),
    
    L("L", Feed.L),
    
    N("N", Feed.NQRW),
    Q("Q", Feed.NQRW),
    R("R", Feed.NQRW),
    W("W", Feed.NQRW),
    
    B("B", Feed.BDFM),
    D("D", Feed.BDFM),
    F("F", Feed.BDFM),
    M("M", Feed.BDFM),
    
    A("A", Feed.ACE),
    C("C", Feed.ACE),
    E("E", Feed.ACE),
    
    G("G", Feed.G),
    
    J("J", Feed.JZ),
    Z("Z", Feed.JZ),
    
    _7("7", Feed._7),;
    
    private final String name;
    private final Feed feed;
    
}