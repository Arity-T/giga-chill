package ru.gigachill.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.gigachill.properties.MinioProperties;

@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfig {
    @Bean
    @Primary
    MinioClient minioClient(MinioProperties minioProperties) {
        return MinioClient.builder()
                .endpoint(minioProperties.getSource())
                .credentials(minioProperties.getUsername(), minioProperties.getPassword())
                .build();
    }

    @Bean
    @Qualifier("publicMinioClient")
    MinioClient publicMinioClient(MinioProperties minioProperties) {
        return MinioClient.builder()
                .endpoint(minioProperties.getPublicSource())
                .credentials(minioProperties.getUsername(), minioProperties.getPassword())
                // Регион нужен, чтобы публичный клиент не пытался подключиться к S3
                .region("us-east-1")
                .build();
    }
}
