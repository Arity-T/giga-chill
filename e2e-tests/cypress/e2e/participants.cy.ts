import dayjs from "dayjs";
import { PAGES } from "@config/pages.config";
import { UserRole } from "@typings/api";

describe('Управление участниками мероприятия', () => {
    beforeEach(() => {
        cy.cleanupDatabase();
        cy.testEventSetup().as('eventId');
        cy.registerUserAPI('guest', 'Влад');
        cy.resetBrowserState();
    });

    context('Организатор', () => {
        beforeEach(() => {
            cy.loginUserAPI('organizer');
            cy.get('@eventId').then((eventId) => {
                cy.visit(PAGES.EVENT_PARTICIPANTS(`${eventId}`));
            });
        });

        it('Может изменять роли других участников', () => {
            cy.getParticipantRow('admin')
                .getParticipantRole().should('eq', 'Администратор');
            cy.getParticipantRow('participant')
                .getParticipantRole().should('eq', 'Участник');

            cy.getParticipantRow('admin')
                .setParticipantRole('Участник');

            cy.getParticipantRow('admin')
                .getParticipantRole().should('eq', 'Участник');

            cy.getParticipantRow('participant')
                .setParticipantRole('Администратор');

            cy.getParticipantRow('participant')
                .getParticipantRole().should('eq', 'Администратор');
        });

        it('Может добавлять участников по логину', () => {
            cy.get('.ant-table-tbody tr').should('have.length', 3);

            cy.openAddParticipantModal()
                .addParticipantByLogin('guest');

            cy.get('.ant-table-tbody tr').should('have.length', 4);
            cy.getParticipantRow('guest')
                .getParticipantRole().should('eq', 'Участник');
        });

        it('Может удалять участников', () => {
            cy.getParticipantRow('organizer').should('be.visible')
                .getDeleteParticipantBtn().should('not.exist');

            cy.getParticipantRow('admin').getDeleteParticipantBtn().click();

            cy.confirmModal();

            cy.get('.ant-table-tbody tr').should('have.length', 2);
            cy.getParticipantRow('admin').should('not.exist');

            cy.getParticipantRow('participant').getDeleteParticipantBtn().click();

            cy.confirmModal();

            cy.get('.ant-table-tbody tr').should('have.length', 1);
            cy.getParticipantRow('participant').should('not.exist');
        });
    });

    context('Администратор', () => {
        beforeEach(() => {
            cy.loginUserAPI('admin');
            cy.get('@eventId').then((eventId) => {
                cy.visit(PAGES.EVENT_PARTICIPANTS(`${eventId}`));
            });
        });

        it('Не может изменять роли других участников', () => {
            cy.getParticipantRow('organizer').should('be.visible')
                .getParticipantRoleSelect().should('not.exist');

            cy.getParticipantRow('participant').should('be.visible')
                .getParticipantRoleSelect().should('not.exist');

            cy.getParticipantRow('admin').should('be.visible')
                .getParticipantRoleSelect().should('not.exist');
        });

        it('Может добавлять участников по логину', () => {
            cy.get('.ant-table-tbody tr').should('have.length', 3);

            cy.openAddParticipantModal()
                .addParticipantByLogin('guest');

            cy.get('.ant-table-tbody tr').should('have.length', 4);
            cy.getParticipantRow('guest')
                .getParticipantRole().should('eq', 'Участник');
        });

        it('Может удалять участников', () => {
            cy.getParticipantRow('organizer').should('be.visible')
                .getDeleteParticipantBtn().should('not.exist');

            cy.getParticipantRow('admin').should('be.visible')
                .getDeleteParticipantBtn().should('not.exist');

            cy.getParticipantRow('participant').getDeleteParticipantBtn().click();
            cy.confirmModal();

            cy.get('.ant-table-tbody tr').should('have.length', 2);
            cy.getParticipantRow('participant').should('not.exist');
        });
    });

    context('Участник', () => {
        beforeEach(() => {
            cy.loginUserAPI('participant');
            cy.get('@eventId').then((eventId) => {
                cy.visit(PAGES.EVENT_PARTICIPANTS(`${eventId}`));
            });
        });

        it('Может только просматривать список участников и их роли', () => {
            cy.getParticipantRow('organizer').should('be.visible')
                .getParticipantRole().should('eq', 'Организатор');
            cy.getParticipantRow('organizer')
                .getParticipantRoleSelect().should('not.exist');
            cy.getParticipantRow('organizer')
                .getDeleteParticipantBtn().should('not.exist');

            cy.getParticipantRow('admin').should('be.visible')
                .getParticipantRole().should('eq', 'Администратор');
            cy.getParticipantRow('admin')
                .getParticipantRoleSelect().should('not.exist');
            cy.getParticipantRow('admin')
                .getDeleteParticipantBtn().should('not.exist');

            cy.getParticipantRow('participant').should('be.visible')
                .getParticipantRole().should('eq', 'Участник');
            cy.getParticipantRow('participant')
                .getParticipantRoleSelect().should('not.exist');
            cy.getParticipantRow('participant')
                .getDeleteParticipantBtn().should('not.exist');
        });
    });
});