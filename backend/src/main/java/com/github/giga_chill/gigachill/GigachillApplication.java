package com.github.giga_chill.gigachill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.github.giga_chill.gigachill.config.DotenvInitializer;

@SpringBootApplication
@EnableConfigurationProperties()
public class GigachillApplication {

	public static void main(String[] args) {
			SpringApplication app = new SpringApplication(GigachillApplication.class);
			app.addInitializers(new DotenvInitializer());
			app.run(args);
	}

}
