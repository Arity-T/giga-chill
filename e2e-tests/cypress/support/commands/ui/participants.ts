/// <reference types="cypress" />

import type { ParticipantRole } from "@typings/ui";

declare global {
    namespace Cypress {
        interface Chainable {
            changeParticipantRoleByName(participantName: string, newRole: ParticipantRole): Chainable<void>;
            addParticipantByLogin(username: string): Chainable<void>;
            openAddParticipantModal(): Chainable<JQuery<HTMLElement>>;
            switchToInviteByLinkTab(): Chainable<JQuery<HTMLElement>>;
            getInvitationLink(): Chainable<string>;
            regenerateInvitationLink(): Chainable<JQuery<HTMLElement>>;
        }
    }
};
export { } // Необходимо для использования global


Cypress.Commands.add('changeParticipantRoleByName', (participantName, newRole) => {
    // Находим строку и сохраняем её в алиас
    cy.contains('tr', participantName).as('participantRow');

    // Кликаем на текущую роль в этой строке
    cy.get('@participantRow')
        .contains(/^(Участник|Администратор)$/)
        .click();

    // Выбираем новую роль
    cy.get('.ant-select-item').contains(newRole).click();

    // Проверяем, что роль поменялась
    cy.get('@participantRow').contains(newRole).should('be.visible');
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