/// <reference types="cypress" />

import { EventCreate, Events } from '../../types/api';

declare global {
    namespace Cypress {
        interface Chainable {
            createEventAPI(eventData: EventCreate): Chainable<void>;
            getEventsAPI(): Chainable<Response<Events>>;
        }
    }
};
export { } // Необходимо для использования global


Cypress.Commands.add('createEventAPI', (eventData) => {
    cy.request({
        method: 'POST',
        url: `${Cypress.env('apiUrl')}/events`,
        body: eventData
    });
});


Cypress.Commands.add('getEventsAPI', () => {
    return cy.request({
        method: 'GET',
        url: `${Cypress.env('apiUrl')}/events`,
    });
});