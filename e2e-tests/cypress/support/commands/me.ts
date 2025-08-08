/// <reference types="cypress" />

import { PAGES } from '../config/pages.config';
import { LoginRequest, RegisterRequest } from '../types';

declare global {
    namespace Cypress {
        interface Chainable {
            registerUserUI(name: string, username: string, password?: string): Chainable<void>;
            registerUserAPI(registerRequest: RegisterRequest): Chainable<void>;
            loginUserUI(username: string, password?: string): Chainable<void>;
            loginUserAPI(loginRequest: LoginRequest): Chainable<void>;
        }
    }
};
export { } // Необходимо для использования global


Cypress.Commands.add('registerUserUI', (name, username, password = '12345678') => {
    // Переходим на страницу регистрации, если ещё не там
    cy.url().then((url) => {
        if (!url.includes(PAGES.REGISTER)) {
            cy.visit(PAGES.REGISTER);
        }
    });

    cy.get('#register_name').type(name);

    cy.get('#register_login').type(username);

    cy.get('#register_password').type(password);

    cy.get('#register_password2').type(password);

    cy.contains('button', 'Зарегистрироваться').click();

    // Проверяем успешную регистрацию (переход на главную)
    cy.url().should('include', PAGES.HOME);

    // Проверяем наличие элемента с именем пользователя
    cy.get('.ant-dropdown-trigger')
        .should('be.visible')
        .and('contain', `@${username}`);
});


Cypress.Commands.add('registerUserAPI', registerRequest => {
    cy.request({
        method: 'POST',
        url: `${Cypress.env('apiUrl')}/auth/register`,
        body: registerRequest
    });
});


Cypress.Commands.add('loginUserUI', (username, password = '12345678') => {
    // Переходим на страницу логина, если ещё не там
    cy.url().then((url) => {
        if (!url.includes(PAGES.LOGIN)) {
            cy.visit(PAGES.LOGIN);
        }
    });

    cy.get('#login_login')
        .type(username)
        .should('have.value', username);

    cy.get('#login_password')
        .type(password)
        .should('have.value', password);

    cy.contains('button', 'Войти')
        .should('be.enabled')
        .click();

    // Проверяем успешный логин
    cy.url().should('include', PAGES.HOME);

    // Проверяем наличие элемента с именем пользователя
    cy.get('.ant-dropdown-trigger')
        .should('be.visible')
        .and('contain', `@${username}`);
});


Cypress.Commands.add('loginUserAPI', loginRequest => {
    cy.request({
        method: 'POST',
        url: `${Cypress.env('apiUrl')}/auth/login`,
        body: loginRequest
    });
});