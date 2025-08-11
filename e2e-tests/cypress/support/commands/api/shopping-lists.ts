/// <reference types="cypress" />

import { ShoppingListCreate, ShoppingListsWithItems } from "@typings/api";

declare global {
    namespace Cypress {
        interface Chainable {
            createShoppingListAPI(eventId: string, shoppingList: ShoppingListCreate): Chainable<void>;
            getShoppingListsAPI(eventId: string): Chainable<Response<ShoppingListsWithItems>>;
            setShoppingListConsumersAPI(eventId: string, shoppingListId: string, consumers: string[]): Chainable<void>;
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


Cypress.Commands.add('getShoppingListsAPI', (eventId) => {
    cy.request({
        method: 'GET',
        url: `${Cypress.env('apiUrl')}/events/${eventId}/shopping-lists`,
    });
});


Cypress.Commands.add('setShoppingListConsumersAPI', (eventId, shoppingListId, consumers) => {
    cy.request({
        method: 'PUT',
        url: `${Cypress.env('apiUrl')}/events/${eventId}/shopping-lists/${shoppingListId}/consumers`,
        body: consumers,
    });
});