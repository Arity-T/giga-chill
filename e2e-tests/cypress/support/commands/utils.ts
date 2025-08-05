/// <reference types="cypress" />

declare global {
    namespace Cypress {
        interface Chainable {
            cleanupDatabase(): Chainable<void>;
            closeModal(): Chainable<void>;
        }
    }
}
export { } // Необходимо для использования global


Cypress.Commands.add('cleanupDatabase', () => {
    cy.request('POST', `${Cypress.env('apiUrl')}/test-utils/cleanup`);
});


Cypress.Commands.add('closeModal', { prevSubject: ['optional', 'element'] }, (modal) => {
    if (modal) {
        cy.wrap(modal).find('.ant-modal-close').should('be.visible').click();
    } else {
        cy.get('.ant-modal-close').should('be.visible').click();
    }
});