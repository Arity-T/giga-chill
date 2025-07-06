'use client';

import React from 'react';
import { Card, Typography, Space, Tag, Button } from 'antd';
import { CalendarOutlined, EnvironmentOutlined, UserOutlined, ArrowLeftOutlined } from '@ant-design/icons';
import { useRouter } from 'next/navigation';

const { Title, Text, Paragraph } = Typography;

interface EventDetailsPageProps {
    params: Promise<{
        eventId: string;
    }>;
}

export default function EventDetailsPage({ params }: EventDetailsPageProps) {
    const router = useRouter();
    const { eventId } = React.use(params);

    const handleGoBack = () => {
        router.back();
    };

    return (
        <div style={{ padding: '24px' }}>
            <div style={{ marginBottom: '24px' }}>
                <Button
                    icon={<ArrowLeftOutlined />}
                    onClick={handleGoBack}
                    type="text"
                >
                    Назад к событиям
                </Button>
            </div>

            <Card>
                <Space direction="vertical" size="large" style={{ width: '100%' }}>
                    <div>
                        <Title level={2}>Детали события</Title>
                        <Text type="secondary">ID события: {eventId}</Text>
                    </div>

                    <div>
                        <Text strong>Здесь будет отображаться полная информация о событии</Text>
                    </div>

                    <div>
                        <Text type="secondary">
                            Страница в разработке. Будет загружаться событие по ID: {eventId}
                        </Text>
                    </div>
                </Space>
            </Card>
        </div>
    );
}
