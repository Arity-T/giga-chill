/// <reference types="cypress" />
/**
 * Команды для работы с долгами (Debts)
 */

// Custom command для завершения мероприятия
Cypress.Commands.add('finishEventUI', () => {
    // Переходим к общим расчётам
    cy.url().then((url) => {
        // TODO: переделать на использование конфига
        if (!url.includes("/debts")) {
            cy.contains('.ant-menu-item a', 'Общие расчёты').should('be.visible').click();
        }
    });

    // Завершаем мероприятие
    cy.contains('button', 'Завершить мероприятие').should('be.visible').click();
    cy.get('.ant-modal-content').contains('button', 'Да, завершить мероприятие')
        .should('be.visible').click();

    // Проверяем, что мероприятие завершено (есть таблица с балансами)
    cy.get('table').should('exist');
});

// Custom command для проверки баланса участника
Cypress.Commands.add('checkParticipantBalanceUI', (participantLogin, expectedBalance, expectedStatus) => {
    // Находим строку с участником и проверяем его баланс и статус
    cy.contains('tr', participantLogin)
        .within(() => {
            cy.get('td').eq(2).should('contain', expectedBalance);
            cy.get('td').eq(3).should('contain', expectedStatus);
        });
}); 