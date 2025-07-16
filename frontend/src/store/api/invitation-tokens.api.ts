import { api } from './api'
import type { InvitationToken } from '@/types/api'

export const invitationTokensApi = api.injectEndpoints({
    endpoints: (builder) => ({
        getEventInvitationToken: builder.query<InvitationToken, string>({
            query: (eventId) => `/events/${eventId}/invitation-token`,
            providesTags: (_result, _error, eventId) => [
                { type: 'InvitationTokens', id: eventId }
            ],
        }),

        createEventInvitationToken: builder.mutation<InvitationToken, string>({
            query: (eventId) => ({
                url: `/events/${eventId}/invitation-token`,
                method: 'POST',
            }),
            invalidatesTags: (_result, _error, eventId) => [
                { type: 'InvitationTokens', id: eventId }
            ],
        }),

        joinByInvitationToken: builder.mutation<{ event_id: string }, string>({
            query: (invitationToken) => ({
                url: `/events/join-by-invitation-token`,
                method: 'POST',
                body: { invitation_token: invitationToken },
            }),
            invalidatesTags: ['Events'],
        }),
    }),
})

export const {
    useGetEventInvitationTokenQuery,
    useCreateEventInvitationTokenMutation,
    useJoinByInvitationTokenMutation,
} = invitationTokensApi
