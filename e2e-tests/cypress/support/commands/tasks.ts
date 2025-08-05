/// <reference types="cypress" />

import { CreateTaskData } from "../types";

declare global {
    namespace Cypress {
        interface Chainable {
            createTaskUI(taskData: CreateTaskData): Chainable<void>;
            takeTaskInProgressUI(taskName: string): Chainable<void>;
            submitTaskForReviewUI(executorComment: string): Chainable<void>;
            completeTaskUI(reviwerComment: string, isApproved: boolean): Chainable<void>;
        }
    }
};
export { } // Необходимо для использования global


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


Cypress.Commands.add('takeTaskInProgressUI', (taskName) => {
    // Переходим на вкладку задач
    cy.url().then((url) => {
        // TODO: переделать на использование конфига
        if (!url.includes("/tasks")) {
            cy.contains('.ant-menu-item a', 'Задачи').click();
        }
    });

    // Открываем задачу
    cy.contains('.ant-card', taskName).should('be.visible').click();

    // В открывшемся модальном окне
    cy.contains('.ant-modal-content', taskName).should('be.visible').within(() => {
        // Берём в работу
        cy.contains('button', 'Взять в работу').should('be.visible').click();

        // Проверяем, что задача взята в работу
        cy.contains('.ant-tag', 'В работе').should('exist');
        cy.contains('button', 'На проверку').should('exist');
    });
});

// TODO: привести к одному формату с takeTaskInProgressUI, добавить проверку, 
// открыта ли модалка с задачей, открыта ли страница задач, etc
// либо убрать эти проверки из takeTaskInProgressUI тоже
Cypress.Commands.add('submitTaskForReviewUI', (executorComment) => {
    // Внутри модального окна с задачей
    cy.get('.ant-modal-content').should('be.visible').within(() => {
        // Заполняем отчёт о выполнении
        cy.get('textarea[placeholder*="Опишите выполненную работу"]')
            .type(executorComment);

        // Отправляем на проверку
        cy.contains('button', 'На проверку').click();

        // Проверяем, что задача перешла в статус "На проверке"
        cy.contains('.ant-tag', 'На проверке').should('exist');
    });
});


Cypress.Commands.add('completeTaskUI', (reviwerComment, isApproved) => {
    // Внутри модального окна с задачей
    cy.get('.ant-modal-content').should('be.visible').within(() => {
        // Добавляем комментарий к проверке
        cy.get('textarea[placeholder*="Добавьте комментарий к проверке..."]')
            .type(reviwerComment);

        // Подтверждаем выполнение, если указано
        if (isApproved) {
            cy.contains('button', 'Подтвердить выполнение').click();
        } else {
            cy.contains('button', 'Отправить назад в работу').click();
        }
    });

}); 