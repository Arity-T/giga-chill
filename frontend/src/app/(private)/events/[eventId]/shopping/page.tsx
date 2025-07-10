'use client';

import React, { useState } from 'react';
import { Typography, Button, Empty, App } from 'antd';
import { ShoppingCartOutlined, PlusOutlined, ExclamationCircleOutlined } from '@ant-design/icons';
import { EventIdPathParam } from '@/types/path-params';
import ShoppingListCard from './ShoppingListCard';
import { useGetShoppingListsQuery, useDeleteShoppingListMutation } from '@/store/api/api';
import ShoppingListModal from './ShoppingListModal';
import AddShoppingItemModal from './AddShoppingItemModal';
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
    const { message, modal } = App.useApp();

    const { data: shoppingLists, isLoading } = useGetShoppingListsQuery(eventId);
    const [deleteShoppingList] = useDeleteShoppingListMutation();

    const handleToggleItemPurchased = (itemId: string, isPurchased: boolean) => {
        console.log(`Товар ${itemId} отмечен как ${isPurchased ? 'купленный' : 'не купленный'}`);
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
        console.log(`Добавить потребителей к списку ${listId}`);
    };

    const handleAddItem = (listId: string) => {
        setAddItemModal({ open: true, shoppingListId: listId });
    };

    const handleDeleteItem = (itemId: string) => {
        console.log(`Удалить товар ${itemId}`);
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
        </div>
    );
} 