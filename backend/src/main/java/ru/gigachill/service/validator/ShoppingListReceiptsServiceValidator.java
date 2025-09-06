package ru.gigachill.service.validator;

import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.errors.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.gigachill.exception.BadRequestException;
import ru.gigachill.exception.ConflictException;
import ru.gigachill.exception.NotFoundException;
import ru.gigachill.properties.MinioProperties;
import ru.gigachill.repository.composite.ShoppingListCompositeRepository;

@Component
@RequiredArgsConstructor
public class ShoppingListReceiptsServiceValidator {
    private final MinioProperties minioProperties;
    private final ShoppingListCompositeRepository shoppingListCompositeRepository;
    private final MinioClient minioClient;

    public void checkContentType(String contentType) {
        if (!minioProperties.getAllowedContentTypes().contains(contentType)) {
            throw new BadRequestException("Content type:" + contentType + " is not supported");
        }
    }

    public void checkContentLength(BigDecimal contentLength) {
        if (contentLength.intValue() <= 0) {
            throw new BadRequestException("File size must be greater than zero.");
        }
        if (contentLength.intValue() > minioProperties.getMaxFileSize()) {
            throw new BadRequestException(
                    "The file size exceeds the allowed size in bytes. "
                            + "Acceptable size: "
                            + minioProperties.getMaxFileSize()
                            + ", Received size: "
                            + contentLength);
        }
    }

    public void checkFileType(String fileType) {
        if (minioProperties.getAllowedFileTypes().stream().noneMatch(fileType::endsWith)) {
            throw new BadRequestException("File type:" + fileType + " is not supported");
        }
    }

    public void checkOpportunityToAddReceipt(UUID shoppingListId) {
        if (!shoppingListCompositeRepository.hasReceipt(shoppingListId)) {
            throw new ConflictException(
                    "List with id: " + shoppingListId + " already has a receipt");
        }
    }

    public void checkKeyInBucket(UUID key, String bucketName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder().bucket(bucketName).object(key.toString()).build());
        } catch (ErrorResponseException
                | InsufficientDataException
                | InternalException
                | InvalidKeyException
                | InvalidResponseException
                | IOException
                | NoSuchAlgorithmException
                | ServerException
                | XmlParserException e) {
            throw new NotFoundException("File with id: " + key + " not found in file storage");
        }
    }
}
