import { codegenApi } from './codegenApi'
import type { TaskWithShoppingLists, ShoppingListWithItems } from './codegenApi'

codegenApi.enhanceEndpoints({
    endpoints: {
        getTasks: {
            providesTags: (_result: any, _error: any, eventId: string) => [
                { type: 'Tasks', id: eventId }
            ],
        },
        createTask: {
            invalidatesTags: (_result: any, _error: any, { eventId }: { eventId: string }) => [
                { type: 'Tasks', id: eventId },
                { type: 'ShoppingLists', id: eventId }
            ],
        },
        getTask: {
            providesTags: (result: TaskWithShoppingLists | undefined, _error: any, { eventId, taskId }: { eventId: string; taskId: string }) => [
                { type: 'Tasks', id: `${eventId}-${taskId}` },
                ...(result?.shopping_lists ?? []).map((shoppingList: ShoppingListWithItems) => ({
                    type: 'ShoppingListInTask' as const,
                    id: `${eventId}-${shoppingList.shopping_list_id}`,
                }))
            ],
        },
        updateTask: {
            invalidatesTags: (_result: any, _error: any, { eventId, taskId }: { eventId: string; taskId: string }) => [
                { type: 'Tasks', id: eventId },
                { type: 'Tasks', id: `${eventId}-${taskId}` }
            ],
        },
        deleteTask: {
            invalidatesTags: (_result: any, _error: any, { eventId }: { eventId: string }) => [
                { type: 'Tasks', id: eventId },
                { type: 'ShoppingLists', id: eventId }
            ],
        },
        setTaskExecutor: {
            invalidatesTags: (_result: any, _error: any, { eventId, taskId }: { eventId: string; taskId: string }) => [
                { type: 'Tasks', id: eventId },
                { type: 'Tasks', id: `${eventId}-${taskId}` }
            ],
        },
        setTaskShoppingLists: {
            invalidatesTags: (_result: any, _error: any, { eventId, taskId }: { eventId: string; taskId: string }) => [
                { type: 'Tasks', id: eventId },
                { type: 'Tasks', id: `${eventId}-${taskId}` },
                { type: 'ShoppingLists', id: eventId }
            ],
        },
        takeTaskInWork: {
            invalidatesTags: (_result: any, _error: any, { eventId, taskId }: { eventId: string; taskId: string }) => [
                { type: 'Tasks', id: eventId },
                { type: 'Tasks', id: `${eventId}-${taskId}` },
                { type: 'ShoppingLists', id: eventId }
            ],
        },
        sendTaskForReview: {
            invalidatesTags: (_result: any, _error: any, { eventId, taskId }: { eventId: string; taskId: string }) => [
                { type: 'Tasks', id: eventId },
                { type: 'Tasks', id: `${eventId}-${taskId}` }
            ],
        },
        reviewTask: {
            invalidatesTags: (_result: any, _error: any, { eventId, taskId }: { eventId: string; taskId: string }) => [
                { type: 'Tasks', id: eventId },
                { type: 'Tasks', id: `${eventId}-${taskId}` },
                { type: 'ShoppingLists', id: eventId }
            ],
        },
    },
})
