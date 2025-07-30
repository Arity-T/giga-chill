// Порядок важен!
// Сначала экспортируем codegenApi, 
// так как в нём определяются эндпоинты (injectEndpoints)
export * from './codegenApi'

// И только потом импортируем остальные файлы, 
// так как в них сгенерированные эндпоинты изменяются (enhanceEndpoints)
import './me.api'
export * from './events.api'
export * from './participants.api'
export * from './shopping.api'
export * from './tasks.api'
export * from './invitation-tokens.api'
export * from './debts.api'

// Экспорт api нужен только для подключения в store.ts
export { api } from './api' 