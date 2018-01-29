package sen.khyber.subway.hello;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.StackWalker.Option;
import java.lang.StackWalker.StackFrame;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Khyber Sen on 1/29/2018.
 *
 * @author Khyber Sen
 */
@RestController
public class GreetingController {
    
    private final AtomicLong id = new AtomicLong();
    
    private static final List<StackFrame> stack = new ArrayList<>();
    
    @RequestMapping("/greeting")
    public Greeting greeting(
            @RequestParam(value = "name", defaultValue = "World") final String name) {
        if (stack.size() == 0) {
            StackWalker.getInstance(
                    Set.of(Option.RETAIN_CLASS_REFERENCE, Option.SHOW_HIDDEN_FRAMES,
                            Option.SHOW_REFLECT_FRAMES)
            )
                    .forEach(stack::add);
            stack.forEach(System.out::println);
        }
        System.out.println(id);
        return new Greeting(id.incrementAndGet(), "Hello, " + name);
    }
    
}