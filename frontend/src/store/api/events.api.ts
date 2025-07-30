import { codegenApi } from './codegenApi'

codegenApi.enhanceEndpoints({
    endpoints: {
        getEvents: {
            providesTags: [{ type: 'Events', id: 'LIST' }],
        },
        createEvent: {
            invalidatesTags: [{ type: 'Events', id: 'LIST' }],
        },
        getEvent: {
            providesTags: (_result: any, _error: any, eventId: string) => [{ type: 'Events', id: eventId }],
        },
        updateEvent: {
            invalidatesTags: (_result: any, _error: any, { eventId }: { eventId: string }) => [
                { type: 'Events', id: eventId },
                { type: 'Events', id: 'LIST' }
            ],
        },
        deleteEvent: {
            invalidatesTags: [{ type: 'Events', id: 'LIST' }],
            // Не инвалидируем тег с конкретным eventId, потому что иначе сразу после удаления
            // будет отправляться лишний запрос.
        },
    },
})
