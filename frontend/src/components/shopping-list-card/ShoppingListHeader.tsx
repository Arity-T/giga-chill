import React, { useState } from 'react';
import { Space, Typography, Tag, Tooltip, InputNumber, App } from 'antd';
import type { ShoppingListWithItems } from '@/store/api';
import { getStatusColor, getStatusText, getStatusTooltip } from '@/utils/shopping-status-utils';
import { ShoppingListStatus, useSetShoppingListBudgetMutation } from '@/store/api';
import InlineEditControls from '@/components/inline-edit-controls';
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
    showStatus?: boolean;
    enableBudgetInput?: boolean;
    eventId?: string;
}

export default function ShoppingListHeader({
    shoppingList,
    isHovered,
    purchasedCount,
    totalCount,
    onEdit,
    onDelete,
    onAddConsumers,
    canEdit,
    showStatus = true,
    enableBudgetInput = false,
    eventId
}: ShoppingListHeaderProps) {
    const { message } = App.useApp();
    const [budgetValue, setBudgetValue] = useState<number | null>(shoppingList.budget);
    const [setBudget, { isLoading: isSavingBudget }] = useSetShoppingListBudgetMutation();

    // Обновляем локальное состояние при изменении данных с сервера
    React.useEffect(() => {
        setBudgetValue(shoppingList.budget);
    }, [shoppingList.budget]);

    const hasBudgetChanges = budgetValue !== shoppingList.budget;

    const handleSaveBudget = async () => {
        if (!eventId || budgetValue === null || budgetValue === shoppingList.budget) return;

        try {
            await setBudget({
                eventId,
                shoppingListId: shoppingList.shopping_list_id,
                shoppingListSetBudget: {
                    budget: budgetValue
                }
            }).unwrap();
            message.success('Бюджет обновлен');
        } catch (error) {
            message.error('Ошибка при обновлении бюджета');
        }
    };

    const handleResetBudget = () => {
        setBudgetValue(shoppingList.budget);
    };

    const showBudget = (budgetValue !== null
        && (shoppingList.status === ShoppingListStatus.PartiallyBought ||
            shoppingList.status === ShoppingListStatus.Bought));

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
                {showStatus && (
                    <Tooltip title={getStatusTooltip(shoppingList.status)}>
                        <Tag color={getStatusColor(shoppingList.status)}>
                            {getStatusText(shoppingList.status)}
                        </Tag>
                    </Tooltip>
                )}
                {(enableBudgetInput || showBudget) && (
                    <div
                        style={{ display: 'flex', alignItems: 'center', gap: '8px', marginLeft: '24px' }}
                        onClick={(e) => e.stopPropagation()}
                    >
                        <InputNumber
                            value={budgetValue}
                            onChange={enableBudgetInput ? setBudgetValue : undefined}
                            placeholder="Бюджет"
                            min={0}
                            precision={2}
                            style={{ width: '120px' }}
                            addonAfter="₽"
                            disabled={!enableBudgetInput}
                        />
                        {enableBudgetInput && (
                            <InlineEditControls
                                hasChanges={hasBudgetChanges}
                                isLoading={isSavingBudget}
                                onSave={handleSaveBudget}
                                onReset={handleResetBudget}
                            />
                        )}
                    </div>
                )}
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
                    <div
                        style={{ width: isHovered ? 'auto' : '0px', overflow: 'hidden', transition: 'width 0.2s ease' }}
                        onClick={(e) => e.stopPropagation()}
                    >
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