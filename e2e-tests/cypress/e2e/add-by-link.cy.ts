describe('Добавление участников по ссылке', { testIsolation: false }, () => {
    it('Создание мероприятия', () => {
        // Входим в систему
        cy.loginUserAPI('lili')

        // Создаём мероприятие используя команду
        cy.createEventAPI({
            title: 'Пикник другой25',
            location: 'Лес',
            start_datetime: '2025-08-20T06:00:00Z',
            end_datetime: '2025-08-30T20:00:00Z',
            description: 'всем добра!!!!'
        }).then((eventId) => {
            // Переход на страницу мероприятия
            cy.visit(`/events/${eventId}`);
        });
    });

    it('Добавление участников', () => {
        cy.getInvitationLinkUI().then((inviteUrl) => {
            cy.log('Повторное использование ссылки:', inviteUrl);
            cy.logoutUserUI('lili');
            cy.loginUserUI('xuxa');
            cy.visit(inviteUrl);
        });
    });
});