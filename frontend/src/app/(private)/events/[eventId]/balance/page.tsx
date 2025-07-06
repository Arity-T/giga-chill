'use client';

import React from 'react';
import { Typography, Alert } from 'antd';
import { DollarOutlined } from '@ant-design/icons';

const { Title } = Typography;

interface BalancePageProps {
    params: Promise<{
        eventId: string;
    }>;
}

export default function BalancePage({ params }: BalancePageProps) {
    const { eventId } = React.use(params);

    return (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', width: '100%' }}>
            <Title level={3} style={{ margin: 0 }}>
                <DollarOutlined style={{ marginRight: '8px' }} />
                Мой баланс
            </Title>

            <Alert
                message="Страница в разработке"
                description="Здесь будет отображаться ваш финансовый баланс в рамках мероприятия, история трат и возможность добавления расходов."
                type="info"
                showIcon
            />
        </div>
    );
} 