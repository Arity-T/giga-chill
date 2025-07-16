'use client';

import React, { useState, useRef, useEffect } from 'react';
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
    const [isHovered, setIsHovered] = useState(false);
    const [value, setValue] = useState<dayjs.Dayjs | null>(null);
    const [isUpdating, setIsUpdating] = useState(false);
    const [isOpen, setIsOpen] = useState(false);
    const containerRef = useRef<HTMLDivElement>(null);

    // Используем ref для хранения актуального значения
    const currentValueRef = useRef<dayjs.Dayjs | null>(null);

    const handleEdit = () => {
        if (!canEdit) {
            message.warning('У вас нет прав для редактирования этой задачи');
            return;
        }
        const initialValue = dayjs(deadlineDateTime);
        setValue(initialValue);
        currentValueRef.current = initialValue;
        setIsEditing(true);
        setIsOpen(true);
    };

    const handleContainerClick = () => {
        if (!isEditing && canEdit) {
            handleEdit();
        }
    };

    const handleSave = async () => {
        if (!value) return;

        // Проверяем, изменились ли данные
        const currentDeadline = dayjs(deadlineDateTime);
        if (value.isSame(currentDeadline, 'minute')) {
            setIsEditing(false);
            setIsOpen(false);
            return;
        }

        setIsUpdating(true);
        try {
            await onUpdate(value.toISOString());
            setIsEditing(false);
            setIsOpen(false);
        } catch (error) {
            // Ошибка уже обработана в родительском компоненте
        } finally {
            setIsUpdating(false);
        }
    };

    const handleCancel = () => {
        setValue(null);
        currentValueRef.current = null;
        setIsEditing(false);
        setIsOpen(false);
    };

    const handleDateChange = (newValue: dayjs.Dayjs | null) => {
        setValue(newValue);
        currentValueRef.current = newValue;
    };

    const handleOpenChange = (open: boolean) => {
        setIsOpen(open);

        // Если календарь закрылся после выбора даты, сохраняем
        if (!open && currentValueRef.current) {
            const valueToSave = currentValueRef.current;

            // Проверяем, изменились ли данные
            const currentDeadline = dayjs(deadlineDateTime);
            if (!valueToSave.isSame(currentDeadline, 'minute')) {
                setIsUpdating(true);
                onUpdate(valueToSave.toISOString()).then(() => {
                    setIsEditing(false);
                    setIsOpen(false);
                }).catch((error) => {
                    // Ошибка уже обработана в родительском компоненте
                }).finally(() => {
                    setIsUpdating(false);
                });
            } else {
                setIsEditing(false);
                setIsOpen(false);
            }
        }
    };

    // Обработчик клика вне компонента
    useEffect(() => {
        const handleClickOutside = async (event: MouseEvent) => {
            if (!isEditing) return;

            const target = event.target as HTMLElement;

            // Проверяем, что клик не по нашему контейнеру
            if (containerRef.current && containerRef.current.contains(target)) {
                return;
            }

            // Проверяем, что клик не по элементам календаря Antd
            const isDatePickerElement = target.closest('.ant-picker-dropdown') ||
                target.closest('.ant-picker') ||
                target.closest('.ant-picker-panel') ||
                target.closest('.ant-picker-time-panel');

            if (isDatePickerElement) {
                return;
            }

            // Клик вне компонента - сохраняем и закрываем
            if (currentValueRef.current) {
                const currentDeadline = dayjs(deadlineDateTime);
                if (!currentValueRef.current.isSame(currentDeadline, 'minute')) {
                    setIsUpdating(true);
                    try {
                        await onUpdate(currentValueRef.current.toISOString());
                    } catch (error) {
                        // Ошибка уже обработана в родительском компоненте
                    } finally {
                        setIsUpdating(false);
                    }
                }
            }
            setIsEditing(false);
            setIsOpen(false);
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [isEditing, deadlineDateTime, onUpdate]);

    return (
        <div
            ref={containerRef}
            onMouseEnter={() => setIsHovered(true)}
            onMouseLeave={() => setIsHovered(false)}
            onClick={handleContainerClick}
            style={{
                cursor: canEdit && !isEditing ? 'pointer' : 'default'
            }}
        >
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

            {isEditing ? (
                <DatePicker
                    value={value}
                    open={isOpen}
                    showTime
                    format="DD.MM.YYYY HH:mm"
                    style={{ width: '100%' }}
                    onChange={handleDateChange}
                    onOpenChange={handleOpenChange}
                    placeholder="Выберите дедлайн"
                    disabledDate={(current) => current && current < dayjs().startOf('day')}
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