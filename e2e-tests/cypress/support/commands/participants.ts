/// <reference types="cypress" />
/**
 * Команды для работы с участниками мероприятий
 */

// Custom command для добавления участника по логину
Cypress.Commands.add('addParticipantByLoginUI', (username) => {
    // Переходим на вкладку участников
    cy.url().then((url) => {
        // TODO: переделать на использование конфига
        if (!url.includes("/participants")) {
            cy.contains('.ant-menu-item a', 'Участники').click();
        }
    });

    // Проверяем, что таблица с участниками загружена
    cy.contains('table', 'Организатор').should('be.visible');

    // Проверяем, что участник ещё не был добавлен
    cy.contains('tr', username).should('not.exist');

    // Ждём загрузки и нажимаем кнопку добавления участника
    cy.contains('button', 'Добавить участника').should('be.visible').click();

    // В появившемся модальном окне вводим логин пользователя и нажимаем кнопку добавления
    cy.contains('.ant-modal-content', 'Добавить участника').should('be.visible')
        .within(() => {
            cy.get('input[placeholder="Введите логин пользователя"]')
                .type(username)
                .should('have.value', username);

            cy.contains('button', 'Добавить участника').should('be.visible').click();
        });

    // Проверяем, что участник добавлен
    cy.contains('tr', username).should('exist');
});

// Custom command для изменения роли участника по имени
Cypress.Commands.add('changeParticipantRoleByNameUI', (participantName, newRole) => {
    // Переходим на вкладку участников
    cy.url().then((url) => {
        // TODO: переделать на использование конфига
        if (!url.includes("/participants")) {
            cy.contains('.ant-menu-item a', 'Участники').click();
        }
    });

    // Находим строку и сохраняем её в алиас
    cy.contains('tr', participantName).as('participantRow');

    // Кликаем на текущую роль в этой строке
    cy.get('@participantRow')
        .contains(/^(Участник|Администратор)$/)
        .should('be.visible')
        .click();

    // Выбираем новую роль
    cy.get('.ant-select-item').contains(newRole).should('be.visible').click();

    // Проверяем, что роль поменялась
    cy.get('@participantRow').contains(newRole).should('exist');
});




Cypress.Commands.add('getInvitationLinkUI', ()=>{
    // Переходим на вкладку участников
    cy.url().then((url) => {
        // TODO: переделать на использование конфига
        if (!url.includes("/participants")) {
            cy.contains('.ant-menu-item a', 'Участники').click();
        }
    });

    // Нажимаем на кнопку для добавления участника
    return cy.contains('button', 'Добавить участника').should('be.visible').click().then(()=>{
        // В появившемся модальном окне выбираем добавление по ссылке-приглашению
        return cy.contains('.ant-modal-content', 'Добавить участника').should('be.visible')
        .within(() => {
            cy.contains('.ant-tabs-tab', 'По ссылке-приглашению').should('be.visible').click();
        });
    }).then(()=>{
        // копируем ссылку-приглашение и осуществляем вход
        return cy.get('span.ant-typography code')
        .invoke('text')
        .then((inviteUrl) => {
            cy.log('Ссылка:', inviteUrl);
            cy.get('.ant-modal-close').click();
            return cy.wrap(inviteUrl);
        });
    })
});


