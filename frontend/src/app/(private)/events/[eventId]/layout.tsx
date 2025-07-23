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
    SettingOutlined,
    CalculatorOutlined
} from '@ant-design/icons';
import { useRouter, usePathname } from 'next/navigation';
import { useGetEventQuery } from '@/store/api';
import { Button, Typography } from 'antd';
import UserRoleTag from '@/components/UserRoleTag';
import { PAGES } from '@/config/pages.config';
import { UserRole } from '@/types/api';

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
        router.push(PAGES.EVENTS);
    };

    const baseMenuItems = [
        {
            key: PAGES.EVENT_DETAILS(eventId),
            icon: <InfoCircleOutlined />,
            label: (
                <Link href={PAGES.EVENT_DETAILS(eventId)}>
                    Общая информация
                </Link>
            ),
        },
        {
            key: PAGES.EVENT_PARTICIPANTS(eventId),
            icon: <TeamOutlined />,
            label: (
                <Link href={PAGES.EVENT_PARTICIPANTS(eventId)}>
                    Участники
                </Link>
            ),
        },
        {
            key: PAGES.EVENT_TASKS(eventId),
            icon: <CheckSquareOutlined />,
            label: (
                <Link href={PAGES.EVENT_TASKS(eventId)}>
                    Задачи
                </Link>
            ),
        },
        {
            key: PAGES.EVENT_SHOPPING(eventId),
            icon: <ShoppingCartOutlined />,
            label: (
                <Link href={PAGES.EVENT_SHOPPING(eventId)}>
                    Списки покупок
                </Link>
            ),
        },
        {
            key: PAGES.EVENT_MY_BALANCE(eventId),
            icon: <DollarOutlined />,
            label: (
                <Link href={PAGES.EVENT_MY_BALANCE(eventId)}>
                    Мой баланс
                </Link>
            ),
        },
    ];

    const adminMenuItems = [
        {
            key: PAGES.EVENT_BALANCE_SUMMARY(eventId),
            icon: <CalculatorOutlined />,
            label: (
                <Link href={PAGES.EVENT_BALANCE_SUMMARY(eventId)}>
                    Общие расчёты
                </Link>
            ),
        },
    ];

    const ownerMenuItems = [
        {
            key: PAGES.EVENT_SETTINGS(eventId),
            icon: <SettingOutlined />,
            label: (
                <Link href={PAGES.EVENT_SETTINGS(eventId)}>
                    Настройки
                </Link>
            ),
        },
    ];

    // Собираем меню в зависимости от роли
    let menuItems = [...baseMenuItems];

    if (event?.user_role === UserRole.ADMIN || event?.user_role === UserRole.OWNER) {
        menuItems = [...menuItems, ...adminMenuItems];
    }

    if (event?.user_role === UserRole.OWNER) {
        menuItems = [...menuItems, ...ownerMenuItems];
    }

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