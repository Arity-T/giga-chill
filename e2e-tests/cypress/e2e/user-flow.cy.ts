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
        cy.contains('.ant-menu-item a', 'Участники').click();
        cy.openAddParticipantModal().addParticipantByLogin('xuxa');
        cy.openAddParticipantModal().addParticipantByLogin('didi');

        // Назначаем роль администратора
        cy.changeParticipantRoleByName('Ксения', 'Администратор');
    });

    it('Создание списка покупок', () => {
        cy.contains('.ant-menu-item a', 'Списки покупок').click();
        cy.createShoppingList('Напитки');

        cy.getShoppingList('Напитки')
            .toogleShoppingList()
            .addShoppingItem({
                name: 'Сок яблочный',
                quantity: '3',
                unit: 'л'
            })
            .setShoppingListConsumers()
            .getShoppingListConsumersCount()
            .should('equal', '3');

        cy.getShoppingList('Напитки')
            .getShoppingItem('Сок яблочный')
            .should('be.visible');
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

        cy.getShoppingList('Напитки')
            .setShoppingListBudget('46')
            .toogleShoppingList()
            .getShoppingItem('Сок яблочный')
            .markShoppingItemAsPurchased();

        cy.getShoppingList('Напитки')
            .getShoppingListBudget()
            .should('contain', '46');

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
        cy.getShoppingList('Напитки')
            .setShoppingListBudget('100');

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