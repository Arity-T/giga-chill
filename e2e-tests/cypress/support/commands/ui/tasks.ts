/// <reference types="cypress" />

import type { CreateTaskData } from "@typings/ui";

declare global {
    namespace Cypress {
        interface Chainable {
            createTask(taskData: CreateTaskData): Chainable<void>;
            getTaskCard(taskName: string): Chainable<JQuery<HTMLElement>>;
            getTaskModal(taskName: string): Chainable<JQuery<HTMLElement>>;
            takeTaskInProgress(): Chainable<JQuery<HTMLElement>>;
            getTaskStatus(): Chainable<string>;
            submitTaskForReview(executorComment: string): Chainable<JQuery<HTMLElement>>;
            completeTask(reviwerComment: string, isApproved: boolean): Chainable<JQuery<HTMLElement>>;
        }
    }
};
export { } // Необходимо для использования global


Cypress.Commands.add('createTask', (taskData) => {
    cy.contains('button', 'Создать задачу').click();

    cy.contains('.ant-modal-content', 'Создать задачу').as('createTaskModal');

    // Сохраняем заголовок, чтобы потом кликать по нему для закрытия выпадающих списокв
    cy.get('@createTaskModal').contains('Создать задачу').as('createTaskModalTitle');

    cy.get('@createTaskModal').within(() => {
        cy.get('input[placeholder*="название"]')
            .type(taskData.name)

        if (taskData.description) {
            cy.get('textarea[placeholder*="описание"]')
                .type(taskData.description)
        }

        // Выбираем дату и время выполнения
        cy.get('input[placeholder="Выберите дату и время"]')
            .type(taskData.deadline);
    });

    if (taskData.assigneeName) {
        cy.get('@createTaskModal').contains('.ant-select', 'Выберите исполнителя')
            .click();

        cy.contains('.ant-select-item', taskData.assigneeName)
            .click();

        // Закрываем выпадающий список
        cy.get('@createTaskModalTitle').click();
    }

    if (taskData.shoppingLists) {
        cy.get('@createTaskModal').contains('.ant-select', 'Выберите списки покупок')
            .click();

        taskData.shoppingLists.forEach(listName => {
            cy.contains('.ant-select-item', listName)
                .click();
        });

        // Закрываем выпадающий список
        cy.get('@createTaskModalTitle').click();
    }

    cy.get('@createTaskModal').contains('button', 'Создать').click();
});


Cypress.Commands.add('getTaskCard', (taskName) => {
    return cy.contains('.ant-card', taskName);
});


// TODO: Наверное стоит заменить глобальной командой для работы с модалками
Cypress.Commands.add('getTaskModal', (taskName) => {
    return cy.contains('.ant-modal-content', taskName);
});


Cypress.Commands.add('takeTaskInProgress', { prevSubject: 'element' }, (taskModal) => {
    cy.wrap(taskModal).contains('button', 'Взять в работу').click();

    return cy.wrap(taskModal);
});


Cypress.Commands.add('getTaskStatus', { prevSubject: 'element' }, (taskModal) => {
    // TODO: Тут точно стоит использовать data-cy
    return cy.wrap(taskModal).find('.ant-tag').first().invoke('text');
});


Cypress.Commands.add('submitTaskForReview', { prevSubject: 'element' }, (taskModal, executorComment) => {
    cy.wrap(taskModal).within(() => {
        cy.get('textarea[placeholder*="Опишите выполненную работу"]')
            .type(executorComment);

        cy.contains('button', 'На проверку').click();
    });

    return cy.wrap(taskModal);
});


Cypress.Commands.add('completeTask', { prevSubject: 'element' }, (taskModal, reviwerComment, isApproved) => {
    cy.wrap(taskModal).within(() => {
        cy.get('textarea[placeholder*="комментарий"]')
            .type(reviwerComment);

        if (isApproved) {
            cy.contains('button', 'Подтвердить выполнение').click();
        } else {
            cy.contains('button', 'Отправить назад в работу').click();
        }
    });
}); 