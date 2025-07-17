'use client';

import React, { useState } from 'react';
import { Card, App } from 'antd';
import { CaretRightOutlined, ExclamationCircleOutlined } from '@ant-design/icons';
import { ShoppingListWithItems } from '@/types/api';
import {
    useDeleteShoppingListMutation,
    useDeleteShoppingItemMutation,
    useUpdateShoppingItemPurchasedStateMutation,
    useGetShoppingListsQuery
} from '@/store/api';
import ShoppingListHeader from './ShoppingListHeader';
import ShoppingListContent from './ShoppingListContent';
import AddShoppingItemModal from './AddShoppingItemModal';
import EditShoppingItemModal from './EditShoppingItemModal';
import AddConsumersModal from './AddConsumersModal';
import ShoppingListEditModal from './ShoppingListModal';

interface ShoppingListCardProps {
    shoppingList: ShoppingListWithItems;
    eventId: string;
    canEdit: boolean;
    canMarkAsPurchased: boolean;
    expandedListId?: string;
    onToggleExpand?: (listId: string) => void;
}

export default function ShoppingListCard({
    shoppingList,
    eventId,
    canEdit,
    canMarkAsPurchased,
    expandedListId,
    onToggleExpand
}: ShoppingListCardProps) {
    const [activeKey, setActiveKey] = useState<string | string[]>([]);
    const [isHovered, setIsHovered] = useState(false);

    // Определяем, раскрыт ли текущий список
    const isExpanded = expandedListId === shoppingList.shopping_list_id;

    // Состояния модалок
    const [addItemModal, setAddItemModal] = useState(false);
    const [editItemModal, setEditItemModal] = useState<{ open: boolean; itemId: string }>({
        open: false,
        itemId: ''
    });
    const [addConsumersModal, setAddConsumersModal] = useState(false);
    const [editListModal, setEditListModal] = useState(false);

    const { message, modal } = App.useApp();

    // API мутации
    const [deleteShoppingList] = useDeleteShoppingListMutation();
    const [deleteShoppingItem] = useDeleteShoppingItemMutation();
    const [updateShoppingItemPurchasedState] = useUpdateShoppingItemPurchasedStateMutation();

    // Для получения обновленных данных (используется в поиске списка для товара)
    const { data: shoppingLists } = useGetShoppingListsQuery(eventId);

    const purchasedCount = shoppingList.shopping_items.filter(item => item.is_purchased).length;
    const totalCount = shoppingList.shopping_items.length;

    const toggleCollapse = () => {
        if (onToggleExpand) {
            // Используем внешнее управление состоянием аккордеона
            const newExpandedId = isExpanded ? '' : shoppingList.shopping_list_id;
            onToggleExpand(newExpandedId);
        } else {
            // Fallback на локальное состояние для обратной совместимости
            setActiveKey(activeKey.length > 0 ? [] : ['1']);
        }
    };

    const handleToggleItemPurchased = async (itemId: string, isPurchased: boolean) => {
        try {
            await updateShoppingItemPurchasedState({
                eventId,
                shoppingListId: shoppingList.shopping_list_id,
                shoppingItemId: itemId,
                shoppingItem: { is_purchased: isPurchased }
            }).unwrap();

            message.success(
                isPurchased ? 'Товар отмечен как купленный' : 'Товар отмечен как не купленный'
            );
        } catch (error) {
            message.error('Не удалось изменить статус товара');
            console.error('Ошибка при изменении статуса товара:', error);
        }
    };

    const handleEditListInternal = () => {
        setEditListModal(true);
    };

    const handleDeleteList = () => {
        modal.confirm({
            title: 'Удалить список покупок?',
            content: (
                <div>
                    <p>
                        Вы уверены, что хотите удалить список покупок <strong>{shoppingList.title}</strong>?
                    </p>
                    <p>
                        Этот список содержит <strong>{shoppingList.shopping_items.length}</strong> элементов.
                    </p>
                    <p>Это действие нельзя отменить.</p>
                </div>
            ),
            icon: <ExclamationCircleOutlined />,
            okText: 'Удалить',
            okType: 'danger',
            cancelText: 'Отмена',
            onOk: async () => {
                try {
                    await deleteShoppingList({
                        eventId,
                        shoppingListId: shoppingList.shopping_list_id
                    }).unwrap();
                    message.success('Список покупок успешно удален');
                } catch (error) {
                    message.error('Не удалось удалить список покупок');
                    console.error('Ошибка при удалении списка:', error);
                }
            },
        });
    };

    const handleAddConsumers = () => {
        setAddConsumersModal(true);
    };

    const handleAddItem = () => {
        setAddItemModal(true);
    };

    const handleDeleteItem = (itemId: string) => {
        const item = shoppingList.shopping_items.find(item => item.shopping_item_id === itemId);
        if (!item) return;

        modal.confirm({
            title: 'Удалить товар?',
            content: (
                <div>
                    <p>
                        Вы уверены, что хотите удалить товар <strong>{item.title}</strong> ({item.quantity} {item.unit})?
                    </p>
                    <p>Это действие нельзя отменить.</p>
                </div>
            ),
            icon: <ExclamationCircleOutlined />,
            okText: 'Удалить',
            okType: 'danger',
            cancelText: 'Отмена',
            onOk: async () => {
                try {
                    await deleteShoppingItem({
                        eventId,
                        shoppingListId: shoppingList.shopping_list_id,
                        shoppingItemId: itemId
                    }).unwrap();
                    message.success('Товар успешно удален');
                } catch (error) {
                    message.error('Не удалось удалить товар');
                    console.error('Ошибка при удалении товара:', error);
                }
            },
        });
    };

    const handleEditItem = (itemId: string) => {
        setEditItemModal({ open: true, itemId });
    };

    const handleCloseAddItemModal = () => {
        setAddItemModal(false);
    };

    const handleCloseEditItemModal = () => {
        setEditItemModal({ open: false, itemId: '' });
    };

    const handleCloseAddConsumersModal = () => {
        setAddConsumersModal(false);
    };

    const handleCloseEditListModal = () => {
        setEditListModal(false);
    };

    return (
        <>
            <Card
                style={{ marginBottom: '16px' }}
                styles={{ body: { padding: 0 } }}
                onMouseEnter={() => setIsHovered(true)}
                onMouseLeave={() => setIsHovered(false)}
            >
                <div>
                    <div
                        style={{
                            padding: '16px 24px',
                            borderBottom: (onToggleExpand ? isExpanded : activeKey.length > 0) ? '1px solid #f0f0f0' : 'none',
                            cursor: 'pointer'
                        }}
                        onClick={toggleCollapse}
                    >
                        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                            <CaretRightOutlined
                                style={{
                                    transform: (onToggleExpand ? isExpanded : activeKey.length > 0) ? 'rotate(90deg)' : 'rotate(0deg)',
                                    transition: 'transform 0.2s ease',
                                    color: '#8c8c8c'
                                }}
                            />
                            <ShoppingListHeader
                                shoppingList={shoppingList}
                                isHovered={isHovered}
                                purchasedCount={purchasedCount}
                                totalCount={totalCount}
                                onEdit={handleEditListInternal}
                                onDelete={handleDeleteList}
                                onAddConsumers={handleAddConsumers}
                                canEdit={canEdit}
                            />
                        </div>
                    </div>
                    {(onToggleExpand ? isExpanded : activeKey.length > 0) && (
                        <ShoppingListContent
                            description={shoppingList.description}
                            items={shoppingList.shopping_items}
                            onToggleItemPurchased={handleToggleItemPurchased}
                            onAddItem={handleAddItem}
                            onDeleteItem={handleDeleteItem}
                            onEditItem={handleEditItem}
                            canEdit={canEdit}
                            canMarkAsPurchased={canMarkAsPurchased}
                        />
                    )}
                </div>
            </Card>

            {/* Модалки */}
            <AddShoppingItemModal
                open={addItemModal}
                onCancel={handleCloseAddItemModal}
                eventId={eventId}
                shoppingListId={shoppingList.shopping_list_id}
            />

            <EditShoppingItemModal
                open={editItemModal.open}
                onCancel={handleCloseEditItemModal}
                eventId={eventId}
                shoppingListId={shoppingList.shopping_list_id}
                item={shoppingList.shopping_items.find(item => item.shopping_item_id === editItemModal.itemId) || null}
            />

            <AddConsumersModal
                open={addConsumersModal}
                onCancel={handleCloseAddConsumersModal}
                eventId={eventId}
                shoppingListId={shoppingList.shopping_list_id}
                currentConsumers={shoppingList.consumers}
            />

            <ShoppingListEditModal
                open={editListModal}
                onCancel={handleCloseEditListModal}
                eventId={eventId}
                shoppingList={shoppingList}
            />
        </>
    );
} 