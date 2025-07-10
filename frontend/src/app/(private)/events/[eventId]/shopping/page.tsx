'use client';

import React, { useState } from 'react';
import { Typography, Button, Space, Empty } from 'antd';
import { ShoppingCartOutlined, PlusOutlined } from '@ant-design/icons';
import { EventIdPathParam } from '@/types/path-params';
import ShoppingListCard from './ShoppingListCard';
import { useGetShoppingListsQuery } from '@/store/api/api';
import CreateShoppingListModal from './CreateShoppingListModal';

const { Title } = Typography;

export default function ShoppingPage({ params }: EventIdPathParam) {
    const { eventId } = React.use(params);
    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);

    // Пока используем моковые данные для демонстрации верстки
    const { data: shoppingLists, isLoading } = useGetShoppingListsQuery(eventId);

    const handleToggleItemPurchased = (itemId: string, isPurchased: boolean) => {
        console.log(`Товар ${itemId} отмечен как ${isPurchased ? 'купленный' : 'не купленный'}`);
    };

    const handleEditList = (listId: string) => {
        console.log(`Редактировать список ${listId}`);
    };

    const handleDeleteList = (listId: string) => {
        console.log(`Удалить список ${listId}`);
    };

    const handleAddConsumers = (listId: string) => {
        console.log(`Добавить потребителей к списку ${listId}`);
    };

    const handleAddItem = (listId: string) => {
        console.log(`Добавить товар в список ${listId}`);
    };

    const handleDeleteItem = (itemId: string) => {
        console.log(`Удалить товар ${itemId}`);
    };

    const handleCreateList = () => {
        setIsCreateModalOpen(true);
    };

    if (isLoading) {
        return <div>Загрузка...</div>;
    }

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

            <CreateShoppingListModal
                open={isCreateModalOpen}
                onCancel={() => setIsCreateModalOpen(false)}
                eventId={eventId}
            />
        </div>
    );
} 