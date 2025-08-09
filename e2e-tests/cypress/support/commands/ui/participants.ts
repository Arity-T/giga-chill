/// <reference types="cypress" />

import type { ParticipantRole } from "@typings/ui";

declare global {
    namespace Cypress {
        interface Chainable {
            getParticipantRow(login: string): Chainable<JQuery<HTMLElement>>;
            getParticipantRole(): Chainable<string>;
            getParticipantRoleSelect(): Chainable<JQuery<HTMLElement>>;
            setParticipantRole(newRole: ParticipantRole): Chainable<JQuery<HTMLElement>>;
            addParticipantByLogin(username: string): Chainable<void>;
            getDeleteParticipantBtn(): Chainable<JQuery<HTMLElement>>;
            openAddParticipantModal(): Chainable<JQuery<HTMLElement>>;
            switchToInviteByLinkTab(): Chainable<JQuery<HTMLElement>>;
            getInvitationLink(): Chainable<string>;
            regenerateInvitationLink(): Chainable<JQuery<HTMLElement>>;
        }
    }
};
export { } // Необходимо для использования global


Cypress.Commands.add('getParticipantRow', (login) => {
    return cy.contains('tr', login);
});


Cypress.Commands.add('getParticipantRole', { prevSubject: 'element' }, (participantRow) => {
    return cy.wrap(participantRow).find('td:nth-child(2) .ant-tag').invoke('text');
});


Cypress.Commands.add('getParticipantRoleSelect', { prevSubject: 'element' }, (participantRow) => {
    return cy.wrap(participantRow).find('td:nth-child(2) .ant-select');
});


Cypress.Commands.add('setParticipantRole', { prevSubject: 'element' }, (participantRow, newRole) => {
    cy.wrap(participantRow).getParticipantRoleSelect().click();

    cy.get('.ant-select-item').contains(newRole).click();

    return cy.wrap(participantRow);
});


Cypress.Commands.add('openAddParticipantModal', () => {
    // Кликаем по кнопке "Добавить участника"
    cy.contains('button', 'Добавить участника').click();

    // Внутри модального окна "Добавить участника"
    return cy.contains('.ant-modal-content', 'Добавить участника');
});


Cypress.Commands.add('addParticipantByLogin', { prevSubject: 'element' }, (modalContent, username) => {
    // Внутри модального окна "Добавить участника"
    cy.wrap(modalContent)
        .within(() => {
            cy.get('input[placeholder="Введите логин пользователя"]')
                .type(username)

            cy.contains('button', 'Добавить участника').click();
        });
});


Cypress.Commands.add('getDeleteParticipantBtn', { prevSubject: 'element' }, (participantRow) => {
    return cy.wrap(participantRow).find('td:nth-child(3) .ant-btn');
});


Cypress.Commands.add('switchToInviteByLinkTab', { prevSubject: 'element' }, (modalContent) => {
    // Внутри модального окна "Добавить участника"
    cy.wrap(modalContent)
        .within(() => {
            // Переключаемся на вкладку "По ссылке-приглашению"
            cy.contains('.ant-tabs-tab', 'По ссылке-приглашению').click();
        });

    return cy.wrap(modalContent);
});


Cypress.Commands.add('getInvitationLink', { prevSubject: 'element' }, (modalContent) => {
    return cy.wrap(modalContent).find('span.ant-typography code').should('be.visible')
        .invoke('text');
});


Cypress.Commands.add('regenerateInvitationLink', { prevSubject: 'element' }, (modalContent) => {
    // Внутри модального окна "Добавить участника"
    cy.wrap(modalContent)
        .within(() => {
            // Находим кнопку "Создать новую ссылку", сохраняем в алиас и кликаем
            cy.contains('button', 'Создать новую ссылку')
                .click();
        });

    return cy.wrap(modalContent);
});