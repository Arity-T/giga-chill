'use client';

import React from 'react';
import Link from 'next/link';
import { Layout, Menu, Card, Spin, Alert, Space } from 'antd';
import {
    InfoCircleOutlined,
    TeamOutlined,
    CheckSquareOutlined,
    DollarOutlined,
    ShoppingCartOutlined,
    ArrowLeftOutlined,
    SettingOutlined
} from '@ant-design/icons';
import { useRouter, usePathname } from 'next/navigation';
import { useGetEventQuery } from '@/store/api/api';
import { Button, Typography } from 'antd';
import UserRoleTag from '@/components/UserRoleTag';

const { Sider, Content } = Layout;
const { Title } = Typography;

interface EventLayoutProps {
    children: React.ReactNode;
    params: Promise<{
        eventId: string;
    }>;
}

export default function EventLayout({ children, params }: EventLayoutProps) {
    const router = useRouter();
    const pathname = usePathname();
    const { eventId } = React.use(params);

    const { data: event, isLoading, error } = useGetEventQuery(eventId);

    const handleGoBack = () => {
        router.push('/events');
    };

    const menuItems = [
        {
            key: `/events/${eventId}`,
            icon: <InfoCircleOutlined />,
            label: (
                <Link href={`/events/${eventId}`}>
                    Общая информация
                </Link>
            ),
        },
        {
            key: `/events/${eventId}/participants`,
            icon: <TeamOutlined />,
            label: (
                <Link href={`/events/${eventId}/participants`}>
                    Участники
                </Link>
            ),
        },
        {
            key: `/events/${eventId}/tasks`,
            icon: <CheckSquareOutlined />,
            label: (
                <Link href={`/events/${eventId}/tasks`}>
                    Задачи
                </Link>
            ),
        },
        {
            key: `/events/${eventId}/shopping`,
            icon: <ShoppingCartOutlined />,
            label: (
                <Link href={`/events/${eventId}/shopping`}>
                    Список покупок
                </Link>
            ),
        },
        {
            key: `/events/${eventId}/balance`,
            icon: <DollarOutlined />,
            label: (
                <Link href={`/events/${eventId}/balance`}>
                    Мой баланс
                </Link>
            ),
        },
        {
            key: `/events/${eventId}/settings`,
            icon: <SettingOutlined />,
            label: (
                <Link href={`/events/${eventId}/settings`}>
                    Настройки
                </Link>
            ),
        },
    ];

    if (isLoading) {
        return (
            <div style={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                height: '100vh'
            }}>
                <Spin size="large" />
            </div>
        );
    }

    if (error) {
        return (
            <div style={{ padding: '24px' }}>
                <Alert
                    message="Ошибка загрузки мероприятия"
                    description="Не удалось загрузить информацию о мероприятии"
                    type="error"
                    showIcon
                />
            </div>
        );
    }

    return (
        <div style={{ padding: '24px' }}>
            {event && (
                <div style={{ marginBottom: '24px' }}>
                    <Space align="center">
                        <Title level={2} style={{ margin: 0 }}>
                            {event.title}
                        </Title>
                        <UserRoleTag role={event.user_role} />
                    </Space>
                </div>
            )}

            <Layout style={{ background: 'transparent' }}>
                <Sider
                    width={250}
                    style={{
                        background: '#fff',
                        borderRadius: '8px',
                        marginRight: '24px',
                        display: 'flex',
                        flexDirection: 'column'
                    }}
                >
                    <Menu
                        mode="inline"
                        selectedKeys={[pathname]}
                        items={menuItems}
                        style={{ border: 'none', flex: 1 }}
                    />
                    <div style={{ padding: '16px', borderTop: '1px solid #f0f0f0' }}>
                        <Button
                            icon={<ArrowLeftOutlined />}
                            onClick={handleGoBack}
                            type="text"
                            block
                        >
                            Все мероприятия
                        </Button>
                    </div>
                </Sider>

                <Content>
                    <Card style={{ minHeight: '600px' }}>
                        {children}
                    </Card>
                </Content>
            </Layout>
        </div>
    );
} 