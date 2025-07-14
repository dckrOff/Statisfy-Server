package uz.dckroff.statisfy;

import com.fasterxml.jackson.databind.MapperFeature;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@OpenAPIDefinition(
        info = @Info(
                title = "Statisfy API",
                version = "1.0.0",
                description = "API для мобильного приложения с интересными фактами, статистикой и новостями",
                contact = @Contact(
                        name = "Statisfy Team",
                        email = "info@statisfy.uz"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"
                )
        ),
        servers = {
                @Server(url = "/", description = "Default Server URL")
        }
)
public class StatisfyApplication {

    public static void main(String[] args) {
        SpringApplication.run(StatisfyApplication.class, args);
    }
}
