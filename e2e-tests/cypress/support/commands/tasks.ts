/// <reference types="cypress" />
/**
 * Команды для работы с задачами
 */

// Custom command для создания задачи
Cypress.Commands.add('createTaskUI', (taskData) => {
    // Переходим на вкладку задач
    cy.url().then((url) => {
        // TODO: переделать на использование конфига
        if (!url.includes("/tasks")) {
            cy.contains('.ant-menu-item a', 'Задачи').click();
        }
    });

    // Нажимаем кнопку создания задачи
    cy.contains('button', 'Создать задачу').should('be.visible').click();

    // Находим модальное окно
    cy.contains('.ant-modal-content', 'Создать задачу').should('be.visible')
        .as('createTaskModal');

    // Сохраняем заголовок, чтобы потом кликать по нему для закрытия выпадающих списокв
    cy.get('@createTaskModal').contains('Создать задачу').as('createTaskModalTitle');

    // Внутри модального окна заполняем форму создания задачи
    cy.get('@createTaskModal').within(() => {
        // Заполняем название задачи
        cy.get('input[placeholder="Введите название задачи"]')
            .type(taskData.name)
            .should('have.value', taskData.name);

        // Заполняем описание задачи, если передано
        if (taskData.description) {
            cy.get('textarea[placeholder*="описание"]')
                .type(taskData.description);
        }
    });

    // Выбираем дату и время выполнения
    cy.get('@createTaskModal').find('input[placeholder="Выберите дату и время"]')
        .click();

    // TODO: добавить выбор даты и вынести работу с календарём в отдельную команду
    cy.get('.ant-picker-datetime-panel-container').should('be.visible').within(() => {
        cy.get('.ant-picker-time-panel-column')
            .first()
            .contains(taskData.hour)
            .click();
        cy.contains('ОК').click();
    });

    // Выбираем исполнителя, если указан
    if (taskData.assigneeName) {
        cy.get('@createTaskModal').contains("Выберите исполнителя")
            .parent()
            .should('be.visible')
            .click();

        cy.contains('.ant-select-item', taskData.assigneeName).should('be.visible')
            .click();

        // Закрываем выпадающий список
        cy.get('@createTaskModalTitle').click();
    }

    // Выбираем списки покупок, если указаны
    if (taskData.shoppingLists && taskData.shoppingLists.length > 0) {
        cy.get('@createTaskModal').contains("Выберите списки покупок")
            .parent()
            .should('be.visible')
            .click();

        taskData.shoppingLists.forEach(listName => {
            cy.contains('.ant-select-item', listName).should('be.visible')
                .click();
        });

        // Закрываем выпадающий список
        cy.get('@createTaskModalTitle').click();
    }

    // Создаём задачу
    cy.get('@createTaskModal').contains('button', 'Создать').should('be.visible').click();

    // Проверяем, что задача создалась
    cy.contains('.ant-card', taskData.name).should('exist');
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