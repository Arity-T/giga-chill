'use client';

import React from 'react';
import { Typography, Alert } from 'antd';
import { CheckSquareOutlined } from '@ant-design/icons';

const { Title } = Typography;

interface TasksPageProps {
    params: Promise<{
        eventId: string;
    }>;
}

export default function TasksPage({ params }: TasksPageProps) {
    const { eventId } = React.use(params);

    return (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', width: '100%' }}>
            <Title level={3} style={{ margin: 0 }}>
                <CheckSquareOutlined style={{ marginRight: '8px' }} />
                Задачи
            </Title>

            <Alert
                message="Страница в разработке"
                description="Здесь будет отображаться список задач мероприятия, возможность создания новых задач и распределения их между участниками."
                type="info"
                showIcon
            />
        </div>
    );
} 