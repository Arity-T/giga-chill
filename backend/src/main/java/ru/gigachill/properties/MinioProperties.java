package ru.gigachill.properties;

import java.math.BigDecimal;
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
    private BigDecimal maxFileSize;
    private List<String> allowedContentTypes;
    private List<String> allowedFileTypes;
    private BigDecimal maxLinkTtl;
    private String uploadUrl;
}
