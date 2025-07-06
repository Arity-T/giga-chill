export const PAGES = {
    HOME: '/',
    LOGIN: '/auth/login',
    REGISTER: '/auth/register',
    EVENTS: '/events',
    EVENT_DETAILS: (eventId: string) => `/events/${eventId}`,
    EVENT_PARTICIPANTS: (eventId: string) => `/events/${eventId}/participants`,
    EVENT_TASKS: (eventId: string) => `/events/${eventId}/tasks`,
    EVENT_BALANCE: (eventId: string) => `/events/${eventId}/balance`,
    EVENT_SHOPPING: (eventId: string) => `/events/${eventId}/shopping`,
    EVENT_SETTINGS: (eventId: string) => `/events/${eventId}/settings`,
}