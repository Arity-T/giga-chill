/// <reference types="cypress" />

import type { LoginRequest, RegisterRequest } from '@typings/api';

declare global {
    namespace Cypress {
        interface Chainable {
            registerUserAPI(login: string, name?: string, password?: string): Chainable<void>;
            loginUserAPI(login: string, password?: string): Chainable<void>;
        }
    }
};
export { } // Необходимо для использования global

Cypress.Commands.add('registerUserAPI', (login, name = undefined, password = '12345678') => {
    if (!name) name = login;
    cy.request({
        method: 'POST',
        url: `${Cypress.env('apiUrl')}/auth/register`,
        body: {
            login,
            name,
            password
        }
    });
});


Cypress.Commands.add('loginUserAPI', (login, password = '12345678') => {
    cy.request({
        method: 'POST',
        url: `${Cypress.env('apiUrl')}/auth/login`,
        body: {
            login,
            password
        }
    });
});