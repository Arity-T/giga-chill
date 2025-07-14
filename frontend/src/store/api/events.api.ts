import { api } from './api'
import type { Event, CreateEventRequest, UpdateEventRequest } from '@/types/api'

export const eventsApi = api.injectEndpoints({
    endpoints: (builder) => ({
        getEvents: builder.query<Event[], void>({
            query: () => '/events',
            providesTags: [{ type: 'Events', id: 'LIST' }],
        }),

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
    useGetEventsQuery,
    useCreateEventMutation,
    useGetEventQuery,
    useDeleteEventMutation,
    useUpdateEventMutation,
} = eventsApi 