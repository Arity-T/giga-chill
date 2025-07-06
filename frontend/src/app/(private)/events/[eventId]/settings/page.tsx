'use client';

import React from 'react';
import { Typography, Card, Space } from 'antd';
import { useGetEventQuery } from '@/store/api/api';
import { EventIdPathParam } from '@/types/path-params';
import DeleteEventButton from './DeleteEventButton';

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

    return (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', width: '100%' }}>
            <Title level={3} style={{ margin: 0 }}>
                Настройки мероприятия
            </Title>

            <Card title="Опасная зона" size="small">
                <Space direction="vertical" size="middle" style={{ width: '100%' }}>
                    <Text type="secondary">
                        Здесь находятся действия, которые могут повлиять на мероприятие.
                        Будьте осторожны при выполнении этих действий.
                    </Text>

                    <DeleteEventButton event={event} />
                </Space>
            </Card>
        </div>
    );
} 