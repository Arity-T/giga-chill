import { PAGES } from "../support/config/pages.config";

describe('Удаление участников мероприятия', () => {
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

            cy.addParticipantByLoginAPI(`${eventId}`, 'xuxa')

            cy.addParticipantByLoginAPI(`${eventId}`, 'didi')

            cy.changeParticipantRoleAPI(`${eventId}`, 'xuxa', 'Администратор')
        });

        // Очищаем состояние браузера
        cy.clearLocalStorage();
        cy.clearCookies();
    });


    it('Создатель мероприятия может удалить всех кроме себя', () => {
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

        // проверяем, что не можем удалить создателя(то есть себя),
        // но при этом можем удалить администратора
        cy.contains('tr', 'Организатор').find('.anticon.anticon-delete')
            .should('not.exist');

        cy.get('tr').filter(':contains("Администратор")').each($row => {
            cy.wrap($row).find('.anticon.anticon-delete').should('be.visible');
        });

        cy.get('tr').filter(':contains("Участник")').each($row => {
            cy.wrap($row).find('.anticon.anticon-delete').should('be.visible');
        });

        // удалим для наглядности
        cy.contains('tr', '@xuxa') // Находим строку с нужным участником
            .find('.anticon.anticon-delete') // Ищем кнопку внутри строки
            .click();

        cy.get('.ant-modal-content')
            .should('be.visible')
            .within(() => {
                cy.contains('button', 'Да, удалить').click();
            });

        cy.get('.ant-table-tbody').within(() => {
            cy.contains('@xuxa').should('not.exist');
        });
    });


    it('Администратор может удалять только обычных участников', () => {
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

        // можем удалить любого участника кроме себя,
        //  организатора и вообще любого администратора
        cy.contains('tr', 'Организатор').find('.anticon.anticon-delete')
            .should('not.exist');

        cy.get('tr').filter(':contains("Администратор")').each($row => {
            cy.wrap($row).find('.anticon.anticon-delete').should('not.exist');
        });

        cy.get('tr').filter(':contains("Участник")').each($row => {
            cy.wrap($row).find('.anticon.anticon-delete').should('be.visible');
        });

        // удалим для наглядности
        cy.contains('tr', '@didi') // Находим строку с нужным участником
            .find('.anticon.anticon-delete') // Ищем кнопку внутри строки
            .click();

        cy.get('.ant-modal-content')
            .should('be.visible')
            .within(() => {
                cy.contains('button', 'Да, удалить').click();
            });

        cy.get('.ant-table-tbody').within(() => {
            cy.contains('@didi').should('not.exist');
        });
    });


    it('Обычный участник никого не может удалять', () => {
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

        //не может удалять
        cy.contains('tr', 'Организатор').find('.anticon.anticon-delete')
            .should('not.exist');

        cy.get('tr').filter(':contains("Администратор")').each($row => {
            cy.wrap($row).find('.anticon.anticon-delete').should('not.exist');
        });

        cy.get('tr').filter(':contains("Участник")').each($row => {
            cy.wrap($row).find('.anticon.anticon-delete').should('not.exist');
        });
    });
});