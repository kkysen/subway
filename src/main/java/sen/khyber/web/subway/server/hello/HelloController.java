package sen.khyber.web.subway.server.hello;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Khyber Sen on 1/29/2018.
 *
 * @author Khyber Sen
 */
@RestController
public class HelloController {
    
    @RequestMapping("/hello")
    public String index() {
        return "Hello, World";
    }
    
}