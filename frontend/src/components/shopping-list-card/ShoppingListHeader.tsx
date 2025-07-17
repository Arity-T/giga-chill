import React from 'react';
import { Space, Typography, Tag, Tooltip } from 'antd';
import { ShoppingListWithItems } from '@/types/api';
import { getStatusColor, getStatusText, getStatusTooltip } from '@/utils/shopping-status-utils';
import ConsumerButton from './ConsumerButton';
import ActionButtons from './ActionButtons';

const { Text, Title } = Typography;

interface ShoppingListHeaderProps {
    shoppingList: ShoppingListWithItems;
    isHovered: boolean;
    purchasedCount: number;
    totalCount: number;
    onEdit: () => void;
    onDelete: () => void;
    onAddConsumers: () => void;
    canEdit: boolean;
}

export default function ShoppingListHeader({
    shoppingList,
    isHovered,
    purchasedCount,
    totalCount,
    onEdit,
    onDelete,
    onAddConsumers,
    canEdit
}: ShoppingListHeaderProps) {
    return (
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', width: '100%' }}>
            <Space align="center">
                <Title level={4} style={{ margin: 0 }}>
                    {shoppingList.title}
                </Title>
                {canEdit && (
                    <ConsumerButton
                        consumersCount={shoppingList.consumers.length}
                        onAddConsumers={onAddConsumers}
                    />
                )}
                <Tooltip title={getStatusTooltip(shoppingList.status)}>
                    <Tag color={getStatusColor(shoppingList.status)}>
                        {getStatusText(shoppingList.status)}
                    </Tag>
                </Tooltip>
            </Space>
            <div style={{ display: 'flex', alignItems: 'center' }}>
                <Text
                    type="secondary"
                    style={{
                        marginRight: isHovered && canEdit ? '8px' : '0px',
                        transition: 'margin-right 0.2s ease'
                    }}
                >
                    {purchasedCount}/{totalCount}
                </Text>
                {canEdit && (
                    <div style={{ width: isHovered ? 'auto' : '0px', overflow: 'hidden', transition: 'width 0.2s ease' }}>
                        <ActionButtons
                            isVisible={isHovered}
                            onEdit={onEdit}
                            onDelete={onDelete}
                        />
                    </div>
                )}
            </div>
        </div>
    );
} 