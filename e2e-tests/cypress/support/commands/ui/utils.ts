/// <reference types="cypress" />

declare global {
    namespace Cypress {
        interface Chainable {
            closeModal(): Chainable<void>;
        }
    }
}
export { } // Необходимо для использования global


Cypress.Commands.add('closeModal', { prevSubject: ['optional', 'element'] }, (modal) => {
    if (modal) {
        cy.wrap(modal).find('.ant-modal-close').click();
    } else {
        cy.get('.ant-modal-close').click();
    }
});