package ru.gigachill.service;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gigachill.exception.ConflictException;
import ru.gigachill.properties.MinioProperties;
import ru.gigachill.repository.composite.ShoppingListCompositeRepository;
import ru.gigachill.service.validator.*;
import ru.gigachill.web.api.model.ReceiptConfirmRequest;
import ru.gigachill.web.api.model.ReceiptUploadPolicy;
import ru.gigachill.web.api.model.ReceiptUploadPolicyCreate;

@RequiredArgsConstructor
@Service
public class ShoppingListReceiptsService {

    private final ShoppingListReceiptsServiceValidator shoppingListReceiptsServiceValidator;
    private final EventServiceValidator eventServiceValidator;
    private final ParticipantServiceValidator participantServiceValidator;
    private final TaskServiceValidator taskServiceValidator;
    private final TaskService taskService;
    private final ShoppingListService shoppingListService;
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final ShoppingListCompositeRepository shoppingListCompositeRepository;
    private final ShoppingListServiceValidator shoppingListServiceValidator;

    public ReceiptUploadPolicy createUploadPolicy(
            UUID userId,
            UUID eventId,
            UUID shoppingListId,
            ReceiptUploadPolicyCreate receiptUploadPolicyCreate) {

        shoppingListReceiptsServiceValidator.checkContentType(
                receiptUploadPolicyCreate.getContentType().getValue());
        shoppingListReceiptsServiceValidator.checkFileType(
                receiptUploadPolicyCreate.getOriginalFileName());
        shoppingListReceiptsServiceValidator.checkContentLength(
                receiptUploadPolicyCreate.getContentLength());

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        participantServiceValidator.checkUserInEvent(eventId, userId);
        eventServiceValidator.checkIsNotFinalized(eventId);
        shoppingListServiceValidator.checkIsExisted(shoppingListId);
        var taskId = shoppingListService.getTaskIdForShoppingList(shoppingListId);
        if (Objects.isNull(taskId)) {
            throw new ConflictException(
                    "List with id:" + shoppingListId + " is not attached to task");
        }
        taskServiceValidator.checkInProgressStatus(taskId, taskService.getTaskStatus(taskId));
        taskServiceValidator.checkOpportunityToSentTaskToReview(taskId, userId);
        shoppingListReceiptsServiceValidator.canSetReceiptId(shoppingListId);

        ZonedDateTime ttl =
                ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(minioProperties.getMaxLinkTtl());
        PostPolicy policy = new PostPolicy(minioProperties.getBucketIncoming(), ttl);
        policy.addContentLengthRangeCondition(1, minioProperties.getMaxFileSize().longValue());
        policy.addStartsWithCondition("key", "");
        policy.addStartsWithCondition(
                "Content-Type", receiptUploadPolicyCreate.getContentType().getValue());
        policy.addStartsWithCondition("x-amz-meta-md5", receiptUploadPolicyCreate.getMd5Hash());

        Map<String, String> fields;
        try {
            fields = minioClient.getPresignedPostFormData(policy);
        } catch (RuntimeException
                | ErrorResponseException
                | InsufficientDataException
                | InternalException
                | InvalidKeyException
                | InvalidResponseException
                | IOException
                | NoSuchAlgorithmException
                | ServerException
                | XmlParserException e) {
            throw new RuntimeException(e);
        }

        return new ReceiptUploadPolicy(
                UUID.randomUUID(),
                minioProperties.getUploadUrl() + minioProperties.getBucketIncoming(),
                fields);
    }

