'use client';

import React, { useState } from 'react';
import { Typography, Button, Empty } from 'antd';
import { ShoppingCartOutlined, PlusOutlined } from '@ant-design/icons';
import type { EventIdPathParam } from '@/types/path-params';
import ShoppingListCard from '@/components/shopping-list-card';
import { useGetShoppingListsQuery } from '@/store/api';
import ShoppingListModal from './ShoppingListModal';

const { Title } = Typography;

export default function ShoppingPage({ params }: EventIdPathParam) {
    const { eventId } = React.use(params);
    const [modalOpen, setModalOpen] = useState(false);
    const [expandedListId, setExpandedListId] = useState<string>('');

    const { data: shoppingLists, isLoading } = useGetShoppingListsQuery(eventId);

    const handleCreateList = () => {
        setModalOpen(true);
    };

    const handleCloseModal = () => {
        setModalOpen(false);
    };

    const handleToggleExpand = (listId: string) => {
        setExpandedListId(listId);
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
                        eventId={eventId}
                        canEdit={list.can_edit}
                        canMarkAsPurchased={false}
                        expandedListId={expandedListId}
                        onToggleExpand={handleToggleExpand}
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
                open={modalOpen}
                onCancel={handleCloseModal}
                eventId={eventId}
            />
        </div>
    );
} 