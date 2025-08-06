import { PAGES } from "../support/config/pages.config";

describe('Добавление участников по логину и назначение ролей', () => {
    // Подготавливаем состояние приложения для тестов
    beforeEach(() => {
        // Очищаем базу данных
        cy.cleanupDatabase();

        // Регистрируем пользователей
        cy.registerUserAPI({
            login: 'lili',
            password: '12345678',
            name: 'Юля'
        })
        cy.registerUserAPI({
            login: 'xuxa',
            password: '12345678',
            name: 'Ксюша'
        })
        cy.registerUserAPI({
            login: 'didi',
            password: '12345678',
            name: 'Даша'
        })

        // Создаём мероприятие из под пользователя lili
        cy.loginUserAPI({
            login: 'lili',
            password: '12345678'
        })
        cy.createEventAPI({
            title: 'Пикник',
            location: 'Лес',
            start_datetime: '2025-08-20T06:00:00Z',
            end_datetime: '2025-08-30T20:00:00Z',
            description: 'всем добра!!!!'
        }).then((eventId) => {
            cy.wrap(eventId).as('eventId');
        });

        // Очищаем состояние браузера
        cy.clearLocalStorage();
        cy.clearCookies();
    });


    it('Создатель может добавить участника и поменять роль', () => {
        // заходим в мероприятие как создатель
        cy.loginUserAPI({
            login: 'lili',
            password: '12345678'
        })

        // открываем мероприятие
        cy.get('@eventId').then((eventId) => {
            cy.visit(PAGES.EVENT_PARTICIPANTS(`${eventId}`));
        });

        // проверяем нашу роль в мероприятии
        cy.contains('.ant-space-item', 'Организатор').should('be.visible');

        // проверяем наши возможности для добавления участников
        cy.contains('button', 'Добавить участника').should('be.visible');

        // можем добавлять по логину
        cy.addParticipantByLoginUI('xuxa');

        // можем менять роли участников
        cy.changeParticipantRoleByNameUI('xuxa', 'Администратор');
    });



    it('Администратор может только добавить участника', () => {
        cy.loginUserAPI({
            login: 'lili',
            password: '12345678'
        })

        // добавляем администратора
        cy.get('@eventId').then((eventId) => {
            cy.addParticipantByLoginAPI(`${eventId}`, 'xuxa')
            cy.changeParticipantRoleAPI(`${eventId}`, 'xuxa', 'Администратор')
        });

        // Очищаем состояние браузера
        cy.clearLocalStorage();
        cy.clearCookies();


        // заходим в мероприятие как администратор
        cy.loginUserAPI({
            login: 'xuxa',
            password: '12345678'
        })

        // открываем мероприятие
        cy.get('@eventId').then((eventId) => {
            cy.visit(PAGES.EVENT_PARTICIPANTS(`${eventId}`));
        });

        // проверяем нашу роль в мероприятии
        cy.contains('.ant-space-item', 'Администратор').should('be.visible');

        // проверяем наши возможности для добавления участников
        cy.contains('button', 'Добавить участника').should('be.visible');

        // можем добавлять по логину
        cy.addParticipantByLoginUI('didi');

        // не может менять роли
        cy.get('tr').each($row => {
            cy.wrap($row).within(() => {
                cy.get('.ant-select-arrow').should('not.exist');
            });
        });
    });


    it('Обычный участник ничего не может', () => {
        cy.loginUserAPI({
            login: 'lili',
            password: '12345678'
        })

        // добавляем участника
        cy.get('@eventId').then((eventId) => {
            cy.addParticipantByLoginAPI(`${eventId}`, 'didi')
        });

        // Очищаем состояние браузера
        cy.clearLocalStorage();
        cy.clearCookies();


        // заходим в мероприятие как участник
        cy.loginUserAPI({
            login: 'didi',
            password: '12345678'
        })

        // открываем мероприятие
        cy.get('@eventId').then((eventId) => {
            cy.visit(PAGES.EVENT_PARTICIPANTS(`${eventId}`));
        });

        // проверяем нашу роль в мероприятии
        cy.contains('.ant-space-item', 'Участник').should('be.visible');

        // проверяем наши возможности для добавления участников
        // не можем добавлять как-либо
        cy.contains('button', 'Добавить участника').should('not.exist');

        // не может менять роли
        cy.get('tr').each($row => {
            cy.wrap($row).within(() => {
                cy.get('.ant-select-arrow').should('not.exist');
            });
        });
    });
});