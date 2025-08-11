import { PAGES } from '@config/pages.config';

describe('Регистрация', () => {
    beforeEach(() => {
        cy.cleanupDatabase();
    });

    it('Регистрация пользователя', () => {
        cy.visit(PAGES.REGISTER);
        cy.registerUser('xuxa', 'Ксюша', '12345678');

        cy.url().should('include', PAGES.HOME);
        cy.contains('Ксюша').should('be.visible');
    });

    it('Редирект на желаемую страницу после регистрации', () => {
        cy.visit(PAGES.JOIN_BY_INVITATION('some-invitation-token'));
        cy.url().should('include', PAGES.LOGIN);

        cy.contains('a', 'Зарегистрироваться').click();
        cy.registerUser('xuxa', 'Ксюша', '12345678');

        cy.url().should('include', PAGES.JOIN_BY_INVITATION('some-invitation-token'));
    });

    it('Регистрация пользователя с неуникальным логином', () => {
        cy.registerUserAPI('xuxa', 'Ксюша');

        cy.visit(PAGES.REGISTER);
        cy.registerUser('xuxa', 'Ксюша', 'asdfasdf');

        cy.contains('Пользователь с таким логином уже существует').should('be.visible');
    });
});

describe('Вход', () => {
    beforeEach(() => {
        cy.cleanupDatabase();
        cy.registerUserAPI('xuxa', 'Ксюша');
        cy.resetBrowserState();
    });

    it('Вход в систему', () => {
        cy.visit(PAGES.LOGIN);
        cy.loginUser('xuxa', '12345678');

        cy.url().should('include', PAGES.HOME);
        cy.contains('Ксюша').should('be.visible');
    });

    it('Вход с неверным логином', () => {
        cy.visit(PAGES.LOGIN);
        cy.loginUser('wronglogin', '12345678');

        cy.contains('Неверный логин или пароль').should('be.visible');
    });

    it('Вход с неверным паролем', () => {
        cy.visit(PAGES.LOGIN);
        cy.loginUser('xuxa', 'wrongpassword');

        cy.contains('Неверный логин или пароль').should('be.visible');
    });

    it('Редирект на желаемую страницу после входа', () => {
        cy.visit(PAGES.JOIN_BY_INVITATION('some-invitation-token'));
        cy.url().should('include', PAGES.LOGIN);

        cy.loginUser('xuxa', '12345678');

        cy.url().should('include', PAGES.JOIN_BY_INVITATION('some-invitation-token'));
    });
});