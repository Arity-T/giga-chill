import { api } from './api'

api.enhanceEndpoints({
    endpoints: {
        getMe: {
            providesTags: ['Me'],
        },
        login: {
            invalidatesTags: ['Me'],
        },
        register: {
            invalidatesTags: ['Me'],
        },
        logout: {
            invalidatesTags: ['Me'],
        },
    },
})