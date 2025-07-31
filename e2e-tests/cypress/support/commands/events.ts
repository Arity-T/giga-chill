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






/**
 * Создает мероприятие через API
 * @param eventData - данные для создания мероприятия
 * @param authToken - токен авторизации (опционально, если нужен другой пользователь)
 */

Cypress.Commands.add('createEventAPI', (eventData) =>{
    cy.request({
        method: 'POST',
        url:`${Cypress.env('apiUrl')}/events`,
        body:{
            title: eventData.title,
            location: eventData.location,
            description: eventData.description || '',
            start_datetime: formatEventDate(eventData.startDay, eventData.startHour),
            end_datetime: formatEventDate(eventData.endDay, eventData.endHour)
        }
    }).then((response) => {
        expect(response.status).to.eq(204);// Проверка, что запрос прошёл успешно
    
        // Получить список всех мероприятий пользователя
        cy.request({
            method: 'GET',
            url: `${Cypress.env('apiUrl')}/events`,
        }).then((getResponse) => {
            // cy.log('Список мероприятий:', JSON.stringify(getResponse.body));
            expect(getResponse.status).to.eq(200);

            // Найти только что созданное мероприятие
            const createdEvent = getResponse.body.find((event) =>
                event.title === eventData.title
            );

            // cy.log('Мероприятие', JSON.stringify(createdEvent));
            // cy.log('Мероприятие', JSON.stringify(createdEvent.event_id));
            if (!createdEvent) {
                expect(createdEvent, 'Мероприятие не найдено после создания').to.exist;
            }

            // ID мероприятия
            return createdEvent.event_id;
        });
    });
});

/**
 * Вспомогательная функция для форматирования даты
 */
function formatEventDate(day: string, hour: string): string {
  const now = new Date();
  const year = now.getFullYear();
  const month = now.getMonth(); 

  const date = new Date(year, month, Number(day), Number(hour), 0, 0);
  return date.toISOString();
}
