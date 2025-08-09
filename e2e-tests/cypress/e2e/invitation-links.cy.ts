import { PAGES } from "@config/pages.config";
import dayjs from "dayjs";

describe('Ссылки-приглашения', () => {
    beforeEach(() => {
        cy.cleanupDatabase();

        cy.registerUserAPI('organizer', 'Юля')
        cy.registerUserAPI('participant', 'Ксюша')

        cy.loginUserAPI('organizer')
        cy.createEventAPI({
            title: 'Пикник',
            location: 'Лес',
            start_datetime: dayjs().add(1, 'day').hour(17).toISOString(),
            end_datetime: dayjs().add(1, 'day').hour(20).toISOString(),
            description: 'всем добра!!!!'
        });
        cy.getEventsAPI().then((response) => {
            cy.wrap(response.body[0].event_id).as('eventId');
        });
    });

    it('Добавление участника по ссылке-приглашению', () => {
        // Создаём ссылку-приглашение
        cy.loginUserAPI('organizer')

        cy.get('@eventId').then((eventId) => {
            cy.visit(PAGES.EVENT_PARTICIPANTS(`${eventId}`));
        });
        cy.openAddParticipantModal()
            .switchToInviteByLinkTab()
            .getInvitationLink()
            .as('inviteLink');

        // Переходим по ссылке-приглашению
        cy.resetBrowserState();
        cy.loginUserAPI('participant');
        cy.get('@inviteLink').then((inviteUrl) => {
            cy.log('Текущая ссылка: ' + `${inviteUrl}`);
            cy.visit(`${inviteUrl}`);
        });

        // Проверяем редирект на страницу мероприятия
        cy.get('@eventId').then((eventId) => {
            cy.url().should('include', PAGES.EVENT_DETAILS(`${eventId}`));
        });
        // Проверяем, что участник добавлен
        cy.contains('Ксюша').should('be.visible');
    });

    it('Регенерация ссылки-приглашения и добавление участника', () => {
        // Создаём ссылку-приглашение
        cy.loginUserAPI('organizer')

        cy.get('@eventId').then((eventId) => {
            cy.visit(PAGES.EVENT_PARTICIPANTS(`${eventId}`));
        });

        cy.openAddParticipantModal().as('addParticipantModal');

        cy.get('@addParticipantModal')
            .switchToInviteByLinkTab()
            .getInvitationLink()
            .then((oldInviteLink) => {
                // Сохраняем ссылку-приглашение после её изменения
                cy.get('@addParticipantModal')
                    .regenerateInvitationLink()
                    .getInvitationLink()
                    .should('not.equal', oldInviteLink)
                    .as('inviteLink');
            });

        // Переходим по ссылке-приглашению
        cy.resetBrowserState();
        cy.loginUserAPI('participant')
        cy.get('@inviteLink').then((inviteUrl) => {
            cy.log('Текущая ссылка: ' + `${inviteUrl}`);
            cy.visit(`${inviteUrl}`);
        });

        // Проверяем редирект на страницу мероприятия
        cy.get('@eventId').then((eventId) => {
            cy.url().should('include', PAGES.EVENT_DETAILS(`${eventId}`));
        });
        // Проверяем, что участник добавлен
        cy.contains('Ксюша').should('be.visible');
    });
});