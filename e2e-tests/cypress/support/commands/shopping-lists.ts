/// <reference types="cypress" />

import { ShoppingItemData } from "../types";

declare global {
    namespace Cypress {
        interface Chainable {
            // Shopping Lists commands
            createShoppingListUI(listName: string, description?: string): Chainable<void>;
            addShoppingItemUI(listName: string, itemData: ShoppingItemData): Chainable<void>;
            assignShoppingListConsumers(listName: string, selectAll?: boolean): Chainable<void>;
            markShoppingItemAsPurchasedUI(listName: string, itemName: string): Chainable<void>;
            setShoppingListBudgetUI(listName: string, budget: string): Chainable<void>;
        }
    }
};
export { } // Необходимо для использования global


Cypress.Commands.add('createShoppingListUI', (listName, description) => {
    // Переходим на вкладку списков покупок
    cy.url().then((url) => {
        // TODO: переделать на использование конфига
        if (!url.includes("/shopping")) {
            cy.contains('.ant-menu-item a', 'Списки покупок').click();
        }
    });

    // Нажимаем кнопку добавления списка
    cy.contains('button', 'Добавить список').should('be.visible').click();

    // Внутри модального окна
    cy.contains('.ant-modal-content', 'Создать список покупок').should('be.visible')
        .within(() => {
            // Вводим название списка покупок
            cy.get('input[placeholder="Введите название списка покупок"]')
                .type(listName)
                .should('have.value', listName);

            // Заполняем описание, если передано
            if (description) {
                cy.get('textarea[placeholder*="описание"]')
                    .type(description);
            }

            // Создаём список
            cy.contains('button', 'Создать').click();
        });

    // Проверяем, что список создан
    cy.contains('.ant-card', listName).should('be.visible');
});


Cypress.Commands.add('addShoppingItemUI', (listName, itemData) => {
    // Переходим на вкладку списков покупок
    cy.url().then((url) => {
        // TODO: переделать на использование конфига
        if (!url.includes("/shopping")) {
            cy.contains('.ant-menu-item a', 'Списки покупок').click();
        }
    });

    // Ждём загрузки и находим нужный список
    cy.contains('.ant-card', listName).should('be.visible').as('shoppingListCard')

    // Если список закрыт, открываем его
    cy.get('@shoppingListCard')
        .invoke('outerHeight')
        .then((height) => {
            if (height <= 80) {
                cy.get('@shoppingListCard').click();
            }
        });

    // Нажимаем кнопку добавления покупки
    cy.get('@shoppingListCard').contains('button', 'Добавить покупку').should('be.visible').click();

    // В появившемся модальном окне создаём покупку
    cy.contains('.ant-modal-content', 'Добавить покупку').should('be.visible').as('addShoppingItemModal');

    cy.get('@addShoppingItemModal').within(() => {
        // Заполняем название товара
        cy.get('input[placeholder="Введите название товара"]')
            .type(itemData.name)
            .should('have.value', itemData.name);

        // Заполняем количество
        cy.get('input[placeholder="1"]').clear().type(itemData.quantity);

        // Открываем выпадающий список с единицами измерения
        cy.get('.ant-select-selector').contains("шт").click();
    });

    // Выбираем нужную единицу измерения
    cy.contains('.ant-select-item', itemData.unit).should('be.visible').click();

    // Добавляем товар
    cy.get('@addShoppingItemModal').contains('button', 'Добавить').should('be.visible').click();

    // Проверяем, что покупка добавлена
    cy.get('@shoppingListCard').contains(itemData.name).should('be.visible');
});


Cypress.Commands.add('assignShoppingListConsumers', (listName, selectAll = true) => {
    // Находим список и кликаем на иконку назначения потребителей
    cy.contains('.ant-card-body', listName).within(() => {
        cy.get('.anticon-user-add').last().as('assignConsumersIcon');
        cy.get('@assignConsumersIcon').click();
    });

    cy.contains('.ant-modal-content', 'Выбрать потребителей').should('be.visible')
        .within(() => {
            // TODO: реализовать выбор потребителей по спику имён или логинов
            if (selectAll) {
                // Выбираем всех потребителей
                cy.contains('Выбрать всех').click();

                // Сохраняем ожидаемое количество потребителей
                cy.get('ul.ant-list-items > li.ant-list-item')
                    .its('length')
                    .as('expectedConsumerCount');
            }

            // Сохраняем выбор
            cy.contains('button', 'Сохранить').click();
        });

    // Проверяем, что количество потребителей соответствует ожидаемому
    cy.get('@expectedConsumerCount').then((count) => {
        cy.get('@assignConsumersIcon')
            .parent()
            .contains('span', `${count}`)
            .should('be.visible');
    });

});


Cypress.Commands.add('markShoppingItemAsPurchasedUI', (listName, itemName) => {
    // Находим карточку списка и кликаем на неё
    cy.contains('.ant-card', listName).as('shoppingListCard');

    // Если список закрыт, открываем его
    cy.get('@shoppingListCard')
        .invoke('outerHeight')
        .then((height) => {
            if (height <= 80) {
                cy.get('@shoppingListCard').click();
            }
        });

    // Находим товар в списке
    cy.get('@shoppingListCard').contains('.ant-card', itemName).should('be.visible')
        .within(() => {
            // Отмечаем товар как купленный и проверяем, что он действительно отмечен
            cy.get('input[type="checkbox"]').click().should('be.checked');
        });
});


Cypress.Commands.add('setShoppingListBudgetUI', (listName, budget) => {
    // Работаем внутри карточки списка
    cy.contains('.ant-card', listName).should('be.visible').within(() => {
        // Указываем бюджет
        cy.get('input[placeholder="Бюджет"]').clear().type(budget);

        // Сохраняем
        cy.get('.anticon-check').should('be.visible').click();

        // Проверяем, что бюджет сохранился
        // Икнока для сохранения должна исчезнуть
        cy.get('.anticon-check').should('not.exist');

        // Проверяем, что бюджет отображается
        // при этом может добавиться десятичная часть, поэтому проверяем по вхождению
        cy.get('input[placeholder="Бюджет"]')
            .invoke('val')
            .should('contain', budget);
    });
}); 