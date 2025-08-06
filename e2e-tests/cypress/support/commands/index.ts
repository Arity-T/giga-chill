/// <reference types="cypress" />

import type {
    CreateEventData,
    ShoppingItemData,
    ParticipantRole,
    CreateTaskData,
    ParticipantStatus,
    EventCreateAPI,
    LoginRequestAPI,
    RegisterRequestAPI
} from '../types';

/**
 * Главный файл импорта всех custom commands
 * Организованы по доменам API
 */

// Импортируем команды по категориям
import './me';
import './events';
import './participants';
import './shopping-lists';
import './tasks';
import './debts';
import './utils';

// Типы для TypeScript
declare global {
    namespace Cypress {
        interface Chainable {
            // Me commands
            registerUserUI(name: string, username: string, password?: string): Chainable<void>;
            registerUserAPI(registerRequest: RegisterRequestAPI): Chainable<void>;
            loginUserUI(username: string, password?: string): Chainable<void>;
            loginUserAPI(loginRequest: LoginRequestAPI): Chainable<void>;

            // Events commands
            createEventUI(eventData: CreateEventData): Chainable<void>;
            createEventAPI(eventData: EventCreateAPI): Chainable<string>;

            // Participants commands
            addParticipantByLoginUI(username: string): Chainable<void>;
            changeParticipantRoleByNameUI(participantName: string, newRole: ParticipantRole): Chainable<void>;
            openAddParticipantModal(): Chainable<JQuery<HTMLElement>>;
            switchToInviteByLinkTab(): Chainable<JQuery<HTMLElement>>;
            getInvitationLink(): Chainable<string>;
            regenerateInvitationLink(): Chainable<JQuery<HTMLElement>>;
            addParticipantByLoginAPI(eventId: string, userName: string): Chainable<void>;
            changeParticipantRoleAPI(eventId: string, userName: string, role: ParticipantRole): Chainable<void>;

            // Shopping Lists commands
            createShoppingListUI(listName: string, description?: string): Chainable<void>;
            addShoppingItemUI(listName: string, itemData: ShoppingItemData): Chainable<void>;
            assignShoppingListConsumers(listName: string, selectAll?: boolean): Chainable<void>;
            markShoppingItemAsPurchasedUI(listName: string, itemName: string): Chainable<void>;
            setShoppingListBudgetUI(listName: string, budget: string): Chainable<void>;

            // Tasks commands
            createTaskUI(taskData: CreateTaskData): Chainable<void>;
            takeTaskInProgressUI(taskName: string): Chainable<void>;
            submitTaskForReviewUI(executorComment: string): Chainable<void>;
            completeTaskUI(reviwerComment: string, isApproved: boolean): Chainable<void>;

            // Debts commands
            finishEventUI(): Chainable<void>;
            checkParticipantBalanceUI(participantLogin: string, expectedBalance: string, expectedStatus: ParticipantStatus): Chainable<void>;

            // Utils commands
            cleanupDatabase(): Chainable<void>;
            closeModal(): Chainable<void>;
        }
    }
}

export { }; 