/// <reference types="cypress" />

import dayjs from "dayjs";
import { UserRole } from "@typings/api";

declare global {
    namespace Cypress {
        interface Chainable {
            resetBrowserState(): Chainable<void>;
            cleanupDatabase(): Chainable<void>;
            testEventSetup(): Chainable<string>;
        }
    }
}
export { } // Необходимо для использования global

Cypress.Commands.add('cleanupDatabase', () => {
    cy.request('POST', `${Cypress.env('apiUrl')}/test-utils/cleanup`);
});

Cypress.Commands.add('resetBrowserState', () => {
    cy.clearAllCookies();
    cy.clearAllLocalStorage();
    cy.clearAllSessionStorage();
    cy.window().then((win) => {
        win.location.href = 'about:blank'
    })
});

Cypress.Commands.add('testEventSetup', () => {
    cy.registerUserAPI('organizer', 'Юля');
    cy.registerUserAPI('admin', 'Даша');
    cy.registerUserAPI('participant', 'Ксюша');

    cy.loginUserAPI('organizer');
    cy.createEventAPI({
        title: 'Пикник',
        location: 'Лес',
        start_datetime: dayjs().add(1, 'day').hour(17).toISOString(),
        end_datetime: dayjs().add(1, 'day').hour(20).toISOString(),
        description: 'всем добра!!!!'
    });

    return cy.getEventsAPI().then((eventsResponse) => {
        const eventId = eventsResponse.body[0].event_id;
        // Добавляем админа
        cy.addParticipantAPI(eventId, 'admin');
        cy.getParticipantsAPI(eventId)
            .then((participantsResponse) => {
                // Надо получить id участника, чтобы сделать его администратором
                cy.wrap(participantsResponse.body).then((participants) => {
                    const adminParticipant = participants
                        .find(participant => participant.login === 'admin');
                    cy.setParticipantRoleAPI(eventId, adminParticipant.id, UserRole.Admin);
                });
            });

        // Добавляем участника
        cy.addParticipantAPI(eventId, 'participant');

        return cy.wrap(eventId);
    });
});