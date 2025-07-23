// Need to use the React-specific entry point to import createApi
import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'

export const api = createApi({
    reducerPath: 'api',
    baseQuery: fetchBaseQuery({
        baseUrl: process.env.NEXT_PUBLIC_API_BASE_URL,
        credentials: 'include',
    }),
    tagTypes: [
        'Me',
        'Events',
        'InvitationTokens',
        'Participants',
        'ShoppingLists',
        'ShoppingListInTask',
        'Tasks',
        'Debts',
    ],
    endpoints: () => ({}),
})