/// <reference types="cypress" />

import { LoginRequest, RegisterRequest } from '../../types/api';

declare global {
    namespace Cypress {
        interface Chainable {
            registerUserAPI(registerRequest: RegisterRequest): Chainable<void>;
            loginUserAPI(loginRequest: LoginRequest): Chainable<void>;
        }
    }
};
export { } // Необходимо для использования global


Cypress.Commands.add('registerUserAPI', registerRequest => {
    cy.request({
        method: 'POST',
        url: `${Cypress.env('apiUrl')}/auth/register`,
        body: registerRequest
    });
});


Cypress.Commands.add('loginUserAPI', loginRequest => {
    cy.request({
        method: 'POST',
        url: `${Cypress.env('apiUrl')}/auth/login`,
        body: loginRequest
    });
});