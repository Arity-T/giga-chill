'use client';

import React, { useState } from 'react';
import { Typography, Space, Input, Button, App } from 'antd';
import { EditOutlined, CheckOutlined, CloseOutlined, FileTextOutlined } from '@ant-design/icons';

const { Title, Paragraph } = Typography;
const { TextArea } = Input;

interface TaskDescriptionProps {
    description: string | null;
    canEdit: boolean;
    onUpdate: (description: string) => Promise<void>;
}

export default function TaskDescription({ description, canEdit, onUpdate }: TaskDescriptionProps) {
    const { message } = App.useApp();
    const [isEditing, setIsEditing] = useState(false);
    const [value, setValue] = useState('');
    const [isUpdating, setIsUpdating] = useState(false);
    const [shouldCancel, setShouldCancel] = useState(false);

    const handleEdit = () => {
        if (!canEdit) {
            message.warning('У вас нет прав для редактирования этой задачи');
            return;
        }
        setValue(description || '');
        setIsEditing(true);
        setShouldCancel(false);
    };

    const handleSave = async () => {
        if (shouldCancel) return;

        // Проверяем, изменились ли данные
        if (value === (description || '')) {
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
        setValue('');
        setIsEditing(false);
    };

    const handleBlur = async () => {
        if (!shouldCancel && !isUpdating) {
            await handleSave();
        }
    };

    return (
        <div style={{ marginBottom: '24px' }}>
            <div style={{ display: 'flex', alignItems: 'center', marginBottom: '8px' }}>
                <FileTextOutlined style={{ color: '#8c8c8c', marginRight: '8px' }} />
                <Title level={5} style={{ margin: 0, marginRight: '8px' }}>
                    Описание
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
                <TextArea
                    value={value}
                    onChange={(e) => setValue(e.target.value)}
                    onBlur={handleBlur}
                    placeholder="Введите описание задачи"
                    autoSize={{ minRows: 2, maxRows: 12 }}
                    style={{ marginBottom: '1px', padding: '11px' }}
                    autoFocus
                />
            ) : (
                <Paragraph
                    style={{
                        minHeight: '69px',
                        backgroundColor: '#fafafa',
                        padding: '12px',
                        borderRadius: '6px',
                        margin: 0,
                        whiteSpace: 'pre-wrap'
                    }}
                >
                    {description || 'Описание отсутствует'}
                </Paragraph>
            )}
        </div>
    );
} 