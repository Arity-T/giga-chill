/// <reference types="cypress" />

import type { CreateEventData, ShoppingItemData, ParticipantRole } from '../types';

/**
 * Главный файл импорта всех custom commands
 * Организованы по доменам API
 */

// Импортируем команды по категориям
import './auth';
import './events';
import './participants';
import './shopping-lists';

// Типы для TypeScript
declare global {
    namespace Cypress {
        interface Chainable {
            // Auth commands
            registerUserUI(name: string, username: string, password?: string): Chainable<void>;
            loginUserUI(username: string, password?: string): Chainable<void>;

            // Events commands
            createEventUI(eventData: CreateEventData): Chainable<void>;

            // Participants commands
            addParticipantByLoginUI(username: string): Chainable<void>;
            changeParticipantRoleByNameUI(participantName: string, newRole: ParticipantRole): Chainable<void>;

            // Shopping Lists commands
            createShoppingListUI(listName: string): Chainable<void>;
            addShoppingItemUI(listName: string, itemData: ShoppingItemData): Chainable<void>;
        }
    }
}

export { }; 