    public void confirmUpload(
            UUID userId,
            UUID eventId,
            UUID shoppingListId,
            ReceiptConfirmRequest receiptConfirmRequest) {
        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        participantServiceValidator.checkUserInEvent(eventId, userId);
        eventServiceValidator.checkIsNotFinalized(eventId);
        shoppingListServiceValidator.checkIsExisted(shoppingListId);
        var taskId = shoppingListService.getTaskIdForShoppingList(shoppingListId);
        if (Objects.isNull(taskId)) {
            throw new ConflictException(
                    "List with id:" + shoppingListId + " is not attached to task");
        }
        taskServiceValidator.checkInProgressStatus(taskId, taskService.getTaskStatus(taskId));
        taskServiceValidator.checkOpportunityToSentTaskToReview(taskId, userId);
        shoppingListReceiptsServiceValidator.canSetReceiptId(shoppingListId);

        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .source(
                                    CopySource.builder()
                                            .bucket(minioProperties.getBucketIncoming())
                                            .object(receiptConfirmRequest.getReceiptId().toString())
                                            .build())
                            .bucket(minioProperties.getBucketReceipt())
                            .object(receiptConfirmRequest.getReceiptId().toString())
                            .build());
        } catch (RuntimeException
                | ErrorResponseException
                | InsufficientDataException
                | InternalException
                | InvalidKeyException
                | InvalidResponseException
                | IOException
                | NoSuchAlgorithmException
                | ServerException
                | XmlParserException e) {
            throw new RuntimeException(e);
        }

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucketIncoming())
                            .object(receiptConfirmRequest.getReceiptId().toString())
                            .build());
        } catch (RuntimeException
                | ErrorResponseException
                | InsufficientDataException
                | InternalException
                | InvalidKeyException
                | InvalidResponseException
                | IOException
                | NoSuchAlgorithmException
                | ServerException
                | XmlParserException e) {
            throw new RuntimeException(e);
        }

        shoppingListCompositeRepository.setReceiptIdByShoppingListId(
                shoppingListId, receiptConfirmRequest.getReceiptId());
    }

    public void deleteReceipt(UUID userId, UUID eventId, UUID shoppingListId, UUID receiptId) {
        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        participantServiceValidator.checkUserInEvent(eventId, userId);
        eventServiceValidator.checkIsNotFinalized(eventId);
        shoppingListServiceValidator.checkIsExisted(shoppingListId);
        var taskId = shoppingListService.getTaskIdForShoppingList(shoppingListId);
        if (Objects.isNull(taskId)) {
            throw new ConflictException(
                    "List with id:" + shoppingListId + " is not attached to task");
        }
        taskServiceValidator.checkInProgressStatus(taskId, taskService.getTaskStatus(taskId));
        taskServiceValidator.checkOpportunityToSentTaskToReview(taskId, userId);
        shoppingListReceiptsServiceValidator.checkKeyInBucket(
                receiptId, minioProperties.getBucketReceipt());

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucketReceipt())
                            .object(receiptId.toString())
                            .build());
        } catch (RuntimeException
                | ErrorResponseException
                | InsufficientDataException
                | InternalException
                | InvalidKeyException
                | InvalidResponseException
                | IOException
                | NoSuchAlgorithmException
                | ServerException
                | XmlParserException e) {
            throw new RuntimeException(e);
        }

        shoppingListCompositeRepository.deleteReceiptIdByShoppingListId(shoppingListId);
    }

    public String getReceiptUrl(UUID userId, UUID eventId, UUID shoppingListId, UUID receiptId) {
        participantServiceValidator.checkUserInEvent(eventId, userId);
        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        shoppingListServiceValidator.checkIsExisted(shoppingListId);
        var taskId = shoppingListService.getTaskIdForShoppingList(shoppingListId);
        if (Objects.isNull(taskId)) {
            throw new ConflictException(
                    "List with id:" + shoppingListId + " is not attached to task");
        }
        shoppingListReceiptsServiceValidator.checkKeyInBucket(
                receiptId, minioProperties.getBucketReceipt());

        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioProperties.getBucketReceipt())
                            .object(receiptId.toString())
                            .expiry(minioProperties.getMaxLinkTtl(), TimeUnit.SECONDS)
                            .build());
        } catch (RuntimeException
                | ErrorResponseException
                | InsufficientDataException
                | InternalException
                | InvalidKeyException
                | InvalidResponseException
                | IOException
                | NoSuchAlgorithmException
                | ServerException
                | XmlParserException e) {
            throw new RuntimeException(e);
        }
    }
}
