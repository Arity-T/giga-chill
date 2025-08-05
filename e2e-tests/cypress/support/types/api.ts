/// <reference types="cypress" />

// TODO: Желательно перейти тут на кодогенерацию по спецификации OpenAPI, как уже 
// сделано на фронтенде. Пока можно просто копипастить оттуда сгенерированные типы.
// frontend/src/store/api/codegenApi.ts
// Чтобы файлик появился, надо предварительно выполнить npm run codegen

export type EventCreateAPI = {
    title: string;
    location: string;
    start_datetime: string;
    end_datetime: string;
    description?: string;
};

export type LoginRequestAPI = {
    login: string;
    /** Пароль пользователя */
    password: string;
};

export type RegisterRequestAPI = {
    login: string;
    password: string;
    name: string;
};