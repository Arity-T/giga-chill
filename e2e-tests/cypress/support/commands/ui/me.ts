/// <reference types="cypress" />

declare global {
    namespace Cypress {
        interface Chainable {
            registerUser(login: string, name: string, password: string): Chainable<void>;
            loginUser(login: string, password: string): Chainable<void>;
        }
    }
};
export { } // Необходимо для использования global


Cypress.Commands.add('registerUser', (login, name, password = '12345678') => {
    cy.get('#register_name').type(name);
    cy.get('#register_login').type(login);
    cy.get('#register_password').type(password);
    cy.get('#register_password2').type(password);
    cy.contains('button', 'Зарегистрироваться').click();
});


Cypress.Commands.add('loginUser', (login, password) => {
    cy.get('#login_login').type(login);
    cy.get('#login_password').type(password);
    cy.contains('button', 'Войти').click();
});