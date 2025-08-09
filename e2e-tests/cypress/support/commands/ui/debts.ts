/// <reference types="cypress" />

import type { ParticipantStatus } from "@typings/ui";

declare global {
    namespace Cypress {
        interface Chainable {
            finishEventUI(): Chainable<void>;
            checkParticipantBalanceUI(participantLogin: string, expectedBalance: string, expectedStatus: ParticipantStatus): Chainable<void>;
        }
    }
};
export { } // Необходимо для использования global


Cypress.Commands.add('finishEventUI', () => {
    // Переходим к общим расчётам
    cy.url().then((url) => {
        // TODO: переделать на использование конфига
        if (!url.includes("/debts")) {
            cy.contains('.ant-menu-item a', 'Общие расчёты').click();
        }
    });

    // Завершаем мероприятие
    cy.contains('button', 'Завершить мероприятие').click();
    cy.get('.ant-modal-content').contains('button', 'Да, завершить мероприятие')
        .click();

    // Проверяем, что мероприятие завершено (есть таблица с балансами)
    cy.get('table').should('be.visible');
});


Cypress.Commands.add('checkParticipantBalanceUI', (participantLogin, expectedBalance, expectedStatus) => {
    // Находим строку с участником и проверяем его баланс и статус
    cy.contains('tr', participantLogin)
        .within(() => {
            cy.get('td').eq(2).should('contain', expectedBalance);
            cy.get('td').eq(3).should('contain', expectedStatus);
        });
}); 