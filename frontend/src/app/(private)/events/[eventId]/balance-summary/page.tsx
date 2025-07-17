'use client';

import React from 'react';
import { Typography, Alert } from 'antd';
import { DollarOutlined } from '@ant-design/icons';
import { EventIdPathParam } from '@/types/path-params';

const { Title } = Typography;

export default function BalancePage({ params }: EventIdPathParam) {
    const { eventId } = React.use(params);

    return (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', width: '100%' }}>
            <Title level={3} style={{ margin: 0 }}>
                <DollarOutlined style={{ marginRight: '8px' }} />
                Мой баланс
            </Title>

            <Alert
                message="Страница в разработке"
                description="Здесь будут отображаться финальные расчёты долгов участников. Они видны только админам."
                type="info"
                showIcon
            />
        </div>
    );
} 