// Need to use the React-specific entry point to import createApi
import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'

export const api = createApi({
    reducerPath: 'api',
    baseQuery: fetchBaseQuery({
        baseUrl: process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:3000',
        credentials: 'include',
    }),
    tagTypes: ['Me', 'Events', 'InvitationTokens', 'EventParticipants', 'ShoppingLists', 'Tasks', 'Debts'],
    endpoints: () => ({}),
})