package ru.gigachill.config;

import io.minio.MinioClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.gigachill.properties.MinioProperties;

@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfig {
    @Bean
    MinioClient minioClient(MinioProperties minioProperties) {
        return MinioClient.builder()
                .endpoint(minioProperties.getSource())
                .credentials(minioProperties.getUsername(), minioProperties.getPassword())
                .build();
    }
}
