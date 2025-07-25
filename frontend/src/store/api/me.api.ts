import { codegenApi } from './codegenApi'

codegenApi.enhanceEndpoints({
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