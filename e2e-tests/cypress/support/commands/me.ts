/// <reference types="cypress" />
/**
 * Команды для работы с пользователем (Me)
 */

import { PAGES } from '../config/pages.config';


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





/**
 * Команда для входа пользователя через API
 * @param username - логин пользователя
 * @param password - пароль (по умолчанию '12345678')
 */
Cypress.Commands.add('loginUserAPI', (username, password = '12345678') => {
  // Отправляем POST-запрос на эндпоинт входа
  cy.request({
    method: 'POST',
    url: `${Cypress.env('apiUrl')}/auth/login`, // Путь к эндпоинту аутентификации
    body: {
      login: username,
      password: password
    },
    failOnStatusCode: false // Не завершать тест при ошибках
  }).then((response) => {
    // Проверяем, что сервер вернул ответ
    expect(response).to.not.be.null;

    // Проверяем успешный статус ответа
    if (response.status !== 204) {
      throw new Error(`Login failed. Status: ${response.status}`);
    }
  });
});


