'use client';

import React from 'react';
import { Typography, Alert } from 'antd';
import { ShoppingCartOutlined } from '@ant-design/icons';

const { Title } = Typography;

interface ShoppingPageProps {
    params: Promise<{
        eventId: string;
    }>;
}

export default function ShoppingPage({ params }: ShoppingPageProps) {
    const { eventId } = React.use(params);

    return (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', width: '100%' }}>
            <Title level={3} style={{ margin: 0 }}>
                <ShoppingCartOutlined style={{ marginRight: '8px' }} />
                Список покупок
            </Title>

            <Alert
                message="Страница в разработке"
                description="Здесь будет отображаться список покупок для мероприятия, возможность добавления новых товаров и отметки о покупке."
                type="info"
                showIcon
            />
        </div>
    );
} 