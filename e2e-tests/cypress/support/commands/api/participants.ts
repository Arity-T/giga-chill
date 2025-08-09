/// <reference types="cypress" />

import { UserRole, Participants } from "@typings/api";

declare global {
    namespace Cypress {
        interface Chainable {
            getParticipantsAPI(eventId: string): Chainable<Response<Participants>>;
            addParticipantAPI(eventId: string, login: string): Chainable<void>;
            setParticipantRoleAPI(eventId: string, login: string, role: UserRole): Chainable<void>;
        }
    }
};
export { } // Необходимо для использования global


Cypress.Commands.add('getParticipantsAPI', (eventId) => {
    cy.request({
        method: 'GET',
        url: `${Cypress.env('apiUrl')}/events/${eventId}/participants`,
    });
});


Cypress.Commands.add('addParticipantAPI', (eventId, login) => {
    cy.request({
        method: 'POST',
        url: `${Cypress.env('apiUrl')}/events/${eventId}/participants`,
        body: { login }
    });
});


Cypress.Commands.add('setParticipantRoleAPI', (eventId, login, role) => {
    cy.request({
        method: 'PATCH',
        url: `${Cypress.env('apiUrl')}/events/${eventId}/participants/${login}/role`,
        body: { role }
    });
});