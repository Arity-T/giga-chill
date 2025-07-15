import { api } from './api'
import type { UserLoginPassword, User, RegisterRequest } from '@/types/api'

export const authApi = api.injectEndpoints({
    endpoints: (builder) => ({
        login: builder.mutation<void, UserLoginPassword>({
            query: (body) => ({
                url: '/auth/login',
                method: 'POST',
                body,
            }),
            invalidatesTags: ['Me'],
        }),

        register: builder.mutation<void, RegisterRequest>({
            query: (body) => ({
                url: '/auth/register',
                method: 'POST',
                body,
            }),
            invalidatesTags: ['Me'],
        }),

        logout: builder.mutation<void, void>({
            query: () => ({
                url: '/auth/logout',
                method: 'POST',
            }),
            invalidatesTags: ['Me'],
        }),

        getMe: builder.query<User, void>({
            query: () => '/me',
            providesTags: ['Me'],
        }),
    }),
})

export const {
    useLoginMutation,
    useRegisterMutation,
    useLogoutMutation,
    useGetMeQuery,
} = authApi 