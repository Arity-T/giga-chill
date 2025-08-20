package ru.gigachill.repository.composite;

import java.util.List;
import java.util.UUID;
import ru.gigachill.data.transfer.object.ParticipantBalanceDTO;
import ru.gigachill.data.transfer.object.ParticipantDTO;
import ru.gigachill.data.transfer.object.ParticipantSummaryBalanceDTO;

/**
 * Data Access Object (DAO) interface for {@link ParticipantDTO} entities.
 *
 * <p>Provides methods to query and manipulate participants associated with events.
 */
public interface ParticipantDAO {

    /**
     * Retrieves all participants for a given event.
     *
     * @param eventId the unique identifier of the event
     * @return a {@link List} of {@link ParticipantDTO} objects for the specified event; an empty
     *     list if there are no participants
     */
    List<ParticipantDTO> getAllParticipantsByEventId(UUID eventId);

    /**
     * Adds a participant to the specified event.
     *
     * @param eventId the unique identifier of the event
     * @param participant the {@link ParticipantDTO} to add to the event
     */
    void addParticipantToEvent(UUID eventId, ParticipantDTO participant);

    /**
     * Removes a participant from the specified event.
     *
     * @param eventId the unique identifier of the event
     * @param participantId the unique identifier of the participant to remove
     */
    void deleteParticipant(UUID eventId, UUID participantId);

    /**
     * Checks whether a user is a participant in the specified event.
     *
     * @param eventId the unique identifier of the event
     * @param userId the unique identifier of the user
     * @return {@code true} if the user is a participant in the event, {@code false} otherwise
     */
    boolean checkUserInEvent(UUID eventId, UUID userId);

    /**
     * Updates the role of a specific participant in an event.
     *
     * @param eventId the unique identifier of the event
     * @param participantId the unique identifier of the participant
     * @param role the new role to assign to the participant
     */
    void updateParticipantRole(UUID eventId, UUID participantId, String role);

    /**
     * Retrieves the role of a participant in a specific event.
     *
     * @param eventId the unique identifier of the event
     * @param participantId the unique identifier of the participant
     * @return the role name of the user in the event, or {@code null} if none
     */
    String getParticipantRoleInEvent(UUID eventId, UUID participantId);

    /**
     * Retrieves participant by id for a given event.
     *
     * @param eventId the unique identifier of the event
     * @return {@link ParticipantDTO} object for the specified event;
     */
    ParticipantDTO getParticipantById(UUID eventId, UUID participantId);

    /**
     * Retrieves the current balance summary for the specified participant.
     *
     * @param eventId the unique identifier of the event
     * @param participantId the unique identifier of the participant
     * @return a {@link ParticipantBalanceDTO} containing the participant’s total debits, credits,
     *     and net balance; never {@code null}
     */
    ParticipantBalanceDTO getParticipantBalance(UUID eventId, UUID participantId);

    /**
     * Computes and retrieves a summary of balance information for each participant in the given
     * event.
     *
     * @param eventId the unique identifier of the event for which to calculate participant balances
     * @return a {@link List} of {@link ParticipantSummaryBalanceDTO} objects
     */
    List<ParticipantSummaryBalanceDTO> getSummaryParticipantBalance(UUID eventId);
}
