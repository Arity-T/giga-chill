'use client';

import React from 'react';
import { Dropdown, Button, App, Typography } from 'antd';
import { DownOutlined, LogoutOutlined } from '@ant-design/icons';
import type { MenuProps } from 'antd';
import { useGetMeQuery, useLogoutMutation } from '@/store/api';

const { Text } = Typography

export default function UserDropdown() {
    const { data: user } = useGetMeQuery();
    const [logout] = useLogoutMutation();
    const { message } = App.useApp();

    const handleLogout = async () => {
        try {
            await logout().unwrap();
            message.success('Вы успешно вышли из системы');
        } catch (error) {
            message.error('Ошибка при выходе из системы');
        }
    };

    const items: MenuProps['items'] = [
        {
            key: 'logout',
            label: 'Выйти',
            icon: <LogoutOutlined style={{ color: '#8c8c8c' }} />,
            onClick: handleLogout,
        },
    ];

    if (!user) {
        return null;
    }

    return (
        <Dropdown menu={{ items }} trigger={['click']}>
            <Button type="text" style={{ height: 'auto', padding: '4px 8px' }}>
                <span style={{ marginRight: '4px' }}>
                    <Text strong style={{ marginRight: 4 }}>{user.name}</Text>
                    <Text type="secondary">@{user.login}</Text>
                </span>
                <DownOutlined style={{ color: '#8c8c8c' }} />
            </Button>
        </Dropdown>
    );
} 