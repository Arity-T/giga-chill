package ru.gigachill.properties;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    private String source;
    private String username;
    private String password;
    private String bucketIncoming;
    private String bucketReceipt;
    private Integer maxFileSize;
    private List<String> allowedContentTypes;
    private List<String> allowedFileTypes;
    private Integer maxLinkTtl;
    private String publicSource;
}
