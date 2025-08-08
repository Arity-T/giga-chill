/// <reference types="cypress" />

import { ShoppingItemData } from "../types";

declare global {
    namespace Cypress {
        interface Chainable {
            // Shopping Lists
            createShoppingList(listName: string, description?: string): Chainable<void>;
            getShoppingList(listName: string): Chainable<JQuery<HTMLElement>>;
            toogleShoppingList(): Chainable<JQuery<HTMLElement>>;

            // Shopping List Consumers
            setShoppingListConsumers(selectAll?: boolean): Chainable<JQuery<HTMLElement>>;
            getShoppingListConsumersCount(): Chainable<string>;

            // Shopping List Budget
            setShoppingListBudget(budget: string): Chainable<JQuery<HTMLElement>>;
            getShoppingListBudget(): Chainable<string>;

            // Shopping Items
            addShoppingItem(itemData: ShoppingItemData): Chainable<JQuery<HTMLElement>>;
            getShoppingItem(itemName: string): Chainable<JQuery<HTMLElement>>;
            markShoppingItemAsPurchased(): Chainable<JQuery<HTMLElement>>;
        }
    }
};
export { } // Необходимо для использования global


Cypress.Commands.add('createShoppingList', (listName, description) => {
    cy.contains('button', 'Добавить список').click();

    cy.contains('.ant-modal-content', 'Создать список покупок')
        .within(() => {
            cy.get('input[placeholder*="название"]')
                .type(listName);

            if (description) {
                cy.get('textarea[placeholder*="описание"]')
                    .type(description);
            }

            cy.contains('button', 'Создать').click();
        });
});


Cypress.Commands.add('getShoppingList', (listName) => {
    return cy.contains('.ant-card', listName);
});


Cypress.Commands.add('toogleShoppingList', { prevSubject: 'element' }, (shoppingListCard) => {
    cy.wrap(shoppingListCard).find('.anticon-caret-right').click();
    return cy.wrap(shoppingListCard);
});


Cypress.Commands.add('setShoppingListConsumers', { prevSubject: 'element' }, (shoppingListCard, selectAll = true) => {
    // Находим список и кликаем на иконку назначения потребителей
    cy.wrap(shoppingListCard).find('.anticon-user-add').last().click();

    cy.contains('.ant-modal-content', 'Выбрать потребителей')
        .within(() => {
            // TODO: реализовать выбор потребителей по спику имён или логинов
            if (selectAll) {
                // Выбираем всех потребителей
                cy.contains('Выбрать всех').click();
            }

            // Сохраняем выбор
            cy.contains('button', 'Сохранить').click();
        });

    return cy.wrap(shoppingListCard);
});


Cypress.Commands.add('getShoppingListConsumersCount', { prevSubject: 'element' }, (shoppingListCard) => {
    return cy.wrap(shoppingListCard).find('.anticon-user-add')
        .parent()
        .find('.ant-typography')
        .should('be.visible')
        .invoke('text');
});


Cypress.Commands.add('setShoppingListBudget', { prevSubject: 'element' }, (shoppingListCard, budget) => {
    cy.wrap(shoppingListCard).within(() => {
        cy.get('input[placeholder="Бюджет"]').clear().type(budget);
        cy.get('.anticon-check').click();

        // Проверяем, что бюджет сохранился
        // Икнока для сохранения должна исчезнуть
        cy.get('.anticon-check').should('not.exist');
    });

    return cy.wrap(shoppingListCard);
});


Cypress.Commands.add('getShoppingListBudget', { prevSubject: 'element' }, (shoppingListCard) => {
    return cy.wrap(shoppingListCard).find('input[placeholder="Бюджет"]').invoke('val');
});


Cypress.Commands.add('addShoppingItem', { prevSubject: 'element' }, (shoppingListCard, itemData) => {
    cy.wrap(shoppingListCard).contains('button', 'Добавить покупку').click();

    cy.contains('.ant-modal-content', 'Добавить покупку').as('addShoppingItemModal');

    cy.get('@addShoppingItemModal').within(() => {
        // Заполняем название товара
        cy.get('input[placeholder="Введите название товара"]')
            .type(itemData.name);

        // Заполняем количество
        cy.get('input[placeholder="1"]').clear().type(itemData.quantity);

        // Открываем выпадающий список с единицами измерения
        cy.get('.ant-select-selector').contains("шт").click();
    });

    // Выбираем нужную единицу измерения
    cy.contains('.ant-select-item', itemData.unit).click();

    cy.get('@addShoppingItemModal').contains('button', 'Добавить').click();

    return cy.wrap(shoppingListCard);
});


Cypress.Commands.add('getShoppingItem', { prevSubject: 'element' }, (shoppingListCard, itemName) => {
    return cy.wrap(shoppingListCard).contains('.ant-card', itemName);
});


Cypress.Commands.add('markShoppingItemAsPurchased', { prevSubject: 'element' }, (shoppingItemCard) => {
    cy.wrap(shoppingItemCard).find('.ant-checkbox')
        .find('input[type="checkbox"]').click().should('be.checked');
});