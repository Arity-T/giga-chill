// Need to use the React-specific entry point to import createApi
import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'
import type { UserLoginPassword, User, RegisterRequest, Event, CreateEventRequest, UpdateEventRequest } from '@/types/api'

export const api = createApi({
  reducerPath: 'api',
  baseQuery: fetchBaseQuery({
    baseUrl: 'http://localhost:3000',
    credentials: 'include',
  }),
  tagTypes: ['Me', 'Events'],
  endpoints: (builder) => ({
    login: builder.mutation<void, UserLoginPassword>({
      query: (body) => ({
        url: '/auth/login',
        method: 'POST',
        body,
      }),
      invalidatesTags: ['Me'],
    }),

    register: builder.mutation<void, RegisterRequest>({
      query: (body) => ({
        url: '/auth/register',
        method: 'POST',
        body,
      }),
      invalidatesTags: ['Me'],
    }),

    logout: builder.mutation<void, void>({
      query: () => ({
        url: '/auth/logout',
        method: 'POST',
      }),
      invalidatesTags: ['Me'],
    }),

    getMe: builder.query<User, void>({
      query: () => '/me',
      providesTags: ['Me'],
    }),

    getEvents: builder.query<Event[], void>({
      query: () => '/events',
      providesTags: [{ type: 'Events', id: 'LIST' }],
    }),

    createEvent: builder.mutation<Event, CreateEventRequest>({
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

    updateEvent: builder.mutation<Event, { eventId: string; event: UpdateEventRequest }>({
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
});

export const {
  useLoginMutation,
  useRegisterMutation,
  useLogoutMutation,
  useGetMeQuery,
  useGetEventsQuery,
  useCreateEventMutation,
  useGetEventQuery,
  useDeleteEventMutation,
  useUpdateEventMutation,
} = api;