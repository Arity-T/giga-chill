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

// Нельзя из кодогенерации достать просто URL, только хуки,
// поэтому приходится хардкодить URL до картинки так:
import type { GetReceiptImageProps } from './codegenApi'
export const getReceiptImagePath = (p: GetReceiptImageProps) =>
    `${process.env.NEXT_PUBLIC_API_BASE_URL}/events/${p.eventId}/shopping-lists/${p.shoppingListId}/receipts/${p.receiptId}`;

// Экспорт api нужен только для подключения в store.ts
export { api } from './api' 