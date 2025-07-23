describe('template spec', () => {
  it('register first person', () => {
    cy.visit('http://localhost:3001');

    cy.get('input').should('have.value', '');
    cy.get('button').should('have.text', 'Войти');

    cy.contains('Зарегистрироваться!')
      .should('be.visible')
      .and('not.be.disabled')
      .click()

    cy.get('input#register_name.ant-input')
      .should('be.visible')
      .and('have.value', '') 
      .type('Ксения', { delay: 100 }) 
      .should('have.value', 'Ксения') 
    
    cy.get('input[type="text"]').eq(1) 
      .type('xuxa', { delay: 100 })
      .should('have.value', 'xuxa')

    cy.get('input[type="password"]').first()
      .type('12345678', { delay: 100 })
      .should('have.value', '12345678')

    cy.get('input[type="password"]').last()
      .type('12345678', { delay: 100 })
      .should('have.value', '12345678')

     cy.contains('button', 'Зарегистрироваться')
      .should('be.enabled')
      .click()
  })

it('register second person', () => {
    cy.visit('http://localhost:3001');

    cy.get('input').should('have.value', '');
    cy.get('button').should('have.text', 'Войти');

    cy.contains('Зарегистрироваться!')
      .should('be.visible')
      .and('not.be.disabled')
      .click()

    cy.get('input#register_name.ant-input')
      .should('be.visible')
      .and('have.value', '') 
      .type('Дарья', { delay: 100 }) 
      .should('have.value', 'Дарья') 
    
    cy.get('input[type="text"]').eq(1)
      .type('didi', { delay: 100 })
      .should('have.value', 'didi')

    cy.get('input[type="password"]').first()
      .type('12345678', { delay: 100 })
      .should('have.value', '12345678')

    cy.get('input[type="password"]').last()
      .type('12345678', { delay: 100 })
      .should('have.value', '12345678')

     cy.contains('button', 'Зарегистрироваться')
      .should('be.enabled')
      .click()
  })

  it('register third person', () => {
    cy.visit('http://localhost:3001');

    cy.get('input').should('have.value', '');
    cy.get('button').should('have.text', 'Войти');

    cy.contains('Зарегистрироваться!')
      .should('be.visible')
      .and('not.be.disabled')
      .click()

    cy.get('input#register_name.ant-input')
      .should('be.visible')
      .and('have.value', '') 
      .type('Юлия', { delay: 100 })
      .should('have.value', 'Юлия')
    
    cy.get('input[type="text"]').eq(1) 
      .type('lili', { delay: 100 })
      .should('have.value', 'lili')

    cy.get('input[type="password"]').first()
      .type('12345678', { delay: 100 })
      .should('have.value', '12345678')

    cy.get('input[type="password"]').last()
      .type('12345678', { delay: 100 })
      .should('have.value', '12345678')

     cy.contains('button', 'Зарегистрироваться')
      .should('be.enabled')
      .click()
  })
  
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

    cy.contains('button', 'Создать')
      .should('be.enabled')
      .click()

    cy.get('input[placeholder="Введите название мероприятия"]')
      .type('Пикник')
      .should('have.value', 'Пикник')

    cy.get('input[placeholder="Введите адрес или место проведения"]')
      .type('Лес')
      .should('have.value', 'Лес')

    cy.get('input[placeholder="Начало"]').click()
    cy.get('.ant-picker-cell').contains('18').click()

    cy.get('.ant-picker-time-panel-column')
    .first() 
    .contains('03') 
    .click()

    cy.contains('ОК').click()
   
    cy.get('input[placeholder="Окончание"]').click()
    cy.get('.ant-picker-cell').contains('23').click()

    cy.get('.ant-picker-time-panel-column')
    .first() 
    .contains('20') 
    .click()

    cy.contains('ОК').click()

    cy.get('textarea[placeholder*="описание"]')
    .type('всем добра')

    cy.get('button:contains("Создать")').last().click()

    cy.wait(2000) // Ждёт 2 секунды
    cy.contains('Пикник').click()

    cy.contains('Участники').click()

    cy.contains('Добавить участника').click()

    cy.get('input[placeholder="Введите логин пользователя"]')
      .type('xuxa')
      .should('have.value', 'xuxa')

    cy.get('button:contains("Добавить участника")').last().click()

    cy.contains('Добавить участника').click()

    cy.get('input[placeholder="Введите логин пользователя"]')
      .type('didi')
      .should('have.value', 'didi')

    cy.get('button:contains("Добавить участника")').last().click()

    cy.contains('tr', 'Ксения') 
       .within(() => {
         cy.contains('Участник')
           .click()
      })

    cy.contains('Администратор').click()

    cy.contains('Списки покупок').click()

    cy.contains('Добавить список').click()

    cy.get('input[placeholder="Введите название списка покупок"]')
      .type('Напитки')
      .should('have.value', 'Напитки')

    cy.contains('button', 'Создать')
      .should('be.enabled')
      .click()

     cy.contains('Напитки').click()

    cy.contains('button', 'Добавить покупку')
      .should('be.enabled')
      .click()

    cy.get('input[placeholder="Введите название товара"]')
      .type('Сок яблочный')
      .should('have.value', 'Сок яблочный')

    cy.get('input[placeholder="1"]').click().clear().type('3')

    cy.contains("шт").click()

    cy.contains('.ant-select-item', 'л').click()

    cy.get('button:contains("Добавить")').last().click()

cy.contains('1').last().click();

cy.contains('Выбрать всех')
      .click()

   
    cy.get('input[type="checkbox"]:checked')
      .should('have.length', 4) 

      cy.get('button:contains("Сохранить")').last().click()


    cy.contains('Задачи').click()

    cy.contains('Создать задачу').click()

    cy.get('input[placeholder="Введите название задачи"]')
      .type('Купить напитки')
      .should('have.value', 'Купить напитки')

    
    cy.get('input[placeholder="Выберите дату и время"]').click()
    
    cy.get('.ant-picker-time-panel-column')
    .first() 
    .contains('03') 
    .click()

    cy.contains('ОК').click()
    
    cy.contains("Выберите исполнителя (необязательно)").click({force: true})
    cy.contains("Ксения (@xuxa)").click()

    cy.contains("Выберите списки покупок (необязательно)").click({force: true})
    cy.contains("Напитки").click()
    cy.contains("Напитки").click()

    cy.get('button:contains("Создать")').last().click()
  })


  it('log in xuxa', () => {
    cy.visit('http://localhost:3001');

    cy.get('input[type="text"]') 
      .type('xuxa', { delay: 100 })
      .should('have.value', 'xuxa')

    cy.get('input[type="password"]')
      .type('12345678', { delay: 100 })
      .should('have.value', '12345678')

    cy.contains('button', 'Войти')
      .should('be.enabled')
      .click()

    cy.visit('http://localhost:3001/events');
    cy.wait(4000) // Ждёт 2 секунды
    cy.contains('Пикник').click()

    cy.contains('Задачи').click()

    cy.contains('Купить напитки').click()

    cy.contains('Взять в работу').click()

    cy.contains('Напитки').click()

    cy.contains('Сок яблочный')
    cy.get('input[type="checkbox"]').click();

    cy.get('input[placeholder="Бюджет"]')
       .type('46')
    cy.get('button').eq(7).click();

    cy.get('textarea[placeholder*="Опишите выполненную работу, результаты и другие важные детали..."]')
    .type('купила')

    cy.contains('button', 'На проверку').click()
  })


  it('end first party', () => {
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


    cy.wait(4000) // Ждёт 2 секунды
    cy.contains('Пикник').click()

    cy.contains('Задачи').click()

    cy.contains('Купить напитки').click()

    cy.get('input[value="46.00"]').click().clear().type("100")
    cy.get('button').eq(7).click();
    
    cy.get('textarea[placeholder*="Добавьте комментарий к проверке..."]')
    .type('молодец')

    cy.contains('button', 'Подтвердить выполнение').click()

    cy.get('button').eq(3).click();

    cy.contains('Общие расчёты').click()
    cy.wait(4000)
    cy.contains('Завершить мероприятие').click()

    cy.contains('Да, завершить мероприятие').click()

    cy.contains('td', 'Юлия (@lili)')
      .parent() // Перейти к родительской строке <tr>
      .within(() => {
        // Проверить баланс
        cy.get('td').eq(2).should('contain', '-33,33');
        // Проверить статус
        cy.get('td').eq(3).should('contain', 'Должник');
      });

    cy.contains('td', 'Ксения (@xuxa)')
      .parent()
      .within(() => {
        cy.get('td').eq(2).should('contain', '66,66');
        cy.get('td').eq(3).should('contain', 'Кредитор');
      });

    cy.contains('td', 'Дарья (@didi)')
      .parent()
      .within(() => {
        cy.get('td').eq(2).should('contain', '-33,33');
        cy.get('td').eq(3).should('contain', 'Должник');
      });
  })
})