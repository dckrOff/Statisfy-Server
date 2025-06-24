package uz.dckroff.statisfy;

import com.fasterxml.jackson.databind.MapperFeature;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StatisfyApplication {

    public static void main(String[] args) {
        SpringApplication.run(StatisfyApplication.class, args);
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer disableRequireHandlersForJava8Times() {
        return builder -> builder.featuresToDisable(MapperFeature.REQUIRE_HANDLERS_FOR_JAVA8_TIMES);
    }
}
