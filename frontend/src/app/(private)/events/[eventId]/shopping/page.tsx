'use client';

import React, { useState } from 'react';
import { Typography, Button, Empty, App } from 'antd';
import { ShoppingCartOutlined, PlusOutlined, ExclamationCircleOutlined } from '@ant-design/icons';
import { EventIdPathParam } from '@/types/path-params';
import ShoppingListCard from './ShoppingListCard';
import { useGetShoppingListsQuery, useDeleteShoppingListMutation, useDeleteShoppingItemMutation, useUpdateShoppingItemPurchasedStateMutation } from '@/store/api/api';
import ShoppingListModal from './ShoppingListModal';
import AddShoppingItemModal from './AddShoppingItemModal';
import EditShoppingItemModal from './EditShoppingItemModal';
import AddConsumersModal from './AddConsumersModal';
import { ShoppingListWithItems } from '@/types/api';

const { Title } = Typography;

type ModalState =
    | { type: 'closed' }
    | { type: 'create' }
    | { type: 'edit'; shoppingList: ShoppingListWithItems };

export default function ShoppingPage({ params }: EventIdPathParam) {
    const { eventId } = React.use(params);
    const [modalState, setModalState] = useState<ModalState>({ type: 'closed' });
    const [addItemModal, setAddItemModal] = useState<{ open: boolean; shoppingListId: string }>({
        open: false,
        shoppingListId: ''
    });
    const [editItemModal, setEditItemModal] = useState<{ open: boolean; shoppingListId: string; itemId: string }>({
        open: false,
        shoppingListId: '',
        itemId: ''
    });
    const [addConsumersModal, setAddConsumersModal] = useState<{ open: boolean; shoppingList: ShoppingListWithItems | null }>({
        open: false,
        shoppingList: null
    });
    const { message, modal } = App.useApp();

    const { data: shoppingLists, isLoading } = useGetShoppingListsQuery(eventId);
    const [deleteShoppingList] = useDeleteShoppingListMutation();
    const [deleteShoppingItem] = useDeleteShoppingItemMutation();
    const [updateShoppingItemPurchasedState] = useUpdateShoppingItemPurchasedStateMutation();

    const handleToggleItemPurchased = async (itemId: string, isPurchased: boolean) => {
        // Находим список покупок, содержащий этот товар
        const shoppingList = shoppingLists?.find(list =>
            list.shopping_items.some(item => item.shopping_item_id === itemId)
        );

        if (!shoppingList) {
            message.error('Не удалось найти список покупок для этого товара');
            return;
        }

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

    const handleEditList = (listId: string) => {
        const shoppingList = shoppingLists?.find(list => list.shopping_list_id === listId);
        if (shoppingList) {
            setModalState({ type: 'edit', shoppingList });
        }
    };

    const handleDeleteList = (listId: string) => {
        const shoppingList = shoppingLists?.find(list => list.shopping_list_id === listId);
        if (!shoppingList) return;

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
                    await deleteShoppingList({ eventId, shoppingListId: listId }).unwrap();
                    message.success('Список покупок успешно удален');
                } catch (error) {
                    message.error('Не удалось удалить список покупок');
                    console.error('Ошибка при удалении списка:', error);
                }
            },
        });
    };

    const handleAddConsumers = (listId: string) => {
        const shoppingList = shoppingLists?.find(list => list.shopping_list_id === listId);
        if (shoppingList) {
            setAddConsumersModal({ open: true, shoppingList });
        }
    };

    const handleAddItem = (listId: string) => {
        setAddItemModal({ open: true, shoppingListId: listId });
    };

    const handleDeleteItem = (itemId: string, shoppingListId: string) => {
        // Найдем товар для отображения в подтверждении
        const shoppingList = shoppingLists?.find(list => list.shopping_list_id === shoppingListId);
        const item = shoppingList?.shopping_items.find(item => item.shopping_item_id === itemId);

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
                        shoppingListId,
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

    const handleCreateList = () => {
        setModalState({ type: 'create' });
    };

    const handleCloseModal = () => {
        setModalState({ type: 'closed' });
    };

    const handleCloseAddItemModal = () => {
        setAddItemModal({ open: false, shoppingListId: '' });
    };

    const handleCloseAddConsumersModal = () => {
        setAddConsumersModal({ open: false, shoppingList: null });
    };

    const handleEditItem = (itemId: string, shoppingListId: string) => {
        setEditItemModal({ open: true, shoppingListId, itemId });
    };

    const handleCloseEditItemModal = () => {
        setEditItemModal({ open: false, shoppingListId: '', itemId: '' });
    };

    if (isLoading) {
        return <div>Загрузка...</div>;
    };

    return (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px', width: '100%' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
                <Title level={3} style={{ margin: 0 }}>
                    <ShoppingCartOutlined style={{ marginRight: '8px' }} />
                    Списки покупок
                </Title>
                <Button type="primary" icon={<PlusOutlined />} onClick={handleCreateList}>
                    Добавить список
                </Button>
            </div>

            {shoppingLists && shoppingLists.length > 0 ? (
                shoppingLists.map(list => (
                    <ShoppingListCard
                        key={list.shopping_list_id}
                        shoppingList={list}
                        onToggleItemPurchased={handleToggleItemPurchased}
                        onDeleteList={handleDeleteList}
                        onEditList={handleEditList}
                        onAddConsumers={handleAddConsumers}
                        onAddItem={handleAddItem}
                        onDeleteItem={handleDeleteItem}
                        onEditItem={handleEditItem}
                    />
                ))
            ) : (
                <Empty
                    description="Нет списков покупок"
                    image={Empty.PRESENTED_IMAGE_SIMPLE}
                >
                </Empty>
            )}

            <ShoppingListModal
                open={modalState.type !== 'closed'}
                onCancel={handleCloseModal}
                eventId={eventId}
                shoppingList={modalState.type === 'edit' ? modalState.shoppingList : undefined}
            />

            <AddShoppingItemModal
                open={addItemModal.open}
                onCancel={handleCloseAddItemModal}
                eventId={eventId}
                shoppingListId={addItemModal.shoppingListId}
            />

            <AddConsumersModal
                open={addConsumersModal.open}
                onCancel={handleCloseAddConsumersModal}
                eventId={eventId}
                shoppingListId={addConsumersModal.shoppingList?.shopping_list_id || ''}
                currentConsumers={addConsumersModal.shoppingList?.consumers || []}
            />

            <EditShoppingItemModal
                open={editItemModal.open}
                onCancel={handleCloseEditItemModal}
                eventId={eventId}
                shoppingListId={editItemModal.shoppingListId}
                item={shoppingLists
                    ?.find(list => list.shopping_list_id === editItemModal.shoppingListId)
                    ?.shopping_items.find(item => item.shopping_item_id === editItemModal.itemId) || null
                }
            />
        </div>
    );
} 