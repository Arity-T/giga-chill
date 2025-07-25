/// <reference types="cypress" />

import type { ShoppingItemData } from '../types';

/**
 * Команды для работы со списками покупок
 */

// Custom command для создания списка покупок
Cypress.Commands.add('createShoppingListUI', (listName: string) => {
    // Убедимся, что мы на странице мероприятия и переходим на вкладку списков покупок
    cy.contains('Списки покупок').click({ force: true });

    // Ждём загрузки и нажимаем кнопку добавления списка
    cy.contains('Добавить список').click();

    // Вводим название списка покупок
    cy.get('input[placeholder="Введите название списка покупок"]')
        .type(listName)
        .should('have.value', listName);

    // Создаём список
    cy.contains('button', 'Создать')
        .should('be.enabled')
        .click();

    // Ждём завершения операции
    cy.wait(1000);
});

// Custom command для добавления элемента в список покупок
Cypress.Commands.add('addShoppingItemUI', (listName: string, itemData: ShoppingItemData) => {
    // Убедимся, что мы на вкладке списков покупок
    cy.contains('Списки покупок').click({ force: true });

    // Ждём загрузки и открываем нужный список
    cy.wait(1000);
    cy.contains(listName).click();

    // Ждём загрузки страницы списка и нажимаем кнопку добавления покупки
    cy.get('button').contains('Добавить покупку').click();

    // Заполняем название товара
    cy.get('input[placeholder="Введите название товара"]')
        .type(itemData.name)
        .should('have.value', itemData.name);

    // Заполняем количество
    cy.get('input[placeholder="1"]').click().clear().type(itemData.quantity);

    // Выбираем единицу измерения
    cy.get('.ant-select-selector').contains("шт").click();
    cy.contains('.ant-select-item', itemData.unit).click();

    // Добавляем товар
    cy.get('button:contains("Добавить")').last().click();

    // Ждём завершения операции
    cy.wait(1000);
}); 