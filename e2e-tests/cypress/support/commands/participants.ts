/// <reference types="cypress" />
/**
 * Команды для работы с участниками мероприятий
 */

// Custom command для добавления участника по логину
Cypress.Commands.add('addParticipantByLoginUI', (username) => {
    // Переходим на вкладку участников
    cy.contains('.ant-menu-item a', 'Участники').click({ force: true });

    // Ждём загрузки и нажимаем кнопку добавления участника
    cy.contains('button', 'Добавить участника').should('be.visible').click();

    // В появившемся модальном окне вводим логин пользователя и нажимаем кнопку добавления
    cy.contains('.ant-modal-content', 'Добавить участника')
        .within(() => {
            cy.get('input[placeholder="Введите логин пользователя"]')
                .type(username)
                .should('have.value', username);

            // Подтверждаем добавление
            cy.contains('button', 'Добавить участника').should('be.visible').click();
        });

    // Ждём завершения операции
    cy.contains('tr', username).should('exist');
});

// Custom command для изменения роли участника по имени
Cypress.Commands.add('changeParticipantRoleByNameUI', (participantName, newRole) => {
    // Открываем вкладку «Участники»
    cy.contains('.ant-menu-item a', 'Участники').click({ force: true });

    // Находим строку и сохраняем её в алиас
    cy.contains('tr', participantName).as('participantRow');

    // Кликаем на текущую роль в этой строке
    cy.get('@participantRow')
        .contains(/^(Участник|Администратор)$/)
        .should('be.visible')
        .click();

    // Выбираем новую роль
    cy.get('.ant-select-item').contains(newRole).should('be.visible').click();

    // Проверяем, что роль поменялась
    cy.get('@participantRow').contains(newRole).should('exist');
});
