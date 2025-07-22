'use client';

import React from 'react';
import { Typography, Space, Alert } from 'antd';
import { useGetEventQuery } from '@/store/api';
import { EventIdPathParam } from '@/types/path-params';
import { UserRole } from '@/store/api';
import DeleteEventButton from './DeleteEventButton';
import EditEventForm from './EditEventForm';
import SettingsSection from './SettingsSection';

const { Title, Text } = Typography;

export default function EventSettingsPage({ params }: EventIdPathParam) {
    const { eventId } = React.use(params);
    const { data: event, isLoading } = useGetEventQuery(eventId);

    if (isLoading) {
        return <div>Загрузка...</div>;
    }

    if (!event) {
        return <div>Мероприятие не найдено</div>;
    }

    // Проверяем, является ли пользователь владельцем мероприятия
    if (event.user_role !== UserRole.Owner) {
        return (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', width: '100%' }}>
                <Title level={2} style={{ margin: 0 }}>
                    Настройки мероприятия
                </Title>

                <Alert
                    message="Доступ запрещён"
                    description="Только владелец мероприятия может получить доступ к настройкам."
                    type="warning"
                    showIcon
                />
            </div>
        );
    }

    return (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', width: '100%' }}>
            <Title level={2} style={{ margin: 0 }}>
                Настройки мероприятия
            </Title>

            <SettingsSection title="Общая информация">
                <EditEventForm event={event} />
            </SettingsSection>

            <SettingsSection title="Опасная зона">
                <Space direction="vertical" size="middle" style={{ width: '100%' }}>
                    <Text type="secondary">
                        Здесь находятся действия, которые могут повлиять на мероприятие.
                        Будьте осторожны при выполнении этих действий.
                    </Text>

                    <DeleteEventButton event={event} />
                </Space>
            </SettingsSection>
        </div>
    );
} 