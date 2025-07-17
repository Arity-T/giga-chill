import { ShoppingListWithItems, ShoppingListStatus } from '@/types/api';

/**
 * Получает списки покупок доступные для выбора (со статусом UNASSIGNED)
 */
export function getAvailableShoppingLists(allLists: ShoppingListWithItems[]): ShoppingListWithItems[] {
    return allLists.filter(list => list.status === ShoppingListStatus.UNASSIGNED);
}

/**
 * Получает списки покупок для селекта в задаче (уже прикрепленные + доступные)
 * Используется при редактировании задачи
 */
export function getTaskShoppingListsOptions(
    attachedLists: ShoppingListWithItems[],
    allLists: ShoppingListWithItems[]
): ShoppingListWithItems[] {
    const availableLists = getAvailableShoppingLists(allLists);

    // Создаем Map для избежания дублирования
    const optionsMap = new Map<string, ShoppingListWithItems>();

    // Сначала добавляем уже прикрепленные к задаче списки
    attachedLists.forEach(list => {
        optionsMap.set(list.shopping_list_id, list);
    });

    // Затем добавляем доступные для выбора списки
    availableLists.forEach(list => {
        optionsMap.set(list.shopping_list_id, list);
    });

    return Array.from(optionsMap.values());
}

/**
 * Преобразует списки покупок в опции для Ant Design Select
 */
export function shoppingListsToSelectOptions(lists: ShoppingListWithItems[]) {
    return lists.map(list => ({
        label: list.title,
        value: list.shopping_list_id
    }));
} 