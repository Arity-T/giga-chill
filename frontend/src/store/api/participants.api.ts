import { api } from './api'
import type { UserInEvent, UserRole } from '@/types/api'

export const participantsApi = api.injectEndpoints({
    endpoints: (builder) => ({
        getEventParticipants: builder.query<UserInEvent[], string>({
            query: (eventId) => `/events/${eventId}/participants`,
            providesTags: (_result, _error, eventId) => [
                { type: 'EventParticipants', id: eventId }
            ],
        }),

        deleteParticipant: builder.mutation<void, { eventId: string; participantId: string }>({
            query: ({ eventId, participantId }) => ({
                url: `/events/${eventId}/participants/${participantId}`,
                method: 'DELETE',
            }),
            invalidatesTags: (_result, _error, { eventId }) => [
                { type: 'EventParticipants', id: eventId }
            ],
        }),

        updateParticipantRole: builder.mutation<void, { eventId: string; participantId: string; role: UserRole }>({
            query: ({ eventId, participantId, role }) => ({
                url: `/events/${eventId}/participants/${participantId}/role`,
                method: 'PATCH',
                body: { role },
            }),
            invalidatesTags: (_result, _error, { eventId }) => [
                { type: 'EventParticipants', id: eventId }
            ],
        }),

        addParticipant: builder.mutation<void, { eventId: string; login: string }>({
            query: ({ eventId, login }) => ({
                url: `/events/${eventId}/participants`,
                method: 'POST',
                body: { login },
            }),
            invalidatesTags: (_result, _error, { eventId }) => [
                { type: 'EventParticipants', id: eventId }
            ],
        }),
    }),
})

export const {
    useGetEventParticipantsQuery,
    useDeleteParticipantMutation,
    useUpdateParticipantRoleMutation,
    useAddParticipantMutation,
} = participantsApi 