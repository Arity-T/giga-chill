import { codegenApi } from './codegenApi'

codegenApi.enhanceEndpoints({
    endpoints: {
        getParticipants: {
            providesTags: (_result: any, _error: any, eventId: string) => [
                { type: 'Participants', id: eventId }
            ],
        },
        deleteParticipant: {
            invalidatesTags: (_result: any, _error: any, { eventId }: { eventId: string }) => [
                { type: 'Participants', id: eventId }
            ],
        },
        setParticipantRole: {
            invalidatesTags: (_result: any, _error: any, { eventId }: { eventId: string }) => [
                { type: 'Participants', id: eventId }
            ],
        },
        addParticipant: {
            invalidatesTags: (_result: any, _error: any, { eventId }: { eventId: string }) => [
                { type: 'Participants', id: eventId }
            ],
        },
    },
})
