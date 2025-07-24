import { ShoppingListStatus } from "@/store/api";

export const getStatusColor = (status: string) => {
    switch (status) {
        case ShoppingListStatus.Unassigned:
            return 'default';
        case ShoppingListStatus.Assigned:
            return 'blue';
        case ShoppingListStatus.InProgress:
            return 'processing';
        case ShoppingListStatus.Bought:
            return 'success';
        case ShoppingListStatus.PartiallyBought:
            return 'warning';
        case ShoppingListStatus.Cancelled:
            return 'error';
        default:
            return 'default';
    }
};

export const getStatusText = (status: string) => {
    switch (status) {
        case ShoppingListStatus.Unassigned:
            return 'Задача не назначена';
        case ShoppingListStatus.Assigned:
            return 'Задача назначена';
        case ShoppingListStatus.InProgress:
            return 'В процессе покупки';
        case ShoppingListStatus.Bought:
            return 'Куплен';
        case ShoppingListStatus.PartiallyBought:
            return 'Частично куплен';
        case ShoppingListStatus.Cancelled:
            return 'Отменён';
        default:
            return status;
    }
};

export const getStatusTooltip = (status: string) => {
    switch (status) {
        case ShoppingListStatus.Unassigned:
            return 'Список создан и пока не привязан ни к одной задаче. Можно редактировать.';
        case ShoppingListStatus.Assigned:
            return 'Под список заведена задача со статусом «Открыта». Можно редактировать.';
        case ShoppingListStatus.InProgress:
            return 'Под список заведена задача в статусе «В работе» или «На проверке».';
        case ShoppingListStatus.Bought:
            return 'Все позиции из списка куплены, задача завершена. Нельзя редактировать.';
        case ShoppingListStatus.PartiallyBought:
            return 'Задача завершена, но не все позиции куплены. Нельзя редактировать.';
        case ShoppingListStatus.Cancelled:
            return 'Список удалён или его задача отменена.';
        default:
            return status;
    }
}; 