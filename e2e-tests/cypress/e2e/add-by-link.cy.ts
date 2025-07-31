import { link } from "fs";

describe('Полный пользовательский сценарий', { testIsolation: false }, () => {
    it('Создание мероприятия', () => {
        // Входим в систему
        cy.loginUserAPI('lili')

        // Создаём мероприятие используя команду
        cy.createEventAPI({
            title: 'Пикник другой',
            location: 'Лес',
            startDay: '20',
            startHour: '03',
            endDay: '30',
            endHour: '20',
            description: 'всем добра!!!!'
        });
        
        // заходим на страничку с мероприятиями
        // cy.visit('/events')
    });

    it('Добавление участников', () => {
        cy.addParticipantByLink('lili', 'xuxa');
    });
});