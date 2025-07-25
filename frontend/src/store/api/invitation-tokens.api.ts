import { codegenApi } from './codegenApi'

codegenApi.enhanceEndpoints({
    endpoints: {
        getInvitationToken: {
            providesTags: (_result: any, _error: any, eventId: string) => [
                { type: 'InvitationTokens', id: eventId }
            ],
        },
        createInvitationToken: {
            invalidatesTags: (_result: any, _error: any, eventId: string) => [
                { type: 'InvitationTokens', id: eventId }
            ],
        },
        joinByInvitationToken: {
            invalidatesTags: ['Events'],
        },
    },
})
