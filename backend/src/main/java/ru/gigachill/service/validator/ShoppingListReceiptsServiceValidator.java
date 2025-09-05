package ru.gigachill.service.validator;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.gigachill.exception.BadRequestException;
import ru.gigachill.properties.MinioProperties;

@Component
@RequiredArgsConstructor
public class ShoppingListReceiptsServiceValidator {
    private final MinioProperties minioProperties;

    public void checkContentType(String contentType) {
        if (!minioProperties.getAllowedContentTypes().contains(contentType)) {
            throw new BadRequestException("Content type:" + contentType + " is not supported");
        }
    }

    public void checkContentLength(BigDecimal contentLength) {
        if (contentLength.compareTo(minioProperties.getMaxFileSize()) > 0) {
            throw new BadRequestException(
                    "The file size exceeds the allowed size in bytes. "
                            + "Acceptable size: minioProperties.getMaxFileSize(), Received size: contentLength");
        }
    }

    public void checkFileType(String fileType) {
        if (minioProperties.getAllowedFileTypes().stream().noneMatch(fileType::endsWith)) {
            throw new BadRequestException("File type:" + fileType + " is not supported");
        }
    }
}
