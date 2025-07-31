/// <reference types="cypress" />

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