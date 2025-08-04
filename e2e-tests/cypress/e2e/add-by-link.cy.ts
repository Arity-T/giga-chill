import { PAGES } from "../support/config/pages.config";

describe('Добавление участников по новой сгенерированной ссылке', () => {
    // Подготавливаем состояние приложения для тестов
    before(() => {
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
    });

    beforeEach(() => {
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

    it('Добавление участника по ссылке', () => {
        // Логинимся как организатор
        cy.loginUserAPI({
            login: 'lili',
            password: '12345678'
        });

        // Открываем страницу мероприятия
        cy.get('@eventId').then((eventId) => {
            cy.visit(PAGES.EVENT_DETAILS(`${eventId}`));
        });

        // Открываем модалку и получаем ссылку-приглашение
        cy.openAddParticipantModal()
            .switchToInviteByLinkTab()
            .getInvitationLink()
            .as('inviteLink');

        // Очищаем состояние браузера
        cy.clearLocalStorage();
        cy.clearCookies();

        // Логинимся как участник
        cy.loginUserAPI({
            login: 'xuxa',
            password: '12345678'
        });

        // Переходим по ссылке-приглашению
        cy.get<string>('@inviteLink').then((inviteUrl) => {
            cy.log('Текущая ссылка: ' + `${inviteUrl}`);
            cy.visit(`${inviteUrl}`);
        });

        // Проверяем, что мы на странице мероприятия
        cy.get('@eventId').then((eventId) => {
            cy.url().should('include', PAGES.EVENT_DETAILS(`${eventId}`));
        });

        // Проверяем, что на странице есть название мероприятия
        cy.contains('Пикник').should('exist');
    });



    it('Добавление участника по новой сгенерированной ссылке', () => {
        // Логинимся как организатор
        cy.loginUserAPI({
            login: 'lili',
            password: '12345678'
        });

        // Открываем страницу мероприятия
        cy.get('@eventId').then((eventId) => {
            cy.visit(PAGES.EVENT_DETAILS(`${eventId}`));
        });

        // Открываем модалку, регенерируем ссылку и получаем новую ссылку-приглашение
        cy.openAddParticipantModal()
            .switchToInviteByLinkTab()
            .regenerateInvitationLink()
            .getInvitationLink()
            .as('inviteLink');

        // Очищаем состояние браузера
        cy.clearLocalStorage();
        cy.clearCookies();

        // Логинимся как участник
        cy.loginUserAPI({
            login: 'didi',
            password: '12345678'
        });

        // Переходим по ссылке-приглашению
        cy.get<string>('@inviteLink').then((inviteUrl) => {
            cy.log('Текущая ссылка: ' + `${inviteUrl}`);
            cy.visit(`${inviteUrl}`);
        });

        // Проверяем, что мы на странице мероприятия
        cy.get('@eventId').then((eventId) => {
            cy.url().should('include', PAGES.EVENT_DETAILS(`${eventId}`));
        });

        // Проверяем, что на странице есть название мероприятия
        cy.contains('Пикник').should('exist');
    });
});