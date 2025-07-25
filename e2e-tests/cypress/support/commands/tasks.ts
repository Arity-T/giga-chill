/// <reference types="cypress" />
/**
 * Команды для работы с задачами
 */

// Custom command для создания задачи
Cypress.Commands.add('createTaskUI', (taskData) => {
    // Переходим на вкладку задач
    cy.contains('Задачи').click();

    // Нажимаем кнопку создания задачи
    cy.contains('Создать задачу').click();

    // Заполняем название задачи
    cy.get('input[placeholder="Введите название задачи"]')
        .type(taskData.name)
        .should('have.value', taskData.name);

    // Выбираем дату и время выполнения
    cy.get('input[placeholder="Выберите дату и время"]').click();

    cy.get('.ant-picker-time-panel-column')
        .first()
        .contains(taskData.hour)
        .click();

    cy.contains('ОК').click();

    // Выбираем исполнителя, если указан
    if (taskData.assigneeName) {
        cy.contains("Выберите исполнителя (необязательно)").click({ force: true });
        cy.contains(taskData.assigneeName).click();
    }

    // Выбираем списки покупок, если указаны
    if (taskData.shoppingLists && taskData.shoppingLists.length > 0) {
        cy.contains("Выберите списки покупок (необязательно)").click({ force: true });
        taskData.shoppingLists.forEach(listName => {
            cy.contains(listName).click();
        });
        // Закрываем выпадающий список
        cy.contains(taskData.shoppingLists[0]).click();
    }

    // Создаём задачу
    cy.get('button:contains("Создать")').last().click();

    // Проверяем, что задача создалась
    cy.contains(taskData.name).should('exist');
});

// Custom command для взятия задачи в работу
Cypress.Commands.add('takeTaskInProgressUI', (taskName) => {
    // Переходим на вкладку задач
    cy.contains('Задачи').click();

    // Открываем задачу
    cy.contains(taskName).click();

    // Берём в работу
    cy.contains('Взять в работу').click();

    // Проверяем, что задача взята в работу
    cy.contains('На проверку').should('exist');
});

// Custom command для отправки задачи на проверку
Cypress.Commands.add('submitTaskForReviewUI', (reportText) => {
    // Заполняем отчёт о выполнении
    cy.get('textarea[placeholder*="Опишите выполненную работу, результаты и другие важные детали..."]')
        .type(reportText);

    // Отправляем на проверку
    cy.contains('button', 'На проверку').click();
});

// Custom command для подтверждения выполнения задачи
Cypress.Commands.add('confirmTaskCompletionUI', (budget, comment) => {
    // Редактируем бюджет, если указан
    if (budget) {
        cy.get('input[value*=".00"]').click().clear().type(budget);
        cy.get('button').eq(7).click();
    }

    // Добавляем комментарий к проверке
    cy.get('textarea[placeholder*="Добавьте комментарий к проверке..."]')
        .type(comment);

    // Подтверждаем выполнение
    cy.contains('button', 'Подтвердить выполнение').click();
}); 