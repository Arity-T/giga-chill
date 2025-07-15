'use client';

import React, { useState } from 'react';
import { Typography, Select, App, Space, Button } from 'antd';
import { UserOutlined, EditOutlined, CheckOutlined, CloseOutlined } from '@ant-design/icons';
import { User } from '@/types/api';

const { Title } = Typography;

interface TaskExecutorProps {
    executor: User | null;
    canEdit: boolean;
    participants: User[];
    onUpdate: (executor: User | null) => Promise<void>;
}

export default function TaskExecutor({ executor, canEdit, participants, onUpdate }: TaskExecutorProps) {
    const { message } = App.useApp();
    const [isEditing, setIsEditing] = useState(false);
    const [value, setValue] = useState<string | undefined>(undefined);
    const [isUpdating, setIsUpdating] = useState(false);
    const [shouldCancel, setShouldCancel] = useState(false);

    const handleEdit = () => {
        if (!canEdit) {
            message.warning('У вас нет прав для редактирования этой задачи');
            return;
        }
        setValue(executor?.id || undefined);
        setIsEditing(true);
        setShouldCancel(false);
    };

    const handleSave = async () => {
        if (shouldCancel) return;

        // Проверяем, изменились ли данные
        const currentExecutorId = executor?.id || undefined;
        if (value === currentExecutorId) {
            setIsEditing(false);
            return;
        }

        setIsUpdating(true);
        try {
            const selectedExecutor = value ? participants.find(p => p.id === value) || null : null;
            await onUpdate(selectedExecutor);
            setIsEditing(false);
        } catch (error) {
            // Ошибка уже обработана в родительском компоненте
        } finally {
            setIsUpdating(false);
        }
    };

    const handleCancel = () => {
        setShouldCancel(true);
        setValue(undefined);
        setIsEditing(false);
    };

    const handleBlur = async () => {
        if (!shouldCancel && !isUpdating) {
            await handleSave();
        }
    };

    return (
        <div>
            <div style={{ display: 'flex', alignItems: 'center', marginBottom: '8px' }}>
                <Title level={5} style={{ margin: 0, marginRight: '8px', marginTop: '0px' }}>
                    <UserOutlined style={{ marginRight: '8px', color: '#8c8c8c' }} />
                    Исполнитель
                </Title>
                {canEdit && !isEditing && (
                    <Button
                        type="text"
                        icon={<EditOutlined style={{ color: '#8c8c8c' }} />}
                        onClick={handleEdit}
                        size="small"
                        style={{ opacity: 0.6, padding: '2px 4px', height: 'auto' }}
                        onMouseEnter={(e) => e.currentTarget.style.opacity = '1'}
                        onMouseLeave={(e) => e.currentTarget.style.opacity = '0.6'}
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

            {isEditing ? (
                <Select
                    value={value}
                    placeholder="Введите имя или ник для поиска"
                    style={{ width: '100%' }}
                    onChange={setValue}
                    onBlur={handleBlur}
                    allowClear
                    autoFocus
                    showSearch
                    filterOption={(input, option) => {
                        const participant = participants.find(p => p.id === option?.value);
                        if (!participant) return false;
                        const searchText = input.toLowerCase();
                        const name = participant.name.toLowerCase();
                        const login = participant.login.toLowerCase();
                        return name.includes(searchText) || login.includes(searchText);
                    }}
                >
                    {participants.map(participant => (
                        <Select.Option key={participant.id} value={participant.id}>
                            {participant.name} (@{participant.login})
                        </Select.Option>
                    ))}
                </Select>
            ) : (
                <div style={{
                    padding: '0px 11px 8px 0px',
                    fontSize: '14px',
                    color: executor ? '#262626' : '#8c8c8c'
                }}>
                    {executor
                        ? `${executor.name} (@${executor.login})`
                        : 'Не назначен'
                    }
                </div>
            )}
        </div>
    );
} 