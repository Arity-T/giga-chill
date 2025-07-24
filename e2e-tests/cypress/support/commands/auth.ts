/// <reference types="cypress" />

import { PAGES } from '../config/pages.config';

/**
 * Команды для аутентификации пользователей
 */

// Custom command для регистрации пользователя
Cypress.Commands.add('registerUserUI', (name: string, username: string, password: string = '12345678') => {
    // Переходим на страницу регистрации, если ещё не там
    cy.url().then((url) => {
        if (!url.includes(PAGES.REGISTER)) {
            cy.visit(PAGES.REGISTER);
        }
    });

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

    // Проверяем успешную регистрацию (переход на главную)
    cy.url().should('include', PAGES.EVENTS);
});

// Custom command для логина пользователя
Cypress.Commands.add('loginUserUI', (username: string, password: string = '12345678') => {
    // Переходим на страницу логина, если ещё не там
    cy.url().then((url) => {
        if (!url.includes(PAGES.LOGIN)) {
            cy.visit(PAGES.LOGIN);
        }
    });

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
    cy.url().should('include', PAGES.EVENTS);

    // Проверяем наличие элемента с именем пользователя
    cy.get('.ant-dropdown-trigger', { timeout: 10000 })
        .should('be.visible')
        .and('contain', `@${username}`);
}); 