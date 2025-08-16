import { PAGES } from '@config/pages.config';
import dayjs from 'dayjs';

describe('Полный пользовательский сценарий', { testIsolation: false }, () => {
    before(() => {
        // Очищаем базу данных перед началом сценария
        cy.cleanupDatabase();

        cy.registerUserAPI("xuxa", "Ксения");
        cy.registerUserAPI("didi", "Дарья");
        cy.registerUserAPI("lili", "Юлия");
    });

    it('Создание мероприятия', () => {
        // Входим в систему
        cy.resetBrowserState();
        cy.loginUserAPI("lili");

        // Создаём мероприятие используя команду
        cy.createEventAPI({
            title: 'Пикник',
            location: 'Лес',
            start_datetime: dayjs().add(1, 'day').hour(17).toISOString(),
            end_datetime: dayjs().add(5, 'day').hour(20).toISOString(),
            description: 'всем добра!!!!'
        });
        cy.visit(PAGES.EVENTS);
        cy.contains('.ant-card', 'Пикник').click();
    });

    it('Добавление участников', () => {
        cy.contains('.ant-menu-item a', 'Участники').click();
        cy.openAddParticipantModal().addParticipantByLogin('xuxa');
        cy.openAddParticipantModal().addParticipantByLogin('didi');

        // Назначаем роль администратора
        cy.getParticipantRow('Ксения').setParticipantRole('Администратор');
        cy.getParticipantRow('Ксения').getParticipantRole().should('eq', 'Администратор');
    });

    it('Создание списка покупок', () => {
        cy.contains('.ant-menu-item a', 'Списки покупок').click();
        cy.createShoppingList('Напитки');

        cy.getShoppingList('Напитки')
            .toggleShoppingList()
            .addShoppingItem({
                name: 'Сок яблочный',
                quantity: '3',
                unit: 'л'
            })
            .setShoppingListConsumers()
            .getShoppingListConsumersCount()
            .should('equal', '3');

        cy.getShoppingList('Напитки')
            .getShoppingItem('Сок яблочный')
            .should('be.visible');
    });

    it('Создание задачи', () => {
        cy.contains('.ant-menu-item a', 'Задачи').click();

        cy.createTask({
            name: 'Купить напитки',
            deadline: dayjs().add(2, 'day').hour(17).format('DD.MM.YYYY HH:mm'),
            assigneeName: 'Ксения',
            shoppingLists: ['Напитки']
        });

        cy.getTaskCard('Купить напитки').should('be.visible');
    });

    it('Выполнение задачи исполнителем', () => {
        cy.resetBrowserState();
        cy.loginUserAPI("xuxa");
        cy.visit(PAGES.EVENTS);

        cy.contains('.ant-card', 'Пикник').click();
        cy.contains('.ant-menu-item a', 'Задачи').click();

        cy.getTaskCard('Купить напитки').click();

        cy.getTaskModal('Купить напитки')
            .takeTaskInProgress()
            .within(() => {
                cy.getShoppingList('Напитки')
                    .setShoppingListBudget('46')
                    .toggleShoppingList()
                    .getShoppingItem('Сок яблочный')
                    .markShoppingItemAsPurchased();

                cy.getShoppingList('Напитки')
                    .getShoppingListBudget()
                    .should('contain', '46');
            });

        cy.getTaskModal('Купить напитки').getTaskStatus().should('equal', 'В работе');

        cy.getTaskModal('Купить напитки').submitTaskForReview('купила')
            .getTaskStatus().should('equal', 'На проверке');
    });

    it('Проверка выполнения задачи ревьюером', () => {
        cy.resetBrowserState();
        cy.loginUserAPI("lili");
        cy.visit(PAGES.EVENTS);

        cy.contains('.ant-card', 'Пикник').click();
        cy.contains('.ant-menu-item a', 'Задачи').click();

        cy.getTaskCard('Купить напитки').click();

        cy.getTaskModal('Купить напитки').within(() => {
            cy.getShoppingList('Напитки').setShoppingListBudget('100');
        });

        cy.getTaskModal('Купить напитки').completeTask('молодец', true)
            .getTaskStatus().should('equal', 'Завершена')

        cy.closeModal();
    });

    it('Завершение мероприятия и проверка балансов', () => {
        // Завершаем мероприятие
        cy.finishEventUI();

        // Проверка балансов
        cy.checkParticipantBalanceUI('lili', '-33,33', 'Должник');
        cy.checkParticipantBalanceUI('xuxa', '66,66', 'Кредитор');
        cy.checkParticipantBalanceUI('didi', '-33,33', 'Должник');
    });
}); 