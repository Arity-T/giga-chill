import { api } from './api'

api.enhanceEndpoints({
    endpoints: {
        getEvents: {
            providesTags: [{ type: 'Events', id: 'LIST' }],
        },
        // createEvent: {
        //     invalidatesTags: [{ type: 'Events', id: 'LIST' }],
        // },
        // getEvent: {
        //     providesTags: (_result, _error, eventId: string) => [{ type: 'Events', id: eventId }],
        // },
        // updateEvent: {
        //     invalidatesTags: (_result, _error, { eventId }: { eventId: string }) => [
        //         { type: 'Events', id: eventId },
        //         { type: 'Events', id: 'LIST' }
        //     ],
        // },
        // deleteEvent: {
        //     invalidatesTags: (_result, _error, eventId) => [
        //         { type: 'Events', id: eventId },
        //         { type: 'Events', id: 'LIST' }
        //     ],
        // },
    },
})


import type { Event, CreateEventRequest, UpdateEventRequest } from '@/types/api'

export const eventsApi = api.injectEndpoints({
    endpoints: (builder) => ({
        createEvent: builder.mutation<void, CreateEventRequest>({
            query: (body) => ({
                url: '/events',
                method: 'POST',
                body,
            }),
            invalidatesTags: [{ type: 'Events', id: 'LIST' }],
        }),

        getEvent: builder.query<Event, string>({
            query: (eventId) => `/events/${eventId}`,
            providesTags: (_result, _error, eventId) => [{ type: 'Events', id: eventId }],
        }),

        deleteEvent: builder.mutation<void, string>({
            query: (eventId) => ({
                url: `/events/${eventId}`,
                method: 'DELETE',
            }),
            invalidatesTags: [{ type: 'Events', id: 'LIST' }],
            // Не ивалидируем тег с конкретным eventId, потому что иначе сразу после удаления
            // будет отправляться лишний запрос.
        }),

        updateEvent: builder.mutation<void, { eventId: string; event: UpdateEventRequest }>({
            query: ({ eventId, event }) => ({
                url: `/events/${eventId}`,
                method: 'PATCH',
                body: event,
            }),
            invalidatesTags: (_result, _error, { eventId }) => [
                { type: 'Events', id: eventId },
                { type: 'Events', id: 'LIST' }
            ],
        }),
    }),
})

export const {
    useCreateEventMutation,
    useGetEventQuery,
    useDeleteEventMutation,
    useUpdateEventMutation,
} = eventsApi 