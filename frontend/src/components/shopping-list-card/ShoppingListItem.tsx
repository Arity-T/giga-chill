import React, { useState } from 'react';
import { Card, Checkbox, Typography, Space, Button } from 'antd';
import { DeleteOutlined, EditOutlined } from '@ant-design/icons';
import type { ShoppingItem } from '@/store/api';

const { Text } = Typography;

interface ShoppingListItemProps {
    item: ShoppingItem;
    onTogglePurchased: (itemId: string, isPurchased: boolean) => void;
    onDeleteItem: (itemId: string) => void;
    onEditItem: (itemId: string) => void;
    canEdit: boolean;
    canMarkAsPurchased: boolean;
}

export default function ShoppingListItem({ item, onTogglePurchased, onDeleteItem, onEditItem, canEdit, canMarkAsPurchased }: ShoppingListItemProps) {
    const [isHovered, setIsHovered] = useState(false);

    return (
        <Card
            size="small"
            style={{
                marginBottom: '8px',
                opacity: item.is_purchased ? 0.7 : 1,
                transition: 'opacity 0.3s ease'
            }}
            styles={{ body: { padding: '12px' } }}
            onMouseEnter={() => setIsHovered(true)}
            onMouseLeave={() => setIsHovered(false)}
        >
            <Space align="center" style={{ width: '100%', justifyContent: 'space-between' }}>
                <Space align="center">
                    <Checkbox
                        checked={item.is_purchased}
                        onChange={(e) => onTogglePurchased(item.shopping_item_id, e.target.checked)}
                        disabled={!canMarkAsPurchased}
                        style={{
                            ...((!canMarkAsPurchased && item.is_purchased) && {
                                // Серый цвет для отмеченного чекбокса когда нельзя редактировать
                                opacity: 0.6,
                            })
                        }}
                    />
                    <Text
                        style={{
                            textDecoration: item.is_purchased ? 'line-through' : 'none',
                            fontSize: '16px'
                        }}
                    >
                        {item.title}
                    </Text>
                    <Text style={{ fontSize: '14px', color: '#000', fontWeight: 500 }}>
                        {item.quantity} {item.unit}
                    </Text>
                </Space>
                {canEdit && (
                    <Space>
                        <Button
                            type="text"
                            icon={<EditOutlined style={{ color: '#8c8c8c' }} />}
                            size="small"
                            style={{
                                opacity: isHovered ? 1 : 0,
                                transition: 'opacity 0.2s ease'
                            }}
                            onClick={() => onEditItem(item.shopping_item_id)}
                        />
                        <Button
                            type="text"
                            icon={<DeleteOutlined style={{ color: '#8c8c8c' }} />}
                            size="small"
                            style={{
                                opacity: isHovered ? 1 : 0,
                                transition: 'opacity 0.2s ease'
                            }}
                            onClick={() => onDeleteItem(item.shopping_item_id)}
                        />
                    </Space>
                )}
            </Space>
        </Card>
    );
} 