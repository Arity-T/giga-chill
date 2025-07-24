/// <reference types="cypress" />

/**
 * Команды для работы с участниками мероприятий
 */

// Custom command для добавления участника по логину
Cypress.Commands.add('addParticipantByLoginUI', (username: string) => {
    // Убедимся, что мы на странице мероприятия и переходим на вкладку участников
    cy.contains('Участники').click({ force: true });

    // Ждём загрузки и нажимаем кнопку добавления участника
    cy.contains('Добавить участника').click();

    // Вводим логин пользователя
    cy.get('input[placeholder="Введите логин пользователя"]')
        .type(username)
        .should('have.value', username);

    // Подтверждаем добавление
    cy.get('button:contains("Добавить участника")').last().click();

    // Ждём завершения операции
    cy.wait(1000);
});

// Custom command для изменения роли участника по имени
Cypress.Commands.add('changeParticipantRoleByNameUI', (participantName: string, newRole: 'Участник' | 'Администратор') => {
    // Убедимся, что мы на вкладке участников
    cy.contains('Участники').click({ force: true });

    // Ждём загрузки списка участников
    cy.wait(1000);

    // Находим строку с участником и кликаем на текущую роль
    cy.contains('tr', participantName)
        .within(() => {
            // Ищем любую текущую роль (может быть как Участник, так и Администратор)
            cy.get('td').contains(/^(Участник|Администратор)$/)
                .click();
        });

    // Выбираем новую роль
    cy.contains('.ant-select-item', newRole).click();

    // Ждём завершения операции
    cy.wait(1000);
}); 