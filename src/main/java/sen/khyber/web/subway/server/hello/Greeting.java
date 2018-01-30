package sen.khyber.web.subway.server.hello;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by Khyber Sen on 1/29/2018.
 *
 * @author Khyber Sen
 */
@RequiredArgsConstructor
@Getter
public class Greeting {
    
    private final long id;
    private final String content;
    
}