'use client';

import React from 'react';
import { Card, Tag, Space, Typography } from 'antd';
import { CalendarOutlined, EnvironmentOutlined, UserOutlined } from '@ant-design/icons';
import Link from 'next/link';
import { formatDateTime } from "@/utils/datetime-utils";
import { getRoleColor, getRoleText } from "@/utils/role-utils";
import { PAGES } from "@/config/pages.config";
import type { Event } from '@/types/api';

const { Text } = Typography;

interface EventCardProps {
    event: Event;
}

export default function EventCard({ event }: EventCardProps) {
    const extraContent = (
        <Space>
            <Tag
                color={getRoleColor(event.user_role)}
                icon={<UserOutlined />}
            >
                {getRoleText(event.user_role)}
            </Tag>
        </Space>
    );

    return (
        <Link href={PAGES.EVENT_DETAILS(event.event_id)} style={{ textDecoration: 'none' }}>
            <Card
                title={event.title}
                extra={extraContent}
                hoverable
                style={{ cursor: 'pointer' }}
            >
                <Space direction="vertical" size="small" style={{ width: '100%' }}>
                    <div>
                        <EnvironmentOutlined style={{ marginRight: 8, color: '#1890ff' }} />
                        <Text>{event.location}</Text>
                    </div>

                    <div>
                        <CalendarOutlined style={{ marginRight: 8, color: '#52c41a' }} />
                        <Text>
                            {formatDateTime(event.start_datetime)} — {formatDateTime(event.end_datetime)}
                        </Text>
                    </div>

                    {!!event.description && (
                        <Text type="secondary" ellipsis>
                            {event.description}
                        </Text>
                    )}

                    {!!event.budget && (
                        <Text strong>
                            Бюджет: {event.budget.toLocaleString('ru-RU')} ₽
                        </Text>
                    )}
                </Space>
            </Card>
        </Link>
    );
} 