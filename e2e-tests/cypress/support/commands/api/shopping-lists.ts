/// <reference types="cypress" />

import { ShoppingListCreate } from "@typings/api";

declare global {
    namespace Cypress {
        interface Chainable {
            createShoppingListAPI(eventId: string, shoppingList: ShoppingListCreate): Chainable<void>;
        }
    }
};
export { } // Необходимо для использования global

Cypress.Commands.add('createShoppingListAPI', (eventId, shoppingList) => {
    cy.request({
        method: 'POST',
        url: `${Cypress.env('apiUrl')}/events/${eventId}/shopping-lists`,
        body: shoppingList,
    });
});