import { api } from './api'
import type { Task, TaskRequest, TaskPatchRequest, TaskExecutorId, TaskWithShoppingLists, TaskSendForReviewRequest, TaskReviewRequest } from '@/types/api'

export const tasksApi = api.injectEndpoints({
    endpoints: (builder) => ({
        getTasks: builder.query<Task[], string>({
            query: (eventId) => `/events/${eventId}/tasks`,
            providesTags: (_result, _error, eventId) => [
                { type: 'Tasks', id: eventId }
            ],
        }),

        createTask: builder.mutation<void, { eventId: string; task: TaskRequest }>({
            query: ({ eventId, task }) => ({
                url: `/events/${eventId}/tasks`,
                method: 'POST',
                body: task,
            }),
            invalidatesTags: (_result, _error, { eventId }) => [
                { type: 'Tasks', id: eventId },
                { type: 'ShoppingLists', id: eventId }
            ],
        }),

        getTask: builder.query<TaskWithShoppingLists, { eventId: string; taskId: string }>({
            query: ({ eventId, taskId }) => `/events/${eventId}/tasks/${taskId}`,
            providesTags: (result, _error, { eventId, taskId }) => [
                { type: 'Tasks', id: `${eventId}-${taskId}` },

                // Вместе с задачей возвращаются списки покупок, которые относятся 
                // к этой задаче. Их можно инвалидировать при изменении этих списков,
                // чтобы запрос на получения задачи выполнялся заново.
                ...(result?.shopping_lists ?? []).map((shoppingList) => ({
                    type: 'ShoppingListInTask' as const,
                    id: `${eventId}-${shoppingList.shopping_list_id}`,
                }))
            ],
        }),

        updateTask: builder.mutation<void, { eventId: string; taskId: string; task: TaskPatchRequest }>({
            query: ({ eventId, taskId, task }) => ({
                url: `/events/${eventId}/tasks/${taskId}`,
                method: 'PATCH',
                body: task,
            }),
            invalidatesTags: (_result, _error, { eventId, taskId }) => [
                { type: 'Tasks', id: eventId },
                { type: 'Tasks', id: `${eventId}-${taskId}` }
            ],
        }),

        assignTask: builder.mutation<void, { eventId: string; taskId: string; executorData: TaskExecutorId }>({
            query: ({ eventId, taskId, executorData }) => ({
                url: `/events/${eventId}/tasks/${taskId}/executor`,
                method: 'PUT',
                body: executorData,
            }),
            invalidatesTags: (_result, _error, { eventId, taskId }) => [
                { type: 'Tasks', id: eventId },
                { type: 'Tasks', id: `${eventId}-${taskId}` }
            ],
        }),

        assignShoppingLists: builder.mutation<void, { eventId: string; taskId: string; shoppingListIds: string[] }>({
            query: ({ eventId, taskId, shoppingListIds }) => ({
                url: `/events/${eventId}/tasks/${taskId}/shopping-lists`,
                method: 'PUT',
                body: shoppingListIds,
            }),
            invalidatesTags: (_result, _error, { eventId, taskId }) => [
                { type: 'Tasks', id: eventId },
                { type: 'Tasks', id: `${eventId}-${taskId}` },
                { type: 'ShoppingLists', id: eventId }
            ],
        }),

        deleteTask: builder.mutation<void, { eventId: string; taskId: string }>({
            query: ({ eventId, taskId }) => ({
                url: `/events/${eventId}/tasks/${taskId}`,
                method: 'DELETE',
            }),
            invalidatesTags: (_result, _error, { eventId }) => [
                { type: 'Tasks', id: eventId },
                { type: 'ShoppingLists', id: eventId }
            ],
        }),

        takeTaskInWork: builder.mutation<void, { eventId: string; taskId: string }>({
            query: ({ eventId, taskId }) => ({
                url: `/events/${eventId}/tasks/${taskId}/take-in-work`,
                method: 'POST',
            }),
            invalidatesTags: (_result, _error, { eventId, taskId }) => [
                { type: 'Tasks', id: eventId },
                { type: 'Tasks', id: `${eventId}-${taskId}` },
                { type: 'ShoppingLists', id: eventId }
            ],
        }),

        sendTaskForReview: builder.mutation<void, { eventId: string; taskId: string; reviewData: TaskSendForReviewRequest }>({
            query: ({ eventId, taskId, reviewData }) => ({
                url: `/events/${eventId}/tasks/${taskId}/send-for-review`,
                method: 'POST',
                body: reviewData,
            }),
            invalidatesTags: (_result, _error, { eventId, taskId }) => [
                { type: 'Tasks', id: eventId },
                { type: 'Tasks', id: `${eventId}-${taskId}` }
            ],
        }),

        reviewTask: builder.mutation<void, { eventId: string; taskId: string; reviewData: TaskReviewRequest }>({
            query: ({ eventId, taskId, reviewData }) => ({
                url: `/events/${eventId}/tasks/${taskId}/review`,
                method: 'POST',
                body: reviewData,
            }),
            invalidatesTags: (_result, _error, { eventId, taskId }) => [
                { type: 'Tasks', id: eventId },
                { type: 'Tasks', id: `${eventId}-${taskId}` },
                { type: 'ShoppingLists', id: eventId }
            ],
        }),
    }),
})

export const {
    useGetTasksQuery,
    useCreateTaskMutation,
    useGetTaskQuery,
    useUpdateTaskMutation,
    useAssignTaskMutation,
    useAssignShoppingListsMutation,
    useDeleteTaskMutation,
    useTakeTaskInWorkMutation,
    useSendTaskForReviewMutation,
    useReviewTaskMutation,
} = tasksApi 