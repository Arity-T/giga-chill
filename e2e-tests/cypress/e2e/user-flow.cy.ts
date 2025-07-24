describe('Полный пользовательский сценарий', () => {
    // beforeEach(() => {
    //     cy.clearAllCookies();
    //     cy.clearAllLocalStorage();
    //     cy.clearAllSessionStorage();
    // });

    it('Подготовка: регистрация тестовых пользователей', () => {
        // Регистрируем всех тестовых пользователей (пароль по умолчанию из команды)
        cy.visit('/auth/register');
        cy.registerUserUI("Ксения", "xuxa");
        cy.visit('/auth/register');
        cy.registerUserUI("Дарья", "didi");
        cy.visit('/auth/register');
        cy.registerUserUI("Юлия", "lili");
    });

    it('Создание мероприятия и настройка', () => {
        cy.visit('/auth/login');
        cy.loginUserUI("lili");

        cy.visit('/events');

        cy.contains('button', 'Создать')
            .should('be.enabled')
            .click();

        cy.get('input[placeholder="Введите название мероприятия"]')
            .type('Пикник')
            .should('have.value', 'Пикник');

        cy.get('input[placeholder="Введите адрес или место проведения"]')
            .type('Лес')
            .should('have.value', 'Лес');

        cy.get('input[placeholder="Начало"]').click();
        cy.get('.ant-picker-cell').contains('18').click();

        cy.get('.ant-picker-time-panel-column')
            .first()
            .contains('03')
            .click();

        cy.contains('ОК').click();

        cy.get('input[placeholder="Окончание"]').click();
        cy.get('.ant-picker-cell').contains('23').click();

        cy.get('.ant-picker-time-panel-column')
            .first()
            .contains('20')
            .click();

        cy.contains('ОК').click();

        cy.get('textarea[placeholder*="описание"]')
            .type('всем добра');

        cy.get('button:contains("Создать")').last().click();

        cy.wait(2000);
        cy.contains('Пикник').click();

        // Добавление участников
        cy.contains('Участники').click();

        cy.contains('Добавить участника').click();

        cy.get('input[placeholder="Введите логин пользователя"]')
            .type('xuxa')
            .should('have.value', 'xuxa');

        cy.get('button:contains("Добавить участника")').last().click();

        cy.contains('Добавить участника').click();

        cy.get('input[placeholder="Введите логин пользователя"]')
            .type('didi')
            .should('have.value', 'didi');

        cy.get('button:contains("Добавить участника")').last().click();

        // Назначение роли администратора
        cy.contains('tr', 'Ксения')
            .within(() => {
                cy.contains('Участник')
                    .click();
            });

        cy.contains('Администратор').click();

        // Создание списка покупок
        cy.contains('Списки покупок').click();

        cy.contains('Добавить список').click();

        cy.get('input[placeholder="Введите название списка покупок"]')
            .type('Напитки')
            .should('have.value', 'Напитки');

        cy.contains('button', 'Создать')
            .should('be.enabled')
            .click();

        cy.contains('Напитки').click();

        cy.contains('button', 'Добавить покупку')
            .should('be.enabled')
            .click();

        cy.get('input[placeholder="Введите название товара"]')
            .type('Сок яблочный')
            .should('have.value', 'Сок яблочный');

        cy.get('input[placeholder="1"]').click().clear().type('3');

        cy.contains("шт").click();
        cy.contains('.ant-select-item', 'л').click();

        cy.get('button:contains("Добавить")').last().click();

        cy.contains('1').last().click();
        cy.contains('Выбрать всех').click();
        cy.get('input[type="checkbox"]:checked').should('have.length', 4);
        cy.get('button:contains("Сохранить")').last().click();

        // Создание задачи
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
        cy.visit('/auth/login');
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
        cy.visit('/auth/login');
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