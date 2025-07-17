import { api } from './api'
import type {
    ShoppingListWithItems,
    ShoppingListRequest,
    ShoppingItemRequest,
    ShoppingItemPurchasedStateRequest,
    UserId
} from '@/types/api'

export const shoppingApi = api.injectEndpoints({
    endpoints: (builder) => ({
        getShoppingLists: builder.query<ShoppingListWithItems[], string>({
            query: (eventId) => `/events/${eventId}/shopping-lists`,
            providesTags: (_result, _error, eventId) => [
                { type: 'ShoppingLists', id: eventId }
            ],
        }),

        createShoppingList: builder.mutation<void, { eventId: string; shoppingList: ShoppingListRequest }>({
            query: ({ eventId, shoppingList }) => ({
                url: `/events/${eventId}/shopping-lists`,
                method: 'POST',
                body: shoppingList,
            }),
            invalidatesTags: (_result, _error, { eventId }) => [
                { type: 'ShoppingLists', id: eventId }
            ],
        }),

        updateShoppingList: builder.mutation<void, { eventId: string; shoppingListId: string; shoppingList: ShoppingListRequest }>({
            query: ({ eventId, shoppingListId, shoppingList }) => ({
                url: `/events/${eventId}/shopping-lists/${shoppingListId}`,
                method: 'PATCH',
                body: shoppingList,
            }),
            invalidatesTags: (_result, _error, { eventId }) => [
                { type: 'ShoppingLists', id: eventId }
            ],
        }),

        deleteShoppingList: builder.mutation<void, { eventId: string; shoppingListId: string }>({
            query: ({ eventId, shoppingListId }) => ({
                url: `/events/${eventId}/shopping-lists/${shoppingListId}`,
                method: 'DELETE',
            }),
            invalidatesTags: (_result, _error, { eventId }) => [
                { type: 'ShoppingLists', id: eventId }
            ],
        }),

        addShoppingItem: builder.mutation<void, { eventId: string; shoppingListId: string; shoppingItem: ShoppingItemRequest }>({
            query: ({ eventId, shoppingListId, shoppingItem }) => ({
                url: `/events/${eventId}/shopping-lists/${shoppingListId}/shopping-items`,
                method: 'POST',
                body: shoppingItem,
            }),
            invalidatesTags: (_result, _error, { eventId }) => [
                { type: 'ShoppingLists', id: eventId }
            ],
        }),

        updateShoppingItem: builder.mutation<void, { eventId: string; shoppingListId: string; shoppingItemId: string; shoppingItem: ShoppingItemRequest }>({
            query: ({ eventId, shoppingListId, shoppingItemId, shoppingItem }) => ({
                url: `/events/${eventId}/shopping-lists/${shoppingListId}/shopping-items/${shoppingItemId}`,
                method: 'PATCH',
                body: shoppingItem,
            }),
            invalidatesTags: (_result, _error, { eventId }) => [
                { type: 'ShoppingLists', id: eventId }
            ],
        }),

        deleteShoppingItem: builder.mutation<void, { eventId: string; shoppingListId: string; shoppingItemId: string }>({
            query: ({ eventId, shoppingListId, shoppingItemId }) => ({
                url: `/events/${eventId}/shopping-lists/${shoppingListId}/shopping-items/${shoppingItemId}`,
                method: 'DELETE',
            }),
            invalidatesTags: (_result, _error, { eventId }) => [
                { type: 'ShoppingLists', id: eventId }
            ],
        }),

        updateShoppingItemPurchasedState: builder.mutation<void, { eventId: string; shoppingListId: string; shoppingItemId: string; shoppingItem: ShoppingItemPurchasedStateRequest }>({
            query: ({ eventId, shoppingListId, shoppingItemId, shoppingItem }) => ({
                url: `/events/${eventId}/shopping-lists/${shoppingListId}/shopping-items/${shoppingItemId}/purchased-state`,
                method: 'PATCH',
                body: shoppingItem,
            }),
            invalidatesTags: (_result, _error, { eventId }) => [
                { type: 'ShoppingLists', id: eventId }
            ],
        }),

        setShoppingListConsumers: builder.mutation<void, { eventId: string; shoppingListId: string; consumers: UserId[] }>({
            query: ({ eventId, shoppingListId, consumers }) => ({
                url: `/events/${eventId}/shopping-lists/${shoppingListId}/consumers`,
                method: 'PUT',
                body: consumers,
            }),
            invalidatesTags: (_result, _error, { eventId }) => [
                { type: 'ShoppingLists', id: eventId }
            ],
        }),

        setShoppingListBudget: builder.mutation<void, { eventId: string; shoppingListId: string; budget: number }>({
            query: ({ eventId, shoppingListId, budget }) => ({
                url: `/events/${eventId}/shopping-lists/${shoppingListId}/budget`,
                method: 'PUT',
                body: { budget },
            }),
        }),
    }),
})

export const {
    useGetShoppingListsQuery,
    useCreateShoppingListMutation,
    useUpdateShoppingListMutation,
    useDeleteShoppingListMutation,
    useAddShoppingItemMutation,
    useUpdateShoppingItemMutation,
    useDeleteShoppingItemMutation,
    useUpdateShoppingItemPurchasedStateMutation,
    useSetShoppingListConsumersMutation,
    useSetShoppingListBudgetMutation,
} = shoppingApi 