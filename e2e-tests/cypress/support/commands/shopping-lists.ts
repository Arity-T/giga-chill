/// <reference types="cypress" />
/**
 * Команды для работы со списками покупок
 */

// Custom command для создания списка покупок
Cypress.Commands.add('createShoppingListUI', (listName) => {
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
Cypress.Commands.add('addShoppingItemUI', (listName, itemData) => {
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

// Custom command для назначения потребителей на список покупок
Cypress.Commands.add('assignShoppingListConsumers', (listName, selectAll = true) => {
    // Находим список и кликаем на иконку назначения потребителей
    cy.contains('.ant-card-body', listName).within(() => {
        cy.get('.anticon-user-add').last().click();
    });

    cy.contains('.ant-modal-content', 'Выбрать потребителей')
        .within(() => {
            if (selectAll) {
                // Выбираем всех потребителей
                cy.contains('Выбрать всех').click();

                // Проверяем, что все чекбоксы выбраны
                cy.get('input[type="checkbox"]:checked').should('have.length', 4);
            }

            // Сохраняем выбор
            cy.contains('button', 'Сохранить').click();
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