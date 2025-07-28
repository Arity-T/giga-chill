/// <reference types="cypress" />
/**
 * Команды для работы со списками покупок
 */

// Custom command для создания списка покупок
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

// Custom command для добавления элемента в список покупок
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

// Custom command для назначения потребителей на список покупок
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

// Custom command для отметки товара как купленного
Cypress.Commands.add('markShoppingItemAsPurchasedUI', (listName, itemName) => {
    // Находим карточку списка и кликаем на неё
    cy.get('.ant-card').contains(listName).closest('.ant-card').as('listCard');
    cy.get('@listCard').click();

    // Работаем внутри карточки списка
    cy.get('@listCard').within(() => {
        // Проверяем, что товар есть в списке
        cy.get('.ant-card').contains(itemName).closest('.ant-card').within(() => {
            // Отмечаем товар как купленный
            cy.get('input[type="checkbox"]').click();
        });
    });
});

// Custom command для установки бюджета списка покупок
Cypress.Commands.add('setShoppingListBudgetUI', (listName, budget) => {
    // Работаем внутри карточки списка
    cy.get('.ant-card').contains(listName).closest('.ant-card').within(() => {
        // Указываем бюджет
        cy.get('input[placeholder="Бюджет"]').type(budget);

        // Подтверждаем
        cy.get('.anticon-check').click();
    });
}); 