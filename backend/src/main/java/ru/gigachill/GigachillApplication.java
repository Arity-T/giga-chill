package ru.gigachill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties()
public class GigachillApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(GigachillApplication.class);
        app.run(args);
    }
}
