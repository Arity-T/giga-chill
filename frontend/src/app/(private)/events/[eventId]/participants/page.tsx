'use client';

import React from 'react';
import { Typography, Alert } from 'antd';
import { TeamOutlined } from '@ant-design/icons';
import { EventIdPathParam } from '@/types/path-params';

const { Title } = Typography;

export default function ParticipantsPage({ params }: EventIdPathParam) {
    const { eventId } = React.use(params);

    return (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', width: '100%' }}>
            <Title level={3} style={{ margin: 0 }}>
                <TeamOutlined style={{ marginRight: '8px' }} />
                Участники
            </Title>

            <Alert
                message="Страница в разработке"
                description="Здесь будет отображаться список участников мероприятия, их роли и возможность управления участниками."
                type="info"
                showIcon
            />
        </div>
    );
} 