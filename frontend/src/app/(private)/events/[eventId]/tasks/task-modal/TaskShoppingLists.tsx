'use client';

import React, { useState, useMemo } from 'react';
import { Typography, Space, Button, App, Select, Tag } from 'antd';
import { EditOutlined, CheckOutlined, CloseOutlined, ShoppingCartOutlined } from '@ant-design/icons';
import type { ShoppingListWithItems, Task } from '@/store/api';
import { getTaskShoppingListsOptions, shoppingListsToSelectOptions } from '@/utils/shopping-list-utils';
import ShoppingListCard from '@/components/shopping-list-card/ShoppingListCard';

const { Title } = Typography;
const { Option } = Select;

interface TaskShoppingListsProps {
    shoppingLists: ShoppingListWithItems[];
    allShoppingLists: ShoppingListWithItems[];
    canEdit: boolean;
    canEditReceipt: boolean;
    onUpdate: (shoppingListIds: string[]) => Promise<void>;
    // Показывать списки как карточки (для исполнителя и проверяющего)
    showAsCards?: boolean;
    eventId?: string;
    expandedListId?: string;
    onToggleExpand?: (listId: string) => void;
    task: Task;
}

export default function TaskShoppingLists({
    shoppingLists,
    allShoppingLists,
    canEdit,
    canEditReceipt,
    onUpdate,
    showAsCards = false,
    eventId,
    expandedListId,
    onToggleExpand,
    task
}: TaskShoppingListsProps) {
    const { message } = App.useApp();
    const [isEditing, setIsEditing] = useState(false);
    const [isHovered, setIsHovered] = useState(false);
    const [value, setValue] = useState<string[]>([]);
    const [isUpdating, setIsUpdating] = useState(false);
    const [shouldCancel, setShouldCancel] = useState(false);

    // Создаем список для селекта: объединяем уже прикрепленные и доступные списки
    const selectOptions = useMemo(() => {
        const availableLists = getTaskShoppingListsOptions(shoppingLists, allShoppingLists);
        return shoppingListsToSelectOptions(availableLists);
    }, [shoppingLists, allShoppingLists]);

    const handleEdit = () => {
        if (!canEdit) {
            message.warning('У вас нет прав для редактирования этой задачи');
            return;
        }
        const currentIds = shoppingLists.map(list => list.shopping_list_id);
        setValue(currentIds);
        setIsEditing(true);
        setShouldCancel(false);
    };

    const handleContainerClick = () => {
        if (!isEditing && canEdit) {
            handleEdit();
        }
    };

    const handleSave = async () => {
        if (shouldCancel) return;

        // Проверяем, изменились ли данные
        const currentIds = shoppingLists.map(list => list.shopping_list_id);
        const hasChanged = value.length !== currentIds.length ||
            value.some(id => !currentIds.includes(id)) ||
            currentIds.some(id => !value.includes(id));

        if (!hasChanged) {
            setIsEditing(false);
            return;
        }

        setIsUpdating(true);
        try {
            await onUpdate(value);
            setIsEditing(false);
        } catch (error) {
            // Ошибка уже обработана в родительском компоненте
        } finally {
            setIsUpdating(false);
        }
    };

    const handleCancel = () => {
        setShouldCancel(true);
        setValue([]);
        setIsEditing(false);
    };

    const handleBlur = async () => {
        if (!shouldCancel && !isUpdating) {
            await handleSave();
        }
    };

    return (
        <div
            style={{
                marginBottom: '24px',
                cursor: canEdit && !isEditing ? 'pointer' : 'default'
            }}
            onMouseEnter={() => setIsHovered(true)}
            onMouseLeave={() => setIsHovered(false)}
            onClick={handleContainerClick}
        >
            <div style={{ display: 'flex', alignItems: 'center', marginBottom: '8px' }}>
                <ShoppingCartOutlined style={{ color: '#8c8c8c', marginRight: '8px' }} />
                <Title level={5} style={{ margin: 0, marginRight: '8px' }}>
                    Списки покупок
                </Title>
                {canEdit && !isEditing && (
                    <Button
                        type="text"
                        icon={<EditOutlined style={{ color: '#8c8c8c' }} />}
                        onClick={handleEdit}
                        size="small"
                        style={{
                            opacity: isHovered ? 0.6 : 0,
                            padding: '2px 4px',
                            height: 'auto',
                            transition: 'opacity 0.2s ease-in-out',
                        }}
                        onMouseEnter={(e) => e.currentTarget.style.opacity = '1'}
                        onMouseLeave={(e) => e.currentTarget.style.opacity = isHovered ? '0.6' : '0'}
                    />
                )}
                {isEditing && (
                    <Space size="small">
                        <Button
                            type="text"
                            icon={<CheckOutlined style={{ color: '#8c8c8c' }} />}
                            onClick={handleSave}
                            loading={isUpdating}
                            size="small"
                            style={{ padding: '2px 4px', height: 'auto' }}
                        />
                        <Button
                            type="text"
                            icon={<CloseOutlined style={{ color: '#8c8c8c' }} />}
                            onClick={handleCancel}
                            disabled={isUpdating}
                            size="small"
                            style={{ padding: '2px 4px', height: 'auto' }}
                        />
                    </Space>
                )}
            </div>

            {showAsCards ? (
                // Режим карточек (для исполнителя и проверяющего)
                <div style={{ minHeight: '24px' }}>
                    {shoppingLists && shoppingLists.length > 0 ? (
                        shoppingLists.map(list => (
                            <div key={list.shopping_list_id} style={{ marginBottom: '16px' }}>
                                <ShoppingListCard
                                    shoppingList={list}
                                    eventId={eventId!}
                                    canEdit={false}
                                    canMarkAsPurchased={true}
                                    expandedListId={expandedListId}
                                    onToggleExpand={onToggleExpand}
                                    showStatus={false}
                                    enableBudgetInput={true}
                                    showReceiptPreview={true}
                                    canEditReceipt={canEditReceipt}
                                />
                            </div>
                        ))
                    ) : (
                        <span style={{ color: '#8c8c8c', fontStyle: 'italic' }}>
                            Списки покупок не прикреплены к задаче
                        </span>
                    )}
                </div>
            ) : isEditing ? (
                <div onClick={(e) => e.stopPropagation()}>
                    <Select
                        mode="multiple"
                        value={value}
                        onChange={setValue}
                        onBlur={handleBlur}
                        placeholder="Выберите списки покупок"
                        allowClear
                        showSearch
                        style={{ width: '100%' }}
                        autoFocus
                        notFoundContent="Нет доступных списков покупок"
                    >
                        {selectOptions.map((option) => (
                            <Option
                                key={option.value}
                                value={option.value}
                            >
                                {option.label}
                            </Option>
                        ))}
                    </Select>
                </div>
            ) : (
                <div style={{ minHeight: '24px' }}>
                    {shoppingLists && shoppingLists.length > 0 ? (
                        <div>
                            {shoppingLists.map(list => (
                                <Tag
                                    key={list.shopping_list_id}
                                    style={{
                                        marginBottom: '4px',
                                        marginRight: '8px',
                                        padding: '4px 8px',
                                        fontSize: '13px',
                                        lineHeight: '1.4'
                                    }}
                                >
                                    {list.title}
                                </Tag>
                            ))}
                        </div>
                    ) : (
                        <span style={{ color: '#8c8c8c', fontStyle: 'italic' }}>
                            Списки покупок не прикреплены к задаче
                        </span>
                    )}
                </div>
            )}
        </div>
    );
} 