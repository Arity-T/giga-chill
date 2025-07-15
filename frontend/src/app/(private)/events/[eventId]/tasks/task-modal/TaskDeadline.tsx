'use client';

import React, { useState } from 'react';
import { Typography, DatePicker, App, Space, Button } from 'antd';
import { CalendarOutlined, EditOutlined, CheckOutlined, CloseOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { formatDateTime } from '@/utils/datetime-utils';

const { Title } = Typography;

interface TaskDeadlineProps {
    deadlineDateTime: string;
    canEdit: boolean;
    onUpdate: (deadline: string) => Promise<void>;
}

export default function TaskDeadline({ deadlineDateTime, canEdit, onUpdate }: TaskDeadlineProps) {
    const { message } = App.useApp();
    const [isEditing, setIsEditing] = useState(false);
    const [value, setValue] = useState<dayjs.Dayjs | null>(null);
    const [isUpdating, setIsUpdating] = useState(false);
    const [shouldCancel, setShouldCancel] = useState(false);

    const handleEdit = () => {
        if (!canEdit) {
            message.warning('У вас нет прав для редактирования этой задачи');
            return;
        }
        setValue(dayjs(deadlineDateTime));
        setIsEditing(true);
        setShouldCancel(false);
    };

    const handleSave = async () => {
        if (!value || shouldCancel) return;

        // Проверяем, изменились ли данные
        const currentDeadline = dayjs(deadlineDateTime);
        if (value.isSame(currentDeadline)) {
            setIsEditing(false);
            return;
        }

        setIsUpdating(true);
        try {
            await onUpdate(value.toISOString());
            setIsEditing(false);
        } catch (error) {
            // Ошибка уже обработана в родительском компоненте
        } finally {
            setIsUpdating(false);
        }
    };

    const handleCancel = () => {
        setShouldCancel(true);
        setValue(null);
        setIsEditing(false);
    };

    const handleBlur = async () => {
        if (!shouldCancel && !isUpdating && value) {
            await handleSave();
        }
    };

    return (
        <div>
            <div style={{ display: 'flex', alignItems: 'center', marginBottom: '8px' }}>
                <Title level={5} style={{ margin: 0, marginRight: '8px', marginTop: '0px' }}>
                    <CalendarOutlined style={{ marginRight: '8px', color: '#8c8c8c' }} />
                    Дедлайн
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
                <DatePicker
                    value={value}
                    showTime
                    format="DD.MM.YYYY HH:mm"
                    style={{ width: '100%' }}
                    onChange={setValue}
                    onBlur={handleBlur}
                    placeholder="Выберите дедлайн"
                    autoFocus
                />
            ) : (
                <div style={{
                    padding: '0px 11px 8px 0px',
                    fontSize: '14px',
                    color: '#262626'
                }}>
                    {formatDateTime(deadlineDateTime)}
                </div>
            )}
        </div>
    );
} 