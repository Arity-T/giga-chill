import { codegenApi } from './codegenApi'

codegenApi.enhanceEndpoints({
    endpoints: {
        getShoppingLists: {
            providesTags: (_result: any, _error: any, eventId: string) => [
                { type: 'ShoppingLists', id: eventId }
            ],
        },
        createShoppingList: {
            invalidatesTags: (_result: any, _error: any, { eventId }: { eventId: string }) => [
                { type: 'ShoppingLists', id: eventId }
            ],
        },
        updateShoppingList: {
            invalidatesTags: (_result: any, _error: any, { eventId, shoppingListId }: { eventId: string; shoppingListId: string }) => [
                { type: 'ShoppingLists', id: eventId },
                { type: 'ShoppingListInTask' as const, id: `${eventId}-${shoppingListId}` },
            ],
        },
        deleteShoppingList: {
            invalidatesTags: (_result: any, _error: any, { eventId, shoppingListId }: { eventId: string; shoppingListId: string }) => [
                { type: 'ShoppingLists', id: eventId },
                { type: 'ShoppingListInTask' as const, id: `${eventId}-${shoppingListId}` },
            ],
        },
        setShoppingListBudget: {
            invalidatesTags: (_result: any, _error: any, { eventId, shoppingListId }: { eventId: string; shoppingListId: string }) => [
                { type: 'ShoppingLists', id: eventId },
                { type: 'ShoppingListInTask' as const, id: `${eventId}-${shoppingListId}` },
            ],
        },
        setShoppingListConsumers: {
            invalidatesTags: (_result: any, _error: any, { eventId, shoppingListId }: { eventId: string; shoppingListId: string }) => [
                { type: 'ShoppingLists', id: eventId },
                { type: 'ShoppingListInTask' as const, id: `${eventId}-${shoppingListId}` },
            ],
        },
        createShoppingItem: {
            invalidatesTags: (_result: any, _error: any, { eventId }: { eventId: string }) => [
                { type: 'ShoppingLists', id: eventId }
            ],
        },
        updateShoppingItem: {
            invalidatesTags: (_result: any, _error: any, { eventId, shoppingListId }: { eventId: string; shoppingListId: string }) => [
                { type: 'ShoppingLists', id: eventId },
                { type: 'ShoppingListInTask' as const, id: `${eventId}-${shoppingListId}` },
            ],
        },
        deleteShoppingItem: {
            invalidatesTags: (_result: any, _error: any, { eventId, shoppingListId }: { eventId: string; shoppingListId: string }) => [
                { type: 'ShoppingLists', id: eventId },
                { type: 'ShoppingListInTask' as const, id: `${eventId}-${shoppingListId}` },
            ],
        },
        setShoppingItemPurchased: {
            invalidatesTags: (_result: any, _error: any, { eventId, shoppingListId }: { eventId: string; shoppingListId: string }) => [
                { type: 'ShoppingLists', id: eventId },
                { type: 'ShoppingListInTask' as const, id: `${eventId}-${shoppingListId}` },
            ],
        },
    },
})
