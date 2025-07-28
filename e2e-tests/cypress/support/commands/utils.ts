/// <reference types="cypress" />
/**
 * Команды для работы с задачами
 */

// Custom command для создания задачи
Cypress.Commands.add('cleanupDatabase', () => {
    cy.request('POST', `${Cypress.env('apiUrl')}/test-utils/cleanup`);
});