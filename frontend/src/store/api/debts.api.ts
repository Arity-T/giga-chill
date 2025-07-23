import { api } from './api'

api.enhanceEndpoints({
    endpoints: {
        finalizeEvent: {
            invalidatesTags: (_result: any, _error: any, eventId: string) => [
                { type: 'Events', id: eventId },
                { type: 'Participants', id: eventId },
                { type: 'Debts', id: eventId },
            ],
        },
        getMyBalance: {
            providesTags: (_result: any, _error: any, eventId: string) => [
                { type: 'Debts', id: eventId }
            ],
        },
        getBalanceSummary: {
            providesTags: (_result: any, _error: any, eventId: string) => [
                { type: 'Debts', id: eventId }
            ],
        },
    },
})
