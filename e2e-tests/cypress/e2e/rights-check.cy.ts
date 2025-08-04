import { PAGES } from "../support/config/pages.config";

describe('Проверка прав, соответствующих роли участника мероприятия', () => {

    let eventID; // создаем глобальную переменную в которой будем хранить id мероприятия

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

        cy.registerUserAPI({
            login: 'aaaa',
            password: '12345678',
            name: '1'
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
            eventID = `${eventId}`;

            cy.addParticipantByLoginAPI(`${eventId}`, 'xuxa')

            cy.addParticipantByLoginAPI(`${eventId}`, 'didi')

            cy.changeParticipantRoleAPI(`${eventId}`, 'xuxa', 'Администратор')
        });

        // Очищаем состояние браузера
        cy.clearLocalStorage();
        cy.clearCookies();
    });


    it('Проверка прав создателя мероприятия', () => {
        // заходим в мероприятие как создатель
        cy.loginUserAPI({
            login: 'lili',
            password: '12345678'
        })

        // открываем мероприятие
        cy.visit(PAGES.EVENT_PARTICIPANTS(eventID))

        // проверяем нашу роль в мероприятии
        cy.contains('.ant-space-item', 'Организатор').should('be.visible');

        // проверяем наши возможности для добавления участников
        cy.contains('button', 'Добавить участника').should('be.visible');

        // можем добавлять по логину
        cy.addParticipantByLoginUI('aaaa');

        // тесты по добавлению участника по ссылке со стороны создателя
        // уже есть и находятся в add-by-link.cy.ts:
        // -- можем добавлять по ссылке
        // -- можем сгенерировать новую ссылку и пригласить по ней

        // можем менять роли участников
        cy.changeParticipantRoleByNameUI('aaaa', 'Администратор');

        // можем удалить любого участника, кроме себя
        cy.contains('tr', '@aaaa') // Находим строку с нужным участником
            .find('.anticon.anticon-delete') // Ищем кнопку внутри строки
            .click();

        cy.get('.ant-modal-content')
            .should('be.visible')
            .within(() => {
                cy.contains('button', 'Да, удалить').click();
            });

        cy.get('.ant-table-tbody').within(() => {
            cy.contains('@aaaa').should('not.exist');
        });

        // проверяем, что не можем удалить создателя(то есть себя),
        // но при этом можем удалить администратора
        cy.contains('tr', 'Организатор').find('.anticon.anticon-delete')
            .should('not.exist');

        cy.get('tr').filter(':contains("Администратор")').each($row => {
            cy.wrap($row).find('.anticon.anticon-delete').should('be.visible');
        });

    });


    it('Проверка прав администратора мероприятия', () => {
        // заходим в мероприятие как администратор
        cy.loginUserAPI({
            login: 'xuxa',
            password: '12345678'
        })

        // открываем мероприятие
        cy.visit(PAGES.EVENT_PARTICIPANTS(eventID))

        // проверяем нашу роль в мероприятии
        cy.contains('.ant-space-item', 'Администратор').should('be.visible');

        // проверяем наши возможности для добавления участников
        cy.contains('button', 'Добавить участника').should('be.visible');


        // можем добавлять по логину
        cy.addParticipantByLoginUI('aaaa');


        // можем добавлять по ссылке
        // вот мы можем открыть модалку для добавления участника 
        // и перейти в раздел, где ссылка-приглашение
        cy.openInviteByLinkModal()
        cy.get<string>('@inviteLink').then((inviteUrl) => {
            cy.log('Текущая ссылка: ' + `${inviteUrl}`);
        });

        // но здесь у нас нет кнопки, чтобы сгенерировать новую ссылку (((
        cy.get('.ant-modal-content')
            .should('be.visible')
            .within(() => {
                cy.contains('button', 'Создать новую ссылку').should('not.exist');
                cy.get('.ant-modal-close').click();
            });


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
        cy.contains('tr', '@aaaa') // Находим строку с нужным участником
            .find('.anticon.anticon-delete') // Ищем кнопку внутри строки
            .click();

        cy.get('.ant-modal-content')
            .should('be.visible')
            .within(() => {
                cy.contains('button', 'Да, удалить').click();
            });

        cy.get('.ant-table-tbody').within(() => {
            cy.contains('@aaaa').should('not.exist');
        });


        // не может менять роли
        cy.get('tr').each($row => {
            cy.wrap($row).within(() => {
                cy.get('.ant-select-arrow').should('not.exist');
            });
        });
    });


    it('Проверка прав участника мероприятия', () => {
        // заходим в мероприятие как участник
        cy.loginUserAPI({
            login: 'didi',
            password: '12345678'
        })

        // открываем мероприятие
        cy.visit(PAGES.EVENT_PARTICIPANTS(eventID))

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