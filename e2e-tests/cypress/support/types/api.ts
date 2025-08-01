/// <reference types="cypress" />

// TODO: Желательно перейти тут на кодогенерацию по спецификации OpenAPI, как уже 
// сделано на фронтенде. Пока можно просто копипастить оттуда сгенерированные типы.
// frontend/src/store/api/codegenApi.ts
// Чтобы файлик появился, надо предварительно выполнить npm run codegen

// Типы для создания мероприятия
export interface CreateEventAPIData {
    title: string;
    location: string;
    description?: string;
    startDay: string; // например "15"
    startHour: string; // например "10"
    endDay: string;
    endHour: string;
}