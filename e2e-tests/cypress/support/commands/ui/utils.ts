/// <reference types="cypress" />

declare global {
    namespace Cypress {
        interface Chainable {
            closeModal(): Chainable<void>;
            confirmModal(): Chainable<void>;
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


Cypress.Commands.add('confirmModal', () => {
    cy.get('.ant-modal-confirm-btns').contains('button', 'Да').click();
});