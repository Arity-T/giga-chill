describe('Полный пользовательский сценарий', { testIsolation: false }, () => {
    before(() => {
        // Очищаем базу данных перед началом сценария
        cy.cleanupDatabase();
    });


    it('Подготовка: регистрация тестовых пользователей', () => {
        // Регистрируем всех тестовых пользователей используя команды
        cy.registerUserUI("Ксения", "xuxa");
        cy.registerUserUI("Дарья", "didi");
        cy.registerUserUI("Юлия", "lili");
    });

    it('Создание мероприятия', () => {
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
    });

    it('Добавление участников', () => {
        // Добавляем участников используя команды
        cy.addParticipantByLoginUI('xuxa');
        cy.addParticipantByLoginUI('didi');

        // Назначаем роль администратора
        cy.changeParticipantRoleByNameUI('Ксения', 'Администратор');

    });

    it('Создание списка покупок', () => {
        // Создаём список покупок используя команды
        cy.createShoppingListUI('Напитки');

        cy.addShoppingItemUI('Напитки', {
            name: 'Сок яблочный',
            quantity: '3',
            unit: 'л'
        });

        // Назначение потребителей для покупки
        cy.assignShoppingListConsumers('Напитки');
    });

    it('Создание задачи', () => {
        // Создание задачи
        cy.createTaskUI({
            name: 'Купить напитки',
            hour: '03',
            assigneeName: 'Ксения',
            shoppingLists: ['Напитки']
        });
    });

    it('Выполнение задачи исполнителем', () => {
        // Входим под другим пользователем
        cy.loginUserUI("xuxa");

        // Переходим на страницу мероприятия
        cy.contains('.ant-card', 'Пикник').should('be.visible').click();

        // Берём задачу в работу
        cy.takeTaskInProgressUI('Купить напитки');

        // Отмечаем покупку как выполненную
        cy.markShoppingItemAsPurchasedUI('Напитки', 'Сок яблочный');

        // Устанавливаем бюджет для списка покупок
        cy.setShoppingListBudgetUI('Напитки', '46');

        // Отправляем задачу на проверку
        cy.submitTaskForReviewUI('купила');
    });

    it('Проверка выполнения задачи ревьюером', () => {
        // Возвращаемся под организатором мероприятия
        cy.loginUserUI("lili");

        // Переходим на страницу мероприятия
        cy.contains('.ant-card', 'Пикник').should('be.visible').click();

        // Переходим на страницу задач
        cy.contains('.ant-menu-item a', 'Задачи').click();

        // Открываем задачу
        cy.contains('.ant-card', 'Купить напитки').should('be.visible').click();

        // Можем изменить бюджет списка покупок
        cy.setShoppingListBudgetUI('Напитки', '100');

        // Подтверждаем выполнение задачи
        cy.completeTaskUI('молодец', true);

        // Закрываем модальное окно с задачей
        cy.closeModal();
    });

    it('Завершение мероприятия и проверка балансов', () => {
        // Завершаем мероприятие
        cy.finishEventUI();

        // Проверка балансов
        cy.checkParticipantBalanceUI('lili', '-33,33', 'Должник');
        cy.checkParticipantBalanceUI('xuxa', '66,66', 'Кредитор');
        cy.checkParticipantBalanceUI('didi', '-33,33', 'Должник');
    });
}); 