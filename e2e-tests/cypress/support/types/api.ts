/// <reference types="cypress" />

// TODO: Желательно перейти тут на кодогенерацию по спецификации OpenAPI, как уже 
// сделано на фронтенде. Пока можно просто копипастить оттуда сгенерированные типы.
// frontend/src/store/api/codegenApi.ts
// Чтобы файлик появился, надо предварительно выполнить npm run codegen

export enum UserRole {
    Owner = "owner",
    Admin = "admin",
    Participant = "participant",
}

export type Event = {
    event_id: string;
    user_role: UserRole;
    /** Название мероприятия */
    title: string;
    /** Место мероприятия */
    location: string;
    /** Дата и время начала мероприятия */
    start_datetime: string;
    /** Дата и время окончания мероприятия */
    end_datetime: string;
    /** Описание мероприятия */
    description?: string;
    /** Бюджет мероприятия */
    budget?: number;
    /** Флаг, который показывает завершено ли мероприятие или нет. */
    is_finalized: boolean;
};

export type Events = Event[];

export type EventCreate = {
    title: string;
    location: string;
    start_datetime: string;
    end_datetime: string;
    description?: string;
};

export type LoginRequest = {
    login: string;
    /** Пароль пользователя */
    password: string;
};

export type RegisterRequest = {
    login: string;
    password: string;
    name: string;
};

export type Participant = {
    id: string;
    login: string;
    name: string;
    user_role: UserRole;
};

export type Participants = Participant[];

export type ShoppingListCreate = {
    /** Название списка покупок */
    title: string;
    /** Описание списка покупок */
    description?: string;
};