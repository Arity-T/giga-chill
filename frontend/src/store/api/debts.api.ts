import { api } from './api'
import type { UserBalance, EventBalanceSummary } from '@/types/api'

export const debtsApi = api.injectEndpoints({
    endpoints: (builder) => ({
        finalizeEvent: builder.mutation<void, string>({
            query: (eventId) => ({
                url: `/events/${eventId}/finalize`,
                method: 'POST',
            }),
            invalidatesTags: (_result, _error, eventId) => [
                { type: 'Events', id: eventId },
                { type: 'EventParticipants', id: eventId },
                { type: 'Debts', id: eventId },
            ],
        }),

        getMyBalance: builder.query<UserBalance, string>({
            query: (eventId) => `/events/${eventId}/my-balance`,
            providesTags: (_result, _error, eventId) => [
                { type: 'Debts', id: eventId }
            ],
        }),

        getBalanceSummary: builder.query<EventBalanceSummary[], string>({
            query: (eventId) => `/events/${eventId}/balance-summary`,
            providesTags: (_result, _error, eventId) => [
                { type: 'Debts', id: eventId }
            ],
        }),
    }),
})

export const {
    useFinalizeEventMutation,
    useGetMyBalanceQuery,
    useGetBalanceSummaryQuery,
} = debtsApi
