package ru.gigachill.web.controller;

import io.minio.errors.*;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import ru.gigachill.service.ShoppingListReceiptsService;
import ru.gigachill.service.UserService;
import ru.gigachill.web.api.ShoppingListReceiptsApi;
import ru.gigachill.web.api.model.ReceiptConfirmRequest;
import ru.gigachill.web.api.model.ReceiptUploadPolicy;
import ru.gigachill.web.api.model.ReceiptUploadPolicyCreate;

@RestController
@RequiredArgsConstructor
public class ShoppingListReceiptsController implements ShoppingListReceiptsApi {

    private final UserService userService;
    private final ShoppingListReceiptsService shoppingListReceiptsService;

    @Override
    public ResponseEntity<Void> confirmReceiptUpload(
            UUID eventId, UUID shoppingListId, ReceiptConfirmRequest receiptConfirmRequest) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        shoppingListReceiptsService.confirmUpload(
                user.getId(), eventId, shoppingListId, receiptConfirmRequest);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ReceiptUploadPolicy> createReceiptUploadPolicy(
            UUID eventId,
            UUID shoppingListId,
            ReceiptUploadPolicyCreate receiptUploadPolicyCreate) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());

        return ResponseEntity.ok(
                shoppingListReceiptsService.uploadPolicy(
                        user.getId(), eventId, shoppingListId, receiptUploadPolicyCreate));
    }

    @Override
    public ResponseEntity<Void> deleteReceipt(UUID eventId, UUID shoppingListId, UUID receiptId) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        shoppingListReceiptsService.deleteReceipt(user.getId(), eventId, shoppingListId, receiptId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> getReceiptImage(UUID eventId, UUID shoppingListId, UUID receiptId) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        shoppingListReceiptsService.deleteReceipt(user.getId(), eventId, shoppingListId, receiptId);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(
                        URI.create(
                                shoppingListReceiptsService.getReceipt(
                                        user.getId(), eventId, shoppingListId, receiptId)))
                .build();
    }
}
