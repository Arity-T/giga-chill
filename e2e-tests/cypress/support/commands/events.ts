/// <reference types="cypress" />
/**
 * Команды для работы с мероприятиями
 */

import { PAGES } from '../config/pages.config';

/**
 * Создает новое мероприятие через UI интерфейс.
 * Команда автоматически открывает страницу мероприятий, заполняет форму
 * и после успешного создания переходит на страницу созданного мероприятия.
 */
Cypress.Commands.add('createEventUI', (eventData) => {
    // Переходим на страницу мероприятий, если ещё не там
    cy.url().then((url) => {
        if (!url.includes(PAGES.EVENTS)) {
            cy.visit(PAGES.EVENTS);
        }
    });

    // Нажимаем кнопку создания
    cy.contains('button', 'Создать')
        .should('be.enabled')
        .click();

    // Находим модальное окно с формой добавления мероприятия
    cy.contains('.ant-modal-content', 'Создать мероприятие').should('be.visible').as('createEventModal');

    // Внутри модального окна
    cy.get('@createEventModal')
        .within(() => {
            // Заполняем название
            cy.get('input[placeholder="Введите название мероприятия"]')
                .type(eventData.title)
                .should('have.value', eventData.title);

            // Заполняем место проведения
            cy.get('input[placeholder="Введите адрес или место проведения"]')
                .type(eventData.location)
                .should('have.value', eventData.location);

            // Заполняем описание, если передано
            if (eventData.description) {
                cy.get('textarea[placeholder*="описание"]')
                    .type(eventData.description);
            }
        });

    // TODO: Выбор даты и времени работает плохо, нужно вынести в отдельную команду и исправить
    // Выбираем дату и время начала
    cy.get('@createEventModal').find('input[placeholder="Начало"]').click();

    // Внутри панели выбора даты и времени
    cy.get('.ant-picker-datetime-panel-container').should('be.visible').within(() => {
        cy.get('.ant-picker-cell').contains(eventData.startDay).click();
        cy.get('.ant-picker-time-panel-column')
            .first()
            .contains(eventData.startHour)
            .click();
        cy.contains('ОК').click();
    });

    // Выбираем дату и время окончания
    cy.get('@createEventModal').find('input[placeholder="Окончание"]').click();

    // Внутри панели выбора даты и времени
    cy.get('.ant-picker-datetime-panel-container').should('be.visible').within(() => {
        cy.get('.ant-picker-cell').contains(eventData.endDay).click();
        cy.get('.ant-picker-time-panel-column')
            .first()
            .contains(eventData.endHour)
            .click();
        cy.contains('ОК').click();
    });

    // Создаём мероприятие
    cy.get('@createEventModal').find('button').contains('Создать').click();

    // Ждём создания и переходим на страницу мероприятия
    cy.contains('.ant-card', eventData.title).should('be.visible').click();
}); 