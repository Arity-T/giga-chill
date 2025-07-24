/// <reference types="cypress" />
// ***********************************************
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************

// Custom command для регистрации пользователя
Cypress.Commands.add('registerUserUI', (name: string, username: string, password: string = '12345678') => {
    cy.get('#register_name')
        .should('be.visible')
        .and('have.value', '')
        .type(name, { delay: 100 })
        .should('have.value', name);

    cy.get('#register_login')
        .type(username, { delay: 100 })
        .should('have.value', username);

    cy.get('#register_password')
        .type(password, { delay: 100 })
        .should('have.value', password);

    cy.get('#register_password2')
        .type(password, { delay: 100 })
        .should('have.value', password);

    cy.contains('button', 'Зарегистрироваться')
        .should('be.enabled')
        .click();
});

// Custom command для логина пользователя
Cypress.Commands.add('loginUserUI', (username: string, password: string = '12345678') => {
    cy.get('input[type="text"]')
        .type(username, { delay: 100 })
        .should('have.value', username);

    cy.get('input[type="password"]')
        .type(password, { delay: 100 })
        .should('have.value', password);

    cy.contains('button', 'Войти')
        .should('be.enabled')
        .click();

    // Проверяем успешный логин
    cy.url().should('include', '/events');

    // Проверяем наличие элемента с именем пользователя
    cy.get('.ant-dropdown-trigger', { timeout: 10000 })
        .should('be.visible')
        .and('contain', `@${username}`);
});

declare global {
    namespace Cypress {
        interface Chainable {
            registerUserUI(name: string, username: string, password?: string): Chainable<void>;
            loginUserUI(username: string, password?: string): Chainable<void>;
        }
    }
}

export { } 