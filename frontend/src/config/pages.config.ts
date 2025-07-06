export const PAGES = {
    HOME: '/',
    LOGIN: '/auth/login',
    REGISTER: '/auth/register',
    EVENTS: '/events',
    EVENT_DETAILS: (eventId: string) => `/events/${eventId}`,
}