/// <reference types="cypress" />
/**
 * Команды для работы с пользователем (Me)
 */

import { PAGES } from '../config/pages.config';
import { LoginRequestAPI } from '../types';


// Custom command для регистрации пользователя
Cypress.Commands.add('registerUserUI', (name, username, password = '12345678') => {
    // Переходим на страницу регистрации, если ещё не там
    cy.url().then((url) => {
        if (!url.includes(PAGES.REGISTER)) {
            cy.visit(PAGES.REGISTER);
        }
    });

    cy.get('#register_name')
        .should('be.visible')
        .and('have.value', '')
        .type(name)
        .should('have.value', name);

    cy.get('#register_login')
        .type(username)
        .should('have.value', username);

    cy.get('#register_password')
        .type(password)
        .should('have.value', password);

    cy.get('#register_password2')
        .type(password)
        .should('have.value', password);

    cy.contains('button', 'Зарегистрироваться')
        .should('be.enabled')
        .click();

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
        body: registerRequest,
        failOnStatusCode: false
    }).then((response) => {
        expect(response.status).to.eq(204);
    });
});

// Custom command для логина пользователя
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


Cypress.Commands.add('logoutUserUI', (username) => {
    cy.contains('button', username).should('be.visible').click();
    cy.get('.ant-dropdown-menu-item').should('be.visible').click();
    cy.url().should('include', '/auth');
})


/**
 * Команда для входа пользователя через API
 */
Cypress.Commands.add('loginUserAPI', loginRequest => {
    // Отправляем POST-запрос на эндпоинт входа
    cy.request({
        method: 'POST',
        url: `${Cypress.env('apiUrl')}/auth/login`, // Путь к эндпоинту аутентификации
        body: loginRequest,
        failOnStatusCode: false // Не завершать тест при ошибках
    }).then((response) => {
        expect(response.status).to.eq(204);
    });
});


