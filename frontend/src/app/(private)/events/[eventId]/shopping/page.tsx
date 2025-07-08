'use client';

import React from 'react';
import { Typography, Alert } from 'antd';
import { ShoppingCartOutlined } from '@ant-design/icons';
import { EventIdPathParam } from '@/types/path-params';

const { Title } = Typography;

export default function ShoppingPage({ params }: EventIdPathParam) {
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