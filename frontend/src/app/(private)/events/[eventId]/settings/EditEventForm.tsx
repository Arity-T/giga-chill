'use client';

import React, { useState, useEffect } from 'react';
import { Input, DatePicker, Card, App } from 'antd';
import { useUpdateEventMutation } from '@/store/api/api';
import { Event, UpdateEventRequest } from '@/types/api';
import dayjs, { Dayjs } from 'dayjs';
import EditableField from './editable-field/EditableField';

const { TextArea } = Input;
const { RangePicker } = DatePicker;

interface EditEventFormProps {
    event: Event;
}

type FieldKey = 'title' | 'location' | 'description' | 'dateRange';

type FormValues = {
    title: string;
    location: string;
    description: string;
    dateRange: [Dayjs, Dayjs];
};

type FieldStates = Record<FieldKey, boolean>;

export default function EditEventForm({ event }: EditEventFormProps) {
    const { message } = App.useApp();
    const [updateEvent] = useUpdateEventMutation();

    // Текущие значения полей
    const [values, setValues] = useState<FormValues>({
        title: event.title,
        location: event.location,
        description: event.description,
        dateRange: [dayjs(event.start_datetime), dayjs(event.end_datetime)],
    });

    // Отслеживание изменений полей
    const [hasChanges, setHasChanges] = useState<FieldStates>({
        title: false,
        location: false,
        description: false,
        dateRange: false,
    });

    // Состояния загрузки для каждого поля
    const [loadingStates, setLoadingStates] = useState<FieldStates>({
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

    const resetField = (field: FieldKey) => {
        const resetValues: Record<FieldKey, () => void> = {
            title: () => setValues(prev => ({ ...prev, title: event.title })),
            location: () => setValues(prev => ({ ...prev, location: event.location })),
            description: () => setValues(prev => ({ ...prev, description: event.description })),
            dateRange: () => setValues(prev => ({ ...prev, dateRange: [dayjs(event.start_datetime), dayjs(event.end_datetime)] })),
        };

        resetValues[field]();
    };

    const validateAndGetUpdateData = (field: FieldKey): UpdateEventRequest | null => {
        switch (field) {
            case 'title':
                if (!values.title || values.title.length < 3) {
                    message.error('Название должно содержать минимум 3 символа');
                    return null;
                }
                return { title: values.title };

            case 'location':
                if (!values.location) {
                    message.error('Место проведения обязательно для заполнения');
                    return null;
                }
                return { location: values.location };

            case 'description':
                return { description: values.description };

            case 'dateRange':
                if (!values.dateRange[0] || !values.dateRange[1]) {
                    message.error('Необходимо выбрать дату и время');
                    return null;
                }
                return {
                    start_datetime: values.dateRange[0].toISOString(),
                    end_datetime: values.dateRange[1].toISOString(),
                };

            default:
                return null;
        }
    };

    const saveField = async (field: FieldKey) => {
        setLoadingStates(prev => ({ ...prev, [field]: true }));

        try {
            const updateData = validateAndGetUpdateData(field);
            if (!updateData) return;

            await updateEvent({ eventId: event.event_id, event: updateData }).unwrap();
            message.success('Поле успешно обновлено');
        } catch (error) {
            message.error('Ошибка при обновлении поля');
        } finally {
            setLoadingStates(prev => ({ ...prev, [field]: false }));
        }
    };

    return (
        <Card title="Общая информация" size="small">
            <EditableField
                label="Название мероприятия"
                hasChanges={hasChanges.title}
                isLoading={loadingStates.title}
                onSave={() => saveField('title')}
                onReset={() => resetField('title')}
            >
                <Input
                    value={values.title}
                    onChange={(e) => setValues(prev => ({ ...prev, title: e.target.value }))}
                    placeholder="Введите название мероприятия"
                    style={{ width: '100%' }}
                />
            </EditableField>

            <EditableField
                label="Место проведения"
                hasChanges={hasChanges.location}
                isLoading={loadingStates.location}
                onSave={() => saveField('location')}
                onReset={() => resetField('location')}
            >
                <Input
                    value={values.location}
                    onChange={(e) => setValues(prev => ({ ...prev, location: e.target.value }))}
                    placeholder="Введите место проведения"
                    style={{ width: '100%' }}
                />
            </EditableField>

            <EditableField
                label="Дата и время"
                hasChanges={hasChanges.dateRange}
                isLoading={loadingStates.dateRange}
                onSave={() => saveField('dateRange')}
                onReset={() => resetField('dateRange')}
            >
                <RangePicker
                    value={values.dateRange}
                    onChange={(dates) => {
                        if (dates && dates[0] && dates[1]) {
                            setValues(prev => ({ ...prev, dateRange: [dates[0]!, dates[1]!] }));
                        }
                    }}
                    showTime={{ format: 'HH:mm' }}
                    format="DD.MM.YYYY HH:mm"
                    placeholder={['Начало мероприятия', 'Конец мероприятия']}
                    style={{ width: '100%' }}
                />
            </EditableField>

            <EditableField
                label="Описание"
                hasChanges={hasChanges.description}
                isLoading={loadingStates.description}
                onSave={() => saveField('description')}
                onReset={() => resetField('description')}
            >
                <TextArea
                    value={values.description}
                    onChange={(e) => setValues(prev => ({ ...prev, description: e.target.value }))}
                    rows={4}
                    placeholder="Введите описание мероприятия"
                    maxLength={500}
                    showCount
                    style={{ width: '100%' }}
                />
            </EditableField>
        </Card>
    );
} 