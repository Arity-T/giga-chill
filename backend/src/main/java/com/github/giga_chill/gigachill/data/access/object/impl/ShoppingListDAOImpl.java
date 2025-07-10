package com.github.giga_chill.gigachill.data.access.object.impl;

import com.github.giga_chill.gigachill.data.access.object.ShoppingListDAO;
import com.github.giga_chill.gigachill.data.transfer.object.ParticipantDTO;
import com.github.giga_chill.gigachill.data.transfer.object.ShoppingItemDTO;
import com.github.giga_chill.gigachill.data.transfer.object.ShoppingListDTO;
import com.github.giga_chill.gigachill.repository.*;
import com.github.giga_chill.jooq.generated.tables.records.ShoppingItemsRecord;
import com.github.giga_chill.jooq.generated.tables.records.ShoppingListsRecord;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingListDAOImpl implements ShoppingListDAO {
    private final ShoppingListRepository shoppingListRepository;
    private final ConsumerInListRepository consumerInListRepository;
    private final ShoppingItemRepository shoppingItemRepository;
    private final UserRepository userRepository;
    private final UserInEventRepository userInEventRepository;

    private List<ShoppingItemDTO> toShoppingItemDTO(UUID shoppingListId) {
        return shoppingItemRepository.findByShoppingListId(shoppingListId).stream()
                .map(item -> new ShoppingItemDTO(
                        item.getShoppingItemId(),
                        item.getTitle(),
                        item.getQuantity(),
                        item.getUnit(),
                        item.getIsPurchased()
                ))
                .toList();
    }

    private List<ParticipantDTO> toConsumerDTO(UUID shoppingListId) {
        Optional<ShoppingListsRecord> listOpt = shoppingListRepository.findById(shoppingListId);
        if (listOpt.isEmpty()) return List.of();

        UUID eventId = listOpt.get().getEventId();

        return consumerInListRepository.findAllConsumers(shoppingListId).stream()
                .map(userId -> {
                    var userOpt = userRepository.findById(userId);
                    var userInEventOpt = userInEventRepository.findById(eventId, userId);

                    if (userOpt.isEmpty()) {
                        return new ParticipantDTO(userId,
                                null,
                                null,
                                null,
                                null);
                    }

                    var user = userOpt.get();
                    var userInEvent = userInEventOpt.orElse(null);

                    return new ParticipantDTO(
                            user.getUserId(),
                            user.getLogin(),
                            user.getName(),
                            userInEvent != null ? userInEvent.getRole().name() : null,
                            userInEvent != null ? userInEvent.getBalance() : null
                    );
                })
                .toList();
    }

    /**
     * Retrieves all shopping lists associated with the specified event.
     *
     * @param eventId the unique identifier of the event
     * @return a list of {@link ShoppingListDTO} objects for the event; empty list if none found
     */
    @Override
    public List<ShoppingListDTO> getAllShoppingListsFromEvent(UUID eventId) {
        return shoppingListRepository.findByEventId(eventId).stream()
                .map(list -> new ShoppingListDTO(
                        list.getShoppingListId(),
                        list.getTaskId(),
                        list.getTitle(),
                        list.getDescription(),
                        getShoppingListStatus(list.getShoppingListId()),
                        toShoppingItemDTO(list.getShoppingListId()),
                        toConsumerDTO(list.getShoppingListId())
                ))
                .toList();
    }

    /**
     * Retrieves a specific shopping list by its identifier.
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @return the {@link ShoppingListDTO} matching the given ID
     */
    @Override
    public ShoppingListDTO getShoppingListById(UUID shoppingListId) {
        Optional<ShoppingListsRecord> recordOpt = shoppingListRepository.findById(shoppingListId);

        if (recordOpt.isEmpty()) {
            return null;
        }

        ShoppingListsRecord record = recordOpt.get();

        List<ShoppingItemDTO> shoppingItems = toShoppingItemDTO(record.getShoppingListId());
        List<ParticipantDTO> consumers = toConsumerDTO(shoppingListId);

        return new ShoppingListDTO(
                record.getShoppingListId(),
                record.getTaskId(),
                record.getTitle(),
                record.getDescription(),
                getShoppingListStatus(shoppingListId),
                shoppingItems,
                consumers
        );
    }

    /**
     * Creates a new shopping list with the given title and description.
     *
     * @param title       the title of the new shopping list
     * @param description the description of the new shopping list
     */
    @Override
    public void createShoppingList(UUID eventId, UUID shoppingListId, UUID userId, String title, String description) {
        shoppingListRepository.save(new ShoppingListsRecord(
                shoppingListId,
                null,
                eventId,
                title,
                description)
        );
    }

    /**
     * Updates the title and/or description of an existing shopping list.
     * Only non-null parameters will be applied.
     *
     * @param shoppingListId the unique identifier of the shopping list to update
     * @param title          the new title, or {@code null} to leave unchanged
     * @param description    the new description, or {@code null} to leave unchanged
     */
    @Override
    public void updateShoppingList(UUID shoppingListId, @Nullable String title, @Nullable String description) {
        shoppingListRepository.updateTitleAndDescription(shoppingListId, title, description);
    }

    /**
     * Deletes the specified shopping list.
     *
     * @param shoppingListId the unique identifier of the shopping list to delete
     */
    @Override
    public void deleteShoppingList(UUID shoppingListId) {
        shoppingListRepository.deleteById(shoppingListId);
    }

    /**
     * Adds a new shopping item to the specified shopping list.
     *
     * @param shoppingListId  the unique identifier of the shopping list
     * @param shoppingItemDTO the {@link ShoppingItemDTO} representing the new item
     */
    @Override
    public void addShoppingItem(UUID shoppingListId, ShoppingItemDTO shoppingItemDTO) {
        shoppingItemRepository.save(new ShoppingItemsRecord(
                shoppingItemDTO.shoppingItemId(),
                shoppingListId,
                shoppingItemDTO.title(),
                shoppingItemDTO.quantity(),
                shoppingItemDTO.unit(),
                shoppingItemDTO.isPurchased()
        ));
    }

    /**
     * Removes an item from a shopping list.
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @param shoppingItemId the unique identifier of the item to remove
     */
    @Override
    public void deleteShoppingItemFromShoppingList(UUID shoppingListId, UUID shoppingItemId) {
        shoppingItemRepository.deleteById(shoppingItemId);
    }

    /**
     * Updates the purchase status of a shopping item.
     *
     * @param shoppingItemId the unique identifier of the shopping item
     * @param status         {@code true} if the item is purchased; {@code false} otherwise
     */
    @Override
    public void updateShoppingItemStatus(UUID shoppingItemId, boolean status) {
        shoppingItemRepository.updateStatus(shoppingItemId, status);
    }

    /**
     * Retrieves a shopping item by its identifier.
     *
     * @param shoppingItemId the unique identifier of the shopping item
     * @return the {@link ShoppingItemDTO} matching the given ID
     */
    @Override
    public ShoppingItemDTO getShoppingItemById(UUID shoppingItemId) {
        Optional<ShoppingItemsRecord> recordOpt = shoppingItemRepository.findById(shoppingItemId);

        if (recordOpt.isEmpty()) {
            return null;
        }

        ShoppingItemsRecord record = recordOpt.get();

        return new ShoppingItemDTO(
                record.getShoppingItemId(),
                record.getTitle(),
                record.getQuantity(),
                record.getUnit(),
                record.getIsPurchased()
        );
    }

    /**
     * Updates the list of consumer user IDs for a shopping list.
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @param allUserIds     the list of user IDs who are allowed to consume this list
     */
    @Override
    public void updateShoppingListConsumers(UUID shoppingListId, List<UUID> allUserIds) {
        List<UUID> currentUserIds = consumerInListRepository.findAllConsumers(shoppingListId);

        // Пользователи, которых нужно удалить (есть сейчас, но нет в новых)
        List<UUID> toRemove = currentUserIds.stream()
                .filter(id -> !allUserIds.contains(id))
                .toList();

        // Пользователи, которых нужно добавить (есть в новых, но нет сейчас)
        List<UUID> toAdd = allUserIds.stream()
                .filter(id -> !currentUserIds.contains(id))
                .toList();

        consumerInListRepository.deleteConsumers(shoppingListId, toRemove);
        consumerInListRepository.addConsumers(shoppingListId, toAdd);
    }

    /**
     * Retrieves the current status of a shopping list (e.g., "open", "closed").
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @return the status string of the shopping list
     */
    @Override
    public String getShoppingListStatus(UUID shoppingListId) {
        return "unassigned";
    }

    /**
     * Checks whether a shopping list exists by its identifier.
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @return {@code true} if the shopping list exists; {@code false} otherwise
     */
    @Override
    public boolean isExisted(UUID shoppingListId) {
        return shoppingListRepository.exists(shoppingListId);
    }

    /**
     * Checks whether a given user is a consumer of the specified shopping list.
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @param consumerId     the unique identifier of the user
     * @return {@code true} if the user is a consumer; {@code false} otherwise
     */
    @Override
    public boolean isConsumer(UUID shoppingListId, UUID consumerId) {
        return consumerInListRepository.isConsumer(shoppingListId, consumerId);
    }

    /**
     * Checks whether a shopping item exists by its identifier.
     *
     * @param shoppingItemId the unique identifier of the shopping item
     * @return {@code true} if the shopping item exists; {@code false} otherwise
     */
    @Override
    public boolean isShoppingItemExisted(UUID shoppingItemId) {
        return shoppingItemRepository.exists(shoppingItemId);
    }

    /**
     * Updates the details of an existing shopping item.
     *
     * @param shoppingItemDTO the {@link ShoppingItemDTO} containing the new field values for the item
     */
    @Override
    public void updateShoppingItem(ShoppingItemDTO shoppingItemDTO) {
        shoppingItemRepository.update(
                shoppingItemDTO.shoppingItemId(),
                shoppingItemDTO.title(),
                shoppingItemDTO.quantity(),
                shoppingItemDTO.unit(),
                shoppingItemDTO.isPurchased()
        );
    }
}
