import React, { useState } from 'react';
import { Card } from 'antd';
import { CaretRightOutlined } from '@ant-design/icons';
import { ShoppingListWithItems } from '@/types/api';
import ShoppingListHeader from './ShoppingListHeader';
import ShoppingListContent from './ShoppingListContent';

interface ShoppingListCardProps {
    shoppingList: ShoppingListWithItems;
    onToggleItemPurchased: (itemId: string, isPurchased: boolean) => void;
    onDeleteList: (listId: string) => void;
    onEditList: (listId: string) => void;
    onAddConsumers: (listId: string) => void;
    onAddItem: (listId: string) => void;
    onDeleteItem: (itemId: string) => void;
}

export default function ShoppingListCard({
    shoppingList,
    onToggleItemPurchased,
    onDeleteList,
    onEditList,
    onAddConsumers,
    onAddItem,
    onDeleteItem
}: ShoppingListCardProps) {
    const [activeKey, setActiveKey] = useState<string | string[]>([]);
    const [isHovered, setIsHovered] = useState(false);

    const purchasedCount = shoppingList.shopping_items.filter(item => item.is_purchased).length;
    const totalCount = shoppingList.shopping_items.length;

    const toggleCollapse = () => {
        setActiveKey(activeKey.length > 0 ? [] : ['1']);
    };

    return (
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
                        borderBottom: activeKey.length > 0 ? '1px solid #f0f0f0' : 'none',
                        cursor: 'pointer'
                    }}
                    onClick={toggleCollapse}
                >
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                        <CaretRightOutlined
                            style={{
                                transform: activeKey.length > 0 ? 'rotate(90deg)' : 'rotate(0deg)',
                                transition: 'transform 0.2s ease',
                                color: '#8c8c8c'
                            }}
                        />
                        <ShoppingListHeader
                            shoppingList={shoppingList}
                            isHovered={isHovered}
                            purchasedCount={purchasedCount}
                            totalCount={totalCount}
                            onEdit={() => onEditList(shoppingList.shopping_list_id)}
                            onDelete={() => onDeleteList(shoppingList.shopping_list_id)}
                            onAddConsumers={() => onAddConsumers(shoppingList.shopping_list_id)}
                        />
                    </div>
                </div>
                {activeKey.length > 0 && (
                    <ShoppingListContent
                        description={shoppingList.description}
                        items={shoppingList.shopping_items}
                        onToggleItemPurchased={onToggleItemPurchased}
                        onAddItem={() => onAddItem(shoppingList.shopping_list_id)}
                        onDeleteItem={onDeleteItem}
                    />
                )}
            </div>
        </Card>
    );
} 