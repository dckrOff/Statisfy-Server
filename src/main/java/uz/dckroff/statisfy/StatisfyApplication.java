package uz.dckroff.statisfy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StatisfyApplication {

    public static void main(String[] args) {
        SpringApplication.run(StatisfyApplication.class, args);
    }

}
