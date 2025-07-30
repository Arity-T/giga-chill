// Порядок важен!
// Сначала экспортируем codegenApi, 
// так как в нём определяются эндпоинты (injectEndpoints)
export * from './codegenApi'

// И только потом импортируем остальные файлы, 
// так как в них сгенерированные эндпоинты изменяются (enhanceEndpoints)
import './me.api'
import './events.api'
import './participants.api'
import './shopping.api'
import './tasks.api'
import './invitation-tokens.api'
import './debts.api'

// Экспорт api нужен только для подключения в store.ts
export { api } from './api' 