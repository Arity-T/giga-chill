import { api } from './api'
import type { Task, TaskRequest, TaskWithShoppingLists } from '@/types/api'

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
                { type: 'Tasks', id: eventId }
            ],
        }),

        getTask: builder.query<TaskWithShoppingLists, { eventId: string; taskId: string }>({
            query: ({ eventId, taskId }) => `/events/${eventId}/tasks/${taskId}`,
            providesTags: (_result, _error, { eventId, taskId }) => [
                { type: 'Tasks', id: `${eventId}-${taskId}` }
            ],
        }),

        updateTask: builder.mutation<void, { eventId: string; taskId: string; task: TaskRequest }>({
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

        deleteTask: builder.mutation<void, { eventId: string; taskId: string }>({
            query: ({ eventId, taskId }) => ({
                url: `/events/${eventId}/tasks/${taskId}`,
                method: 'DELETE',
            }),
            invalidatesTags: (_result, _error, { eventId }) => [
                { type: 'Tasks', id: eventId }
            ],
        }),

        takeTaskInWork: builder.mutation<void, { eventId: string; taskId: string }>({
            query: ({ eventId, taskId }) => ({
                url: `/events/${eventId}/tasks/${taskId}/take-in-work`,
                method: 'POST',
            }),
            invalidatesTags: (_result, _error, { eventId, taskId }) => [
                { type: 'Tasks', id: eventId },
                { type: 'Tasks', id: `${eventId}-${taskId}` }
            ],
        }),
    }),
})

export const {
    useGetTasksQuery,
    useCreateTaskMutation,
    useGetTaskQuery,
    useUpdateTaskMutation,
    useDeleteTaskMutation,
    useTakeTaskInWorkMutation,
} = tasksApi 