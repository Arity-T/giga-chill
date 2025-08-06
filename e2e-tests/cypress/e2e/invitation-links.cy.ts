import { PAGES } from "../support/config/pages.config";

describe('Ссылки-приглашения', () => {
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

    it('Добавление участника по ссылке-приглашению', () => {
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
        cy.get('@inviteLink').then((inviteUrl) => {
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

    it('Регенрация ссылки-приглашения и добавление участника', () => {
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
            login: 'xuxa',
            password: '12345678'
        });

        // Переходим по ссылке-приглашению
        cy.get('@inviteLink').then((inviteUrl) => {
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

    it('Проверка прав администратора мероприятия, может только делиться с ссылкой', () => {
        cy.loginUserAPI({
            login: 'lili',
            password: '12345678'
        })
        //добавляем участника и назначаем его администратором
        cy.get('@eventId').then((eventId) => {
            cy.addParticipantByLoginAPI(`${eventId}`, 'xuxa');
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

        // Открываем страницу мероприятия
        cy.get('@eventId').then((eventId) => {
            cy.visit(PAGES.EVENT_PARTICIPANTS(`${eventId}`));
        });

        // проверяем нашу роль в мероприятии
        cy.contains('.ant-space-item', 'Администратор').should('be.visible');

        // можем добавлять по ссылке
        // вот мы можем открыть модалку для добавления участника 
        // и перейти в раздел, где ссылка-приглашение
        cy.openAddParticipantModal()
            .switchToInviteByLinkTab()
            .getInvitationLink()
            .as('inviteLink');

        cy.get<string>('@inviteLink').then((inviteUrl) => {
            cy.log('Текущая ссылка: ' + `${inviteUrl}`);
        });

        // проверяем, что здесь у нас нет кнопки, чтобы сгенерировать новую ссылку (((
        cy.get('.ant-modal-content')
            .should('be.visible')
            .within(() => {
                cy.contains('button', 'Создать новую ссылку').should('not.exist');
                cy.get('.ant-modal-close').click();
            });
    });


    it('Проверка прав участника мероприятия, у него нет возможности добавить участников', () => {
        cy.loginUserAPI({
            login: 'lili',
            password: '12345678'
        })
        //добавляем участника
        cy.get('@eventId').then((eventId) => {
            cy.addParticipantByLoginAPI(`${eventId}`, 'xuxa');
        });

        // Очищаем состояние браузера
        cy.clearLocalStorage();
        cy.clearCookies();

        // заходим в мероприятие как участник
        cy.loginUserAPI({
            login: 'xuxa',
            password: '12345678'
        })

        // Открываем страницу мероприятия
        cy.get('@eventId').then((eventId) => {
            cy.visit(PAGES.EVENT_PARTICIPANTS(`${eventId}`));
        });

        // проверяем нашу роль в мероприятии
        cy.contains('.ant-space-item', 'Участник').should('be.visible');

        // проверяем наши возможности для добавления участников
        // не можем добавлять как-либо
        cy.contains('button', 'Добавить участника').should('not.exist');
    });
});