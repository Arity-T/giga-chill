/// <reference types="cypress" />

/**
 * Централизованные типы для команд Cypress
 */

// Типы для команд аутентификации
export interface RegisterUserData {
    name: string;
    username: string;
    password?: string;
}

export interface LoginUserData {
    username: string;
    password?: string;
}

// Типы для команд мероприятий
export interface CreateEventData {
    title: string;
    location: string;
    startDay: string;
    startHour: string;
    endDay: string;
    endHour: string;
    description?: string;
}

// Типы для команд участников
export type ParticipantRole = 'Участник' | 'Администратор';

// Типы для команд списков покупок
export interface ShoppingItemData {
    name: string;
    quantity: string;
    unit: "шт" | "кг" | "г" | "л" | "мл";
}

// Типы для команд задач
export interface CreateTaskData {
    name: string;
    description?: string;
    hour: string;
    assigneeName?: string;
    shoppingLists?: string[];
}

// Типы для команд балансов
export type ParticipantStatus = 'Должник' | 'Кредитор' | 'Равен нулю'; 