package sen.khyber.web.subway.server;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class SubwayApplication {
    
    public static void main(final String[] args) {
        SpringApplication.run(SubwayApplication.class, args);
    }
    
    @Bean
    public CommandLineRunner commandLineRunner(final ApplicationContext context) {
        return args -> {
            System.out.println("Spring Boot Beans:");
            final String[] beanNames = context.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (final String beanName : beanNames) {
                System.out.println(beanName);
            }
        };
    }
    
}
