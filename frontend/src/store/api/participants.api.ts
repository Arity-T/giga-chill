import { api } from './api'

api.enhanceEndpoints({
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
        updateParticipantRole: {
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
