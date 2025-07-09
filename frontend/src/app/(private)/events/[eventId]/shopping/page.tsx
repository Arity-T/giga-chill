'use client';

import React from 'react';
import { Typography, Button, Space, Empty } from 'antd';
import { ShoppingCartOutlined, PlusOutlined } from '@ant-design/icons';
import { EventIdPathParam } from '@/types/path-params';
import { ShoppingListWithItems, ShoppingListStatus, UserRole } from '@/types/api';
import ShoppingListCard from './ShoppingListCard';

const { Title } = Typography;

// Моковые данные для демонстрации верстки
const mockShoppingLists: ShoppingListWithItems[] = [
    {
        shopping_list_id: '1',
        task_id: 'task-1',
        title: 'Продукты для пикника',
        description: 'Всё необходимое для отличного пикника в парке',
        status: ShoppingListStatus.IN_PROGRESS,
        shopping_items: [
            {
                shopping_item_id: '1-1',
                title: 'Хлеб',
                quantity: 2,
                unit: 'булки',
                is_purchased: true
            },
            {
                shopping_item_id: '1-2',
                title: 'Колбаса',
                quantity: 500,
                unit: 'г',
                is_purchased: false
            },
            {
                shopping_item_id: '1-3',
                title: 'Помидоры',
                quantity: 1,
                unit: 'кг',
                is_purchased: false
            },
            {
                shopping_item_id: '1-4',
                title: 'Вода',
                quantity: 6,
                unit: 'л',
                is_purchased: true
            }
        ],
        consumers: [
            {
                id: 'user-1',
                login: 'john_doe',
                name: 'Иван Иванов',
                user_role: UserRole.OWNER,
                balance: 0
            },
            {
                id: 'user-2',
                login: 'jane_smith',
                name: 'Мария Петрова',
                user_role: UserRole.PARTICIPANT,
                balance: 150
            }
        ]
    },
    {
        shopping_list_id: '2',
        task_id: 'task-2',
        title: 'Напитки',
        description: 'Различные напитки для мероприятия',
        status: ShoppingListStatus.BOUGHT,
        shopping_items: [
            {
                shopping_item_id: '2-1',
                title: 'Сок апельсиновый',
                quantity: 2,
                unit: 'л',
                is_purchased: true
            },
            {
                shopping_item_id: '2-2',
                title: 'Газировка',
                quantity: 4,
                unit: 'бутылки',
                is_purchased: true
            }
        ],
        consumers: [
            {
                id: 'user-1',
                login: 'john_doe',
                name: 'Иван Иванов',
                user_role: UserRole.OWNER,
                balance: 0
            }
        ]
    },
    {
        shopping_list_id: '3',
        task_id: 'task-3',
        title: 'Одноразовая посуда',
        description: '',
        status: ShoppingListStatus.UNASSIGNED,
        shopping_items: [
            {
                shopping_item_id: '3-1',
                title: 'Тарелки пластиковые',
                quantity: 20,
                unit: 'шт',
                is_purchased: false
            },
            {
                shopping_item_id: '3-2',
                title: 'Стаканы',
                quantity: 30,
                unit: 'шт',
                is_purchased: false
            },
            {
                shopping_item_id: '3-3',
                title: 'Салфетки',
                quantity: 2,
                unit: 'упаковки',
                is_purchased: false
            }
        ],
        consumers: []
    }
];

export default function ShoppingPage({ params }: EventIdPathParam) {
    const { eventId } = React.use(params);

    // Пока используем моковые данные для демонстрации верстки
    // const { data: shoppingLists, isLoading } = useGetShoppingListsQuery(eventId);
    const shoppingLists = mockShoppingLists;
    const isLoading = false;

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
        console.log('Создать новый список покупок');
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
        </div>
    );
} 