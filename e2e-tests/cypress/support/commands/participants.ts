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




Cypress.Commands.add('addParticipantByLink', (adminLogin, participantLogin)=>{
    // Переходим на вкладку участников
    cy.url().then((url) => {
        // TODO: переделать на использование конфига
        if (!url.includes("/participants")) {
            cy.contains('.ant-menu-item a', 'Участники').click();
        }
    });

    // Проверяем, что таблица с участниками загружена
    cy.contains('table', 'Организатор').should('be.visible');

    // Ждём загрузки и нажимаем кнопку добавления участника
    cy.contains('button', 'Добавить участника').should('be.visible').click();

    // В появившемся модальном окне вводим логин пользователя и нажимаем кнопку добавления
    cy.contains('.ant-modal-content', 'Добавить участника').should('be.visible')
        .within(() => {
            cy.contains('.ant-tabs-tab', 'По ссылке-приглашению').should('be.visible').click();
        });

    cy.get('span.ant-typography code')
    .invoke('text')
    .then((inviteUrl) => {
        cy.log('Ссылка:', inviteUrl);
        cy.get('.ant-modal-close').click();
        cy.contains('button', adminLogin).should('be.visible').click();
        cy.get('.ant-dropdown-menu-item').should('be.visible').click();
        cy.url().should('include', '/auth');
        cy.joinEventByInvite(inviteUrl, participantLogin);
    });
});



// commands/participants.ts
Cypress.Commands.add('joinEventByInvite', (
  inviteLink: string,
  username: string,
  password: string = '12345678'
) => {
  // Переходим по ссылке-приглашению
  cy.visit(inviteLink);

  // Заполняем даннные для входа
  cy.get('#login_login')
        .type(username)
        .should('have.value', username);

    cy.get('#login_password')
        .type(password)
        .should('have.value', password);

    cy.contains('button', 'Войти')
        .should('be.enabled')
        .click();

  // Проверяем успешное присоединение
  cy.contains('Успешно присоединились!').should('be.visible');
});

