/// <reference types="cypress" />

import { PAGES } from '../config/pages.config';

/**
 * Команды для работы с мероприятиями
 */

interface CreateEventData {
    title: string;
    location: string;
    startDay: string;
    startHour: string;
    endDay: string;
    endHour: string;
    description?: string;
}

// Custom command для создания мероприятия
Cypress.Commands.add('createEventUI', (eventData: CreateEventData) => {
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

    // Заполняем название
    cy.get('input[placeholder="Введите название мероприятия"]')
        .type(eventData.title)
        .should('have.value', eventData.title);

    // Заполняем место проведения
    cy.get('input[placeholder="Введите адрес или место проведения"]')
        .type(eventData.location)
        .should('have.value', eventData.location);

    // Выбираем дату и время начала
    cy.get('input[placeholder="Начало"]').click();
    cy.get('.ant-picker-cell').contains(eventData.startDay).click();
    cy.get('.ant-picker-time-panel-column')
        .first()
        .contains(eventData.startHour)
        .click();
    cy.contains('ОК').click();

    // Выбираем дату и время окончания
    cy.get('input[placeholder="Окончание"]').click();
    cy.get('.ant-picker-cell').contains(eventData.endDay).click();
    cy.get('.ant-picker-time-panel-column')
        .first()
        .contains(eventData.endHour)
        .click();
    cy.contains('ОК').click();

    // Заполняем описание, если передано
    if (eventData.description) {
        cy.get('textarea[placeholder*="описание"]')
            .type(eventData.description);
    }

    // Создаём мероприятие
    cy.get('button:contains("Создать")').last().click();

    // Ждём создания и переходим на страницу мероприятия
    cy.wait(2000);
    cy.contains(eventData.title).click();
}); 