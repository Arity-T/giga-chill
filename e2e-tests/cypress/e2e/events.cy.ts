import { PAGES } from '@config/pages.config';
import dayjs from 'dayjs';

describe('Список мероприятий', () => {
    beforeEach(() => {
        cy.cleanupDatabase();
        cy.registerUserAPI('organizer', 'Юля');
        cy.loginUserAPI('organizer');
        cy.visit(PAGES.EVENTS);
    });

    it('Создание мероприятия', () => {
        const startDatetime = dayjs().add(1, 'day').hour(9).minute(15);
        const endDatetime = dayjs().add(2, 'day').hour(18).minute(45);

        cy.contains('button', 'Создать').click();

        cy.contains('.ant-modal-content', 'Создать мероприятие').within(() => {
            cy.get('input[placeholder="Введите название мероприятия"]')
                .type('Вечеринка');
            cy.get('input[placeholder="Введите адрес или место проведения"]')
                .type('Дома у Ксюши');
            cy.get('input[placeholder="Начало"]')
                .type(startDatetime.format('DD.MM.YYYY HH:mm') + '{enter}');
            cy.get('input[placeholder="Окончание"]')
                .type(endDatetime.format('DD.MM.YYYY HH:mm'));
            cy.get('textarea[placeholder*="описание"]').type('всем добра');
            cy.get('button').contains('Создать').click();
        });

        cy.contains('.ant-card', 'Вечеринка').click();

        cy.contains('h2', 'Вечеринка').should('be.visible');
        cy.contains('всем добра').should('be.visible');
        cy.contains(startDatetime.format('DD.MM.YYYY, HH:mm')).should('be.visible');
        cy.contains(endDatetime.format('DD.MM.YYYY, HH:mm')).should('be.visible');
        cy.contains('Дома у Ксюши').should('be.visible');
    });
});

describe('Настройки мероприятия', () => {
    beforeEach(() => {
        cy.cleanupDatabase();
        cy.testEventSetup().as('eventId');
        cy.resetBrowserState();
    });

    context('Организатор', () => {
        beforeEach(() => {
            cy.loginUserAPI('organizer');
            cy.get('@eventId').then((eventId) => {
                cy.visit(PAGES.EVENT_DETAILS(`${eventId}`));
            });
        });

        it('Может изменять любые настройки мероприятия', () => {
            const startDatetime = dayjs().add(2, 'day').hour(9).minute(15);
            const endDatetime = dayjs().add(3, 'day').hour(18).minute(45);

            cy.contains('.ant-menu-item a', 'Настройки').click();
            cy.get('@eventId').then((eventId) => {
                cy.url().should('include', PAGES.EVENT_SETTINGS(`${eventId}`));
            });

            cy.contains('div[class*="EditableField"]', 'Название мероприятия').within(() => {
                cy.get('input').clear().type('Супер-вечеринка!');
                cy.get('.anticon-check').click();
            });

            cy.contains('div[class*="EditableField"]', 'Место проведения').within(() => {
                cy.get('input').clear().type('Улица Пушкина, дом Колотушкина');
                cy.get('.anticon-check').click();
            });
            cy.contains('div[class*="EditableField"]', 'Дата и время').within(() => {
                cy.get('input[placeholder="Начало мероприятия"]').clear()
                    .type(startDatetime.format('DD.MM.YYYY HH:mm') + '{enter}');
                cy.get('input[placeholder="Конец мероприятия"]').clear()
                    .type(endDatetime.format('DD.MM.YYYY HH:mm') + '{enter}');
                cy.get('.anticon-check').click();
            });
            cy.contains('div[class*="EditableField"]', 'Описание').within(() => {
                cy.get('textarea').clear().type('всем удачи');
                cy.get('.anticon-check').click();
            });

            cy.get('@eventId').then((eventId) => {
                cy.visit(PAGES.EVENT_DETAILS(`${eventId}`));
            });

            cy.contains('h2', 'Супер-вечеринка!').should('be.visible');
            cy.contains('всем удачи').should('be.visible');
            cy.contains(startDatetime.format('DD.MM.YYYY, HH:mm')).should('be.visible');
            cy.contains(endDatetime.format('DD.MM.YYYY, HH:mm')).should('be.visible');
            cy.contains('Улица Пушкина, дом Колотушкина').should('be.visible');
        });

        it('Может удалить мероприятие', () => {
            cy.get('@eventId').then((eventId) => {
                cy.visit(PAGES.EVENT_SETTINGS(`${eventId}`));
            });

            cy.contains('button', 'Удалить мероприятие').click();
            cy.confirmModal();

            cy.url().should('include', PAGES.EVENTS);
            cy.contains('Нет мероприятий').should('be.visible');
        });
    });

    context('Администратор', () => {
        beforeEach(() => {
            cy.loginUserAPI('admin');
            cy.get('@eventId').then((eventId) => {
                cy.visit(PAGES.EVENT_DETAILS(`${eventId}`));
            });
        });

        it('Не может изменять настройки мероприятия', () => {
            // Предварительно проверяем, что меню отображается
            cy.contains('.ant-menu-item a', 'Общая информация').should('be.visible');
            // затем проверяем, что в меню нет настроек
            cy.contains('.ant-menu-item a', 'Настройки').should('not.exist');

            cy.get('@eventId').then((eventId) => {
                cy.visit(PAGES.EVENT_SETTINGS(`${eventId}`));
            });
            cy.contains('Доступ запрещён').should('be.visible');
        });
    });

    context('Участник', () => {
        beforeEach(() => {
            cy.loginUserAPI('participant');
            cy.get('@eventId').then((eventId) => {
                cy.visit(PAGES.EVENT_DETAILS(`${eventId}`));
            });
        });

        it('Не может изменять настройки мероприятия', () => {
            // Предварительно проверяем, что меню отображается
            cy.contains('.ant-menu-item a', 'Общая информация').should('be.visible');
            // затем проверяем, что в меню нет настроек
            cy.contains('.ant-menu-item a', 'Настройки').should('not.exist');

            cy.get('@eventId').then((eventId) => {
                cy.visit(PAGES.EVENT_SETTINGS(`${eventId}`));
            });
            cy.contains('Доступ запрещён').should('be.visible');
        });
    });
});