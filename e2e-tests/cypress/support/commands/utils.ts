/// <reference types="cypress" />

declare global {
    namespace Cypress {
        interface Chainable {
            resetBrowserState(): Chainable<void>;
            cleanupDatabase(): Chainable<void>;
        }
    }
}
export { } // Необходимо для использования global

Cypress.Commands.add('cleanupDatabase', () => {
    cy.request('POST', `${Cypress.env('apiUrl')}/test-utils/cleanup`);
});

Cypress.Commands.add('resetBrowserState', () => {
    cy.clearAllCookies();
    cy.clearAllLocalStorage();
    cy.clearAllSessionStorage();
    cy.window().then((win) => {
        win.location.href = 'about:blank'
    })
});