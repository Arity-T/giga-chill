import { PAGES } from "@config/pages.config";


describe('Списки покупок', () => {
    beforeEach(() => {
        cy.cleanupDatabase();
        cy.testEventSetup().as('eventId');

        cy.loginUserAPI('organizer');
        cy.get('@eventId').then((eventId) => {
            cy.createShoppingListAPI(`${eventId}`, {
                title: 'Аренда колонки',
                description: 'Музыка для всех'
            });
            cy.getShoppingListsAPI(`${eventId}`).then(({ body }) => {
                const shoppingListId = body[0].shopping_list_id;

                cy.getParticipantsAPI(`${eventId}`).then(({ body: participants }) => {
                    const participantsIds = participants.map(p => p.id);
                    cy.setShoppingListConsumersAPI(`${eventId}`, shoppingListId, participantsIds);
                });
            });

            cy.createShoppingListAPI(`${eventId}`, {
                title: 'Фрукты организатора',
                description: 'Фруктовая корзина организатора'
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
                .toggleShoppingList()
                .should('contain', 'Мясная тарелка')
                .getShoppingListStatus()
                .should('eq', 'Задача не назначена');
        });

        it('Может удалять любые списки покупок', () => {
            cy.getShoppingList('Фрукты организатора').getDeleteShoppingListBtn().click();
            cy.confirmModal();
            cy.getBySel('shopping-list-card').should('have.length', 3);
            cy.getShoppingList('Фрукты организатора').should('not.exist');

            cy.getShoppingList('Овощи администратора').getDeleteShoppingListBtn().click();
            cy.confirmModal();
            cy.getBySel('shopping-list-card').should('have.length', 2);
            cy.getShoppingList('Овощи администратора').should('not.exist');

            cy.getShoppingList('Алкоголь участника').getDeleteShoppingListBtn().click();
            cy.confirmModal();
            cy.getBySel('shopping-list-card').should('have.length', 1);
            cy.getShoppingList('Алкоголь участника').should('not.exist');

            cy.getShoppingList('Аренда колонки').getDeleteShoppingListBtn().click();
            cy.confirmModal();
            cy.getBySel('shopping-list-card').should('have.length', 0);
            cy.contains('Нет списков покупок').should('be.visible');
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
                .toggleShoppingList()
                .should('contain', 'Мясная тарелка')
                .getShoppingListStatus()
                .should('eq', 'Задача не назначена');
        });

        it('Может удалять любые списки покупок', () => {
            cy.getShoppingList('Фрукты организатора').getDeleteShoppingListBtn().click();
            cy.confirmModal();
            cy.getBySel('shopping-list-card').should('have.length', 3);
            cy.getShoppingList('Фрукты организатора').should('not.exist');

            cy.getShoppingList('Овощи администратора').getDeleteShoppingListBtn().click();
            cy.confirmModal();
            cy.getBySel('shopping-list-card').should('have.length', 2);
            cy.getShoppingList('Овощи администратора').should('not.exist');

            cy.getShoppingList('Алкоголь участника').getDeleteShoppingListBtn().click();
            cy.confirmModal();
            cy.getBySel('shopping-list-card').should('have.length', 1);
            cy.getShoppingList('Алкоголь участника').should('not.exist');

            cy.getShoppingList('Аренда колонки').getDeleteShoppingListBtn().click();
            cy.confirmModal();
            cy.getBySel('shopping-list-card').should('have.length', 0);
            cy.contains('Нет списков покупок').should('be.visible');
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
                .toggleShoppingList()
                .should('contain', 'Мясная тарелка')
                .getShoppingListStatus()
                .should('eq', 'Задача не назначена');
        });

        it('Может удалять списки покупок, потребителем которых является', () => {
            cy.getShoppingList('Фрукты организатора').getDeleteShoppingListBtn()
                .should('not.exist');

            cy.getShoppingList('Овощи администратора').getDeleteShoppingListBtn()
                .should('not.exist');

            cy.getShoppingList('Алкоголь участника').getDeleteShoppingListBtn().click();
            cy.confirmModal();
            cy.getBySel('shopping-list-card').should('have.length', 3);
            cy.getShoppingList('Алкоголь участника').should('not.exist');

            cy.getShoppingList('Аренда колонки').getDeleteShoppingListBtn().click();
            cy.confirmModal();
            cy.getBySel('shopping-list-card').should('have.length', 2);
            cy.getShoppingList('Аренда колонки').should('not.exist');
        });
    });
});