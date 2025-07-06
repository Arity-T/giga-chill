'use client';

import React from 'react';
import { Typography, Descriptions, Divider } from 'antd';
import { CalendarOutlined, EnvironmentOutlined, DollarOutlined } from '@ant-design/icons';
import { useGetEventQuery } from '@/store/api/api';
import { formatDateTime } from '@/utils/datetime-utils';

const { Title, Paragraph, Text } = Typography;

interface EventDetailsPageProps {
    params: Promise<{
        eventId: string;
    }>;
}

export default function EventDetailsPage({ params }: EventDetailsPageProps) {
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
            <div>
                <Title level={3} style={{ margin: 0, marginBottom: '16px' }}>
                    Общая информация
                </Title>

                {event.description && (
                    <Paragraph style={{ marginBottom: '24px' }}>
                        {event.description}
                    </Paragraph>
                )}
            </div>

            <Descriptions
                bordered
                column={1}
                size="middle"
                styles={{ label: { width: '200px', fontWeight: 'bold' } }}
            >
                <Descriptions.Item
                    label={
                        <span>
                            <CalendarOutlined style={{ marginRight: '8px' }} />
                            Начало мероприятия
                        </span>
                    }
                >
                    {formatDateTime(event.start_datetime)}
                </Descriptions.Item>

                <Descriptions.Item
                    label={
                        <span>
                            <CalendarOutlined style={{ marginRight: '8px' }} />
                            Окончание мероприятия
                        </span>
                    }
                >
                    {formatDateTime(event.end_datetime)}
                </Descriptions.Item>

                <Descriptions.Item
                    label={
                        <span>
                            <EnvironmentOutlined style={{ marginRight: '8px' }} />
                            Место проведения
                        </span>
                    }
                >
                    {event.location}
                </Descriptions.Item>

                <Descriptions.Item
                    label={
                        <span>
                            <DollarOutlined style={{ marginRight: '8px' }} />
                            Бюджет
                        </span>
                    }
                >
                    {event.budget.toLocaleString('ru-RU')} ₽
                </Descriptions.Item>
            </Descriptions>

            <Divider />

            <div>
                <Text type="secondary">
                    ID мероприятия: {event.event_id}
                </Text>
            </div>
        </div>
    );
}
