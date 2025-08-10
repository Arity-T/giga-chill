import { PAGES } from "@config/pages.config";

describe('Списки покупок', () => {
    beforeEach(() => {
        cy.cleanupDatabase();
        cy.testEventSetup().as('eventId');

        cy.loginUserAPI('organizer');
        cy.get('@eventId').then((eventId) => {
            cy.createShoppingListAPI(`${eventId}`, {
                title: 'Фрукты организатора',
                description: 'Фрутктовая корзина организатора'
            });
        });

        cy.loginUserAPI('admin');
        cy.get('@eventId').then((eventId) => {
            cy.createShoppingListAPI(`${eventId}`, {
                title: 'Овощи администратора',
                description: 'Овощная корзина администратора'
            });
        });

        cy.loginUserAPI('participant');
        cy.get('@eventId').then((eventId) => {
            cy.createShoppingListAPI(`${eventId}`, {
                title: 'Алкоголь участника',
                description: 'Алкоголь вреден для тела и духа'
            });
        });

        cy.resetBrowserState();
    });

    context('Организатор', () => {
        beforeEach(() => {
            cy.loginUserAPI('organizer');
            cy.get('@eventId').then((eventId) => {
                cy.visit(PAGES.EVENT_SHOPPING(`${eventId}`));
            });
        });

        it('Может создавать списки покупок', () => {
            cy.createShoppingList('Мясо', 'Мясная тарелка');
            cy.getShoppingList('Мясо')
                .should('be.visible')
                .toogleShoppingList()
                .should('contain', 'Мясная тарелка')
                .getShoppingListStatus()
                .should('eq', 'Задача не назначена');
        });
    });

    context('Администратор', () => {
        beforeEach(() => {
            cy.loginUserAPI('admin');
            cy.get('@eventId').then((eventId) => {
                cy.visit(PAGES.EVENT_SHOPPING(`${eventId}`));
            });
        });

        it('Может создавать списки покупок', () => {
            cy.createShoppingList('Мясо', 'Мясная тарелка');
            cy.getShoppingList('Мясо')
                .should('be.visible')
                .toogleShoppingList()
                .should('contain', 'Мясная тарелка')
                .getShoppingListStatus()
                .should('eq', 'Задача не назначена');
        });
    });

    context('Участник', () => {
        beforeEach(() => {
            cy.loginUserAPI('participant');
            cy.get('@eventId').then((eventId) => {
                cy.visit(PAGES.EVENT_SHOPPING(`${eventId}`));
            });
        });

        it('Может создавать списки покупок', () => {
            cy.createShoppingList('Мясо', 'Мясная тарелка');
            cy.getShoppingList('Мясо')
                .should('be.visible')
                .toogleShoppingList()
                .should('contain', 'Мясная тарелка')
                .getShoppingListStatus()
                .should('eq', 'Задача не назначена');
        });
    });
});