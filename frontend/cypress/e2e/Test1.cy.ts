describe('template spec', () => {
//   it('register first person', () => {
//     cy.visit('http://localhost:3001');

//     cy.get('input').should('have.value', '');
//     cy.get('button').should('have.text', 'Войти');

//     cy.contains('Зарегистрироваться!')
//       .should('be.visible')
//       .and('not.be.disabled')
//       .click()

//     cy.get('input#register_name.ant-input')
//       .should('be.visible')
//       .and('have.value', '') 
//       .type('Ксения', { delay: 100 }) 
//       .should('have.value', 'Ксения') 
    
//     cy.get('input[type="text"]').eq(1) 
//       .type('xuxa', { delay: 100 })
//       .should('have.value', 'xuxa')

//     cy.get('input[type="password"]').first()
//       .type('12345678', { delay: 100 })
//       .should('have.value', '12345678')

//     cy.get('input[type="password"]').last()
//       .type('12345678', { delay: 100 })
//       .should('have.value', '12345678')

//      cy.contains('button', 'Зарегистрироваться')
//       .should('be.enabled')
//       .click()
//   })

// it('register second person', () => {
//     cy.visit('http://localhost:3001');

//     cy.get('input').should('have.value', '');
//     cy.get('button').should('have.text', 'Войти');

//     cy.contains('Зарегистрироваться!')
//       .should('be.visible')
//       .and('not.be.disabled')
//       .click()

//     cy.get('input#register_name.ant-input')
//       .should('be.visible')
//       .and('have.value', '') 
//       .type('Дарья', { delay: 100 }) 
//       .should('have.value', 'Дарья') 
    
//     cy.get('input[type="text"]').eq(1)
//       .type('didi', { delay: 100 })
//       .should('have.value', 'didi')

//     cy.get('input[type="password"]').first()
//       .type('12345678', { delay: 100 })
//       .should('have.value', '12345678')

//     cy.get('input[type="password"]').last()
//       .type('12345678', { delay: 100 })
//       .should('have.value', '12345678')

//      cy.contains('button', 'Зарегистрироваться')
//       .should('be.enabled')
//       .click()
//   })

//   it('register third person', () => {
//     cy.visit('http://localhost:3001');

//     cy.get('input').should('have.value', '');
//     cy.get('button').should('have.text', 'Войти');

//     cy.contains('Зарегистрироваться!')
//       .should('be.visible')
//       .and('not.be.disabled')
//       .click()

//     cy.get('input#register_name.ant-input')
//       .should('be.visible')
//       .and('have.value', '') 
//       .type('Юлия', { delay: 100 })
//       .should('have.value', 'Юлия')
    
//     cy.get('input[type="text"]').eq(1) 
//       .type('lili', { delay: 100 })
//       .should('have.value', 'lili')

//     cy.get('input[type="password"]').first()
//       .type('12345678', { delay: 100 })
//       .should('have.value', '12345678')

//     cy.get('input[type="password"]').last()
//       .type('12345678', { delay: 100 })
//       .should('have.value', '12345678')

//      cy.contains('button', 'Зарегистрироваться')
//       .should('be.enabled')
//       .click()
//   })
  
  it('log in', () => {
    cy.visit('http://localhost:3001');

    cy.get('input[type="text"]') 
      .type('lili', { delay: 100 })
      .should('have.value', 'lili')

    cy.get('input[type="password"]')
      .type('12345678', { delay: 100 })
      .should('have.value', '12345678')

    cy.contains('button', 'Войти')
      .should('be.enabled')
      .click()

    cy.visit('http://localhost:3001/events');

    // cy.contains('button', 'Создать')
    //   .should('be.enabled')
    //   .click()

    // cy.get('input[placeholder="Введите название мероприятия"]')
    //   .type('Пикник')
    //   .should('have.value', 'Пикник')

    // cy.get('input[placeholder="Введите адрес или место проведения"]')
    //   .type('Лес')
    //   .should('have.value', 'Лес')

    // cy.get('input[placeholder="Начало"]').click()
    // cy.get('.ant-picker-cell').contains('18').click()

    // cy.get('.ant-picker-time-panel-column')
    // .first() 
    // .contains('03') 
    // .click()

    // cy.contains('ОК').click()
   
    // cy.get('input[placeholder="Окончание"]').click()
    // cy.get('.ant-picker-cell').contains('23').click()

    // cy.get('.ant-picker-time-panel-column')
    // .first() 
    // .contains('20') 
    // .click()

    // cy.contains('ОК').click()

    // cy.get('textarea[placeholder*="описание"]')
    // .type('всем добра')

    // cy.get('button:contains("Создать")').last().click()

    cy.contains('Пикник').click()

    cy.contains('Участники').click()

    // cy.contains('Добавить участника').click()

    // cy.get('input[placeholder="Введите логин пользователя"]')
    //   .type('xuxa')
    //   .should('have.value', 'xuxa')

    // cy.get('button:contains("Добавить участника")').last().click()

    // cy.contains('Добавить участника').click()

    // cy.get('input[placeholder="Введите логин пользователя"]')
    //   .type('didi')
    //   .should('have.value', 'didi')

    // cy.get('button:contains("Добавить участника")').last().click()

    // cy.contains('tr', 'Ксения') // Ищем строку таблицы с текстом "Ксения"
    //    .within(() => {
    //      cy.contains('Участник')
    //        .click()
    //   })

    // cy.contains('Администратор').click()

    cy.contains('Списки покупок').click()

    // cy.contains('Добавить список').click()

    // cy.get('input[placeholder="Введите название списка покупок"]')
    //   .type('Напитки')
    //   .should('have.value', 'Напитки')

    // cy.contains('button', 'Создать')
    //   .should('be.enabled')
    //   .click()

    cy.contains('Напитки').click()

    cy.contains('button', 'Добавить покупку')
      .should('be.enabled')
      .click()

    cy.get('input[placeholder="Введите название товара"]')
      .type('Сок яблочный')
      .should('have.value', 'Сок яблочный')

    cy.get('input[placeholder="1"]').click().clear().type('3')

    cy.contains("шт").click()

    // cy.contains('л').click()
    // Ожидать, пока список станет видимым
    cy.get('.dropdown-menu') // Предполагаемый селектор выпадающего списка
      .should('be.visible');

    // Выбрать "л"
    cy.contains('.dropdown-item', 'л') // Используем более точный селектор
      .click();
    
  })


})