'use client';

import React, { useState, useEffect } from 'react';
import { Input, DatePicker, Button, Card, Space, Typography, App } from 'antd';
import { CheckOutlined, CloseOutlined } from '@ant-design/icons';
import { useUpdateEventMutation } from '@/store/api/api';
import { Event, UpdateEventRequest } from '@/types/api';
import dayjs, { Dayjs } from 'dayjs';

const { TextArea } = Input;
const { RangePicker } = DatePicker;
const { Text } = Typography;

interface EditEventFormProps {
    event: Event;
}

interface FieldChanges {
    title: boolean;
    location: boolean;
    description: boolean;
    dateRange: boolean;
}

export default function EditEventForm({ event }: EditEventFormProps) {
    const { message } = App.useApp();
    const [updateEvent] = useUpdateEventMutation();

    // Текущие значения полей
    const [values, setValues] = useState({
        title: event.title,
        location: event.location,
        description: event.description,
        dateRange: [dayjs(event.start_datetime), dayjs(event.end_datetime)] as [Dayjs, Dayjs],
    });

    // Отслеживание изменений полей
    const [hasChanges, setHasChanges] = useState<FieldChanges>({
        title: false,
        location: false,
        description: false,
        dateRange: false,
    });

    // Состояния загрузки для каждого поля
    const [loadingStates, setLoadingStates] = useState<FieldChanges>({
        title: false,
        location: false,
        description: false,
        dateRange: false,
    });

    // Отслеживаем изменения в полях
    useEffect(() => {
        setHasChanges({
            title: values.title !== event.title,
            location: values.location !== event.location,
            description: values.description !== event.description,
            dateRange: !values.dateRange[0].isSame(dayjs(event.start_datetime)) ||
                !values.dateRange[1].isSame(dayjs(event.end_datetime)),
        });
    }, [values, event]);

    const resetField = (field: keyof FieldChanges) => {
        switch (field) {
            case 'title':
                setValues(prev => ({ ...prev, title: event.title }));
                break;
            case 'location':
                setValues(prev => ({ ...prev, location: event.location }));
                break;
            case 'description':
                setValues(prev => ({ ...prev, description: event.description }));
                break;
            case 'dateRange':
                setValues(prev => ({ ...prev, dateRange: [dayjs(event.start_datetime), dayjs(event.end_datetime)] as [Dayjs, Dayjs] }));
                break;
        }
    };

    const saveField = async (field: keyof FieldChanges) => {
        setLoadingStates(prev => ({ ...prev, [field]: true }));

        try {
            const updateData: UpdateEventRequest = {};

            switch (field) {
                case 'title':
                    if (!values.title || values.title.length < 3) {
                        message.error('Название должно содержать минимум 3 символа');
                        return;
                    }
                    updateData.title = values.title;
                    break;
                case 'location':
                    if (!values.location) {
                        message.error('Место проведения обязательно для заполнения');
                        return;
                    }
                    updateData.location = values.location;
                    break;
                case 'description':
                    updateData.description = values.description;
                    break;
                case 'dateRange':
                    if (!values.dateRange[0] || !values.dateRange[1]) {
                        message.error('Необходимо выбрать дату и время');
                        return;
                    }
                    updateData.start_datetime = values.dateRange[0].toISOString();
                    updateData.end_datetime = values.dateRange[1].toISOString();
                    break;
            }

            await updateEvent({ eventId: event.event_id, event: updateData }).unwrap();
            message.success('Поле успешно обновлено');
        } catch (error) {
            message.error('Ошибка при обновлении поля');
        } finally {
            setLoadingStates(prev => ({ ...prev, [field]: false }));
        }
    };

    const renderField = (
        field: keyof FieldChanges,
        label: string,
        inputElement: React.ReactNode
    ) => (
        <div style={{ marginBottom: '16px', maxWidth: '500px' }}>
            <Text strong style={{ display: 'block', marginBottom: '8px' }}>
                {label}
            </Text>
            <div style={{ position: 'relative' }}>
                <div style={{ width: '100%', paddingRight: '72px' }}>
                    {inputElement}
                </div>
                <div style={{
                    position: 'absolute',
                    right: '8px',
                    top: '50%',
                    transform: 'translateY(-50%)',
                    display: 'flex',
                    gap: '4px',
                    opacity: hasChanges[field] ? 1 : 0,
                    transition: 'opacity 0.2s ease'
                }}>
                    <Button
                        type="text"
                        icon={<CheckOutlined />}
                        onClick={() => saveField(field)}
                        loading={loadingStates[field]}
                        size="small"
                    />
                    <Button
                        type="text"
                        icon={<CloseOutlined />}
                        onClick={() => resetField(field)}
                        disabled={loadingStates[field]}
                        size="small"
                    />
                </div>
            </div>
        </div>
    );

    return (
        <Card title="Общая информация" size="small">
            {renderField(
                'title',
                'Название мероприятия',
                <Input
                    value={values.title}
                    onChange={(e) => setValues(prev => ({ ...prev, title: e.target.value }))}
                    placeholder="Введите название мероприятия"
                    style={{ width: '100%' }}
                />
            )}

            {renderField(
                'location',
                'Место проведения',
                <Input
                    value={values.location}
                    onChange={(e) => setValues(prev => ({ ...prev, location: e.target.value }))}
                    placeholder="Введите место проведения"
                    style={{ width: '100%' }}
                />
            )}

            {renderField(
                'dateRange',
                'Дата и время',
                <RangePicker
                    value={values.dateRange}
                    onChange={(dates) => {
                        if (dates && dates[0] && dates[1]) {
                            setValues(prev => ({ ...prev, dateRange: [dates[0], dates[1]] as [Dayjs, Dayjs] }));
                        }
                    }}
                    showTime={{ format: 'HH:mm' }}
                    format="DD.MM.YYYY HH:mm"
                    placeholder={['Начало мероприятия', 'Конец мероприятия']}
                    style={{ width: '100%' }}
                />
            )}

            {renderField(
                'description',
                'Описание',
                <TextArea
                    value={values.description}
                    onChange={(e) => setValues(prev => ({ ...prev, description: e.target.value }))}
                    rows={4}
                    placeholder="Введите описание мероприятия"
                    maxLength={500}
                    showCount
                    style={{ width: '100%' }}
                />
            )}
        </Card>
    );
} 