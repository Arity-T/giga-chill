/// <reference types="cypress" />
/**
 * Команды для работы с долгами (Debts)
 */

// Custom command для завершения мероприятия
Cypress.Commands.add('finishEventUI', () => {
    // Переходим к общим расчётам
    cy.contains('Общие расчёты').click();
    cy.wait(4000);

    // Завершаем мероприятие
    cy.contains('Завершить мероприятие').click();
    cy.contains('Да, завершить мероприятие').click();

    // Проверяем, что мероприятие завершено (есть таблица с балансами)
    cy.get('table').should('exist');
});

// Custom command для проверки баланса участника
Cypress.Commands.add('checkParticipantBalanceUI', (participantLogin, expectedBalance, expectedStatus) => {
    // Находим строку с участником и проверяем его баланс и статус
    cy.contains('td', participantLogin)
        .parent()
        .within(() => {
            cy.get('td').eq(2).should('contain', expectedBalance);
            cy.get('td').eq(3).should('contain', expectedStatus);
        });
}); 