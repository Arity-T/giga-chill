describe('Полный пользовательский сценарий', () => {
    // beforeEach(() => {
    //     cy.clearAllCookies();
    //     cy.clearAllLocalStorage();
    //     cy.clearAllSessionStorage();
    // });

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

        // Назначение потребителей для покупки (старый код пока оставляем)
        cy.contains('1').last().click();
        cy.contains('Выбрать всех').click();
        cy.get('input[type="checkbox"]:checked').should('have.length', 4);
        cy.get('button:contains("Сохранить")').last().click();

        // Создание задачи (пока оставляем старый код)
        cy.contains('Задачи').click();

        cy.contains('Создать задачу').click();

        cy.get('input[placeholder="Введите название задачи"]')
            .type('Купить напитки')
            .should('have.value', 'Купить напитки');

        cy.get('input[placeholder="Выберите дату и время"]').click();

        cy.get('.ant-picker-time-panel-column')
            .first()
            .contains('03')
            .click();

        cy.contains('ОК').click();

        cy.contains("Выберите исполнителя (необязательно)").click({ force: true });
        cy.contains("Ксения (@xuxa)").click();

        cy.contains("Выберите списки покупок (необязательно)").click({ force: true });
        cy.contains("Напитки").click();
        cy.contains("Напитки").click();

        cy.get('button:contains("Создать")').last().click();
    });

    it('Выполнение задачи исполнителем', () => {
        // Входим под другим пользователем
        cy.loginUserUI("xuxa");

        cy.visit('/events');
        cy.wait(4000);
        cy.contains('Пикник').click();

        cy.contains('Задачи').click();
        cy.contains('Купить напитки').click();
        cy.contains('Взять в работу').click();

        cy.contains('Напитки').click();
        cy.contains('Сок яблочный');
        cy.get('input[type="checkbox"]').click();

        cy.get('input[placeholder="Бюджет"]').type('46');
        cy.get('button').eq(7).click();

        cy.get('textarea[placeholder*="Опишите выполненную работу, результаты и другие важные детали..."]')
            .type('купила');

        cy.contains('button', 'На проверку').click();
    });

    it('Завершение мероприятия и проверка балансов', () => {
        // Возвращаемся под организатором мероприятия
        cy.loginUserUI("lili");

        cy.visit('/events');
        cy.wait(4000);
        cy.contains('Пикник').click();

        cy.contains('Задачи').click();
        cy.contains('Купить напитки').click();

        cy.get('input[value="46.00"]').click().clear().type("100");
        cy.get('button').eq(7).click();

        cy.get('textarea[placeholder*="Добавьте комментарий к проверке..."]')
            .type('молодец');

        cy.contains('button', 'Подтвердить выполнение').click();

        cy.get('button').eq(3).click();

        cy.contains('Общие расчёты').click();
        cy.wait(4000);
        cy.contains('Завершить мероприятие').click();
        cy.contains('Да, завершить мероприятие').click();

        // Проверка балансов
        cy.contains('td', 'Юлия (@lili)')
            .parent()
            .within(() => {
                cy.get('td').eq(2).should('contain', '-33,33');
                cy.get('td').eq(3).should('contain', 'Должник');
            });

        cy.contains('td', 'Ксения (@xuxa)')
            .parent()
            .within(() => {
                cy.get('td').eq(2).should('contain', '66,66');
                cy.get('td').eq(3).should('contain', 'Кредитор');
            });

        cy.contains('td', 'Дарья (@didi)')
            .parent()
            .within(() => {
                cy.get('td').eq(2).should('contain', '-33,33');
                cy.get('td').eq(3).should('contain', 'Должник');
            });
    });
}); 