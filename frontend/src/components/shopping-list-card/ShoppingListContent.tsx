import React from 'react';
import { Button, Typography } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { ShoppingItem } from '@/types/api';
import ShoppingListItem from './ShoppingListItem';

const { Text } = Typography;

interface ShoppingListContentProps {
    description?: string;
    items: ShoppingItem[];
    onToggleItemPurchased: (itemId: string, isPurchased: boolean) => void;
    onAddItem: () => void;
    onDeleteItem: (itemId: string) => void;
    onEditItem: (itemId: string) => void;
    canEdit: boolean;
}

export default function ShoppingListContent({
    description,
    items,
    onToggleItemPurchased,
    onAddItem,
    onDeleteItem,
    onEditItem,
    canEdit
}: ShoppingListContentProps) {
    return (
        <div style={{ padding: '16px 24px 16px' }}>
            {description && (
                <Text type="secondary" style={{ display: 'block', marginBottom: '16px' }}>
                    {description}
                </Text>
            )}

            {items.length > 0 ? (
                <>
                    {items.map(item => (
                        <ShoppingListItem
                            key={item.shopping_item_id}
                            item={item}
                            onTogglePurchased={onToggleItemPurchased}
                            onDeleteItem={() => onDeleteItem(item.shopping_item_id)}
                            onEditItem={() => onEditItem(item.shopping_item_id)}
                            canEdit={canEdit}
                        />
                    ))}
                    {canEdit && (
                        <div style={{ marginTop: '16px', textAlign: 'center' }}>
                            <Button
                                type="dashed"
                                icon={<PlusOutlined />}
                                onClick={onAddItem}
                                style={{ width: '100%' }}
                            >
                                Добавить покупку
                            </Button>
                        </div>
                    )}
                </>
            ) : (
                <div style={{ textAlign: 'center', padding: '16px 0' }}>
                    <Text type="secondary" style={{ display: 'block', marginBottom: '16px' }}>
                        Список пуст
                    </Text>
                    {canEdit && (
                        <Button
                            type="primary"
                            icon={<PlusOutlined />}
                            onClick={onAddItem}
                        >
                            Добавить покупку
                        </Button>
                    )}
                </div>
            )}
        </div>
    );
} 