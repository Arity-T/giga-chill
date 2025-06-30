package com.github.giga_chill.gigachill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties()
public class GigachillApplication {

	public static void main(String[] args) {
		SpringApplication.run(GigachillApplication.class, args);
	}

}
