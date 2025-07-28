describe('Полный пользовательский сценарий', () => {
    before(() => {
        // Очищаем базу данных перед началом сценария
        cy.request('POST', 'http://localhost:3000/test-utils/cleanup');
    });


    it('Подготовка: регистрация тестовых пользователей', () => {
        // Регистрируем всех тестовых пользователей используя команды
        cy.registerUserUI("Ксения", "xuxa");
        cy.registerUserUI("Дарья", "didi");
        cy.registerUserUI("Юлия", "lili");
    });

    it('Создание мероприятия и настройка', () => {
        // Входим в систему
        cy.loginUserUI("lili");

        // Создаём мероприятие используя команду
        cy.createEventUI({
            title: 'Пикник',
            location: 'Лес',
            startDay: '18',
            startHour: '03',
            endDay: '23',
            endHour: '20',
            description: 'всем добра'
        });

        // Добавляем участников используя команды
        cy.addParticipantByLoginUI('xuxa');
        cy.addParticipantByLoginUI('didi');

        // Назначаем роль администратора
        cy.changeParticipantRoleByNameUI('Ксения', 'Администратор');

        // Создаём список покупок используя команды
        cy.createShoppingListUI('Напитки');

        cy.addShoppingItemUI('Напитки', {
            name: 'Сок яблочный',
            quantity: '3',
            unit: 'л'
        });

        // Назначение потребителей для покупки
        cy.assignShoppingListConsumers('Напитки');

        // Создание задачи
        cy.createTaskUI({
            name: 'Купить напитки',
            hour: '03',
            assigneeName: 'Ксения (@xuxa)',
            shoppingLists: ['Напитки']
        });
    });

    it('Выполнение задачи исполнителем', () => {
        // Входим под другим пользователем
        cy.loginUserUI("xuxa");

        cy.visit('/events');
        cy.wait(4000);
        cy.contains('Пикник').click();

        // Берём задачу в работу
        cy.takeTaskInProgressUI('Купить напитки');

        // Отмечаем покупку как выполненную
        cy.markShoppingItemAsPurchasedUI('Напитки', 'Сок яблочный');

        // Устанавливаем бюджет для списка покупок
        cy.setShoppingListBudgetUI('Напитки', '46');

        // Отправляем задачу на проверку
        cy.submitTaskForReviewUI('купила');
    });

    it('Завершение мероприятия и проверка балансов', () => {
        // Возвращаемся под организатором мероприятия
        cy.loginUserUI("lili");

        cy.visit('/events');
        cy.wait(4000);
        cy.contains('Пикник').click();

        cy.contains('Задачи').click();
        cy.contains('Купить напитки').click();

        // Подтверждаем выполнение задачи
        cy.confirmTaskCompletionUI('100', 'молодец');

        // Завершаем мероприятие
        cy.finishEventUI();

        // Проверка балансов
        cy.checkParticipantBalanceUI('Юлия (@lili)', '-33,33', 'Должник');
        cy.checkParticipantBalanceUI('Ксения (@xuxa)', '66,66', 'Кредитор');
        cy.checkParticipantBalanceUI('Дарья (@didi)', '-33,33', 'Должник');
    });
}); 