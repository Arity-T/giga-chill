import { ShoppingListStatus } from "@/types/api";

export const getStatusColor = (status: string) => {
    switch (status) {
        case ShoppingListStatus.UNASSIGNED:
            return 'default';
        case ShoppingListStatus.ASSIGNED:
            return 'blue';
        case ShoppingListStatus.IN_PROGRESS:
            return 'processing';
        case ShoppingListStatus.BOUGHT:
            return 'success';
        case ShoppingListStatus.PARTIALLY_BOUGHT:
            return 'warning';
        case ShoppingListStatus.CANCELLED:
            return 'error';
        default:
            return 'default';
    }
};

export const getStatusText = (status: string) => {
    switch (status) {
        case ShoppingListStatus.UNASSIGNED:
            return 'Задача не назначена';
        case ShoppingListStatus.ASSIGNED:
            return 'Задача назначена';
        case ShoppingListStatus.IN_PROGRESS:
            return 'В процессе покупки';
        case ShoppingListStatus.BOUGHT:
            return 'Куплен';
        case ShoppingListStatus.PARTIALLY_BOUGHT:
            return 'Частично куплен';
        case ShoppingListStatus.CANCELLED:
            return 'Отменён';
        default:
            return status;
    }
};

export const getStatusTooltip = (status: string) => {
    switch (status) {
        case ShoppingListStatus.UNASSIGNED:
            return 'Список создан и пока не привязан ни к одной задаче. Можно редактировать.';
        case ShoppingListStatus.ASSIGNED:
            return 'Под список заведена задача со статусом «Открыта». Можно редактировать.';
        case ShoppingListStatus.IN_PROGRESS:
            return 'Под список заведена задача в статусе «В работе» или «На проверке».';
        case ShoppingListStatus.BOUGHT:
            return 'Все позиции из списка куплены, задача завершена. Нельзя редактировать.';
        case ShoppingListStatus.PARTIALLY_BOUGHT:
            return 'Задача завершена, но не все позиции куплены. Нельзя редактировать.';
        case ShoppingListStatus.CANCELLED:
            return 'Список удалён или его задача отменена.';
        default:
            return status;
    }
}; 