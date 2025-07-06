import React from 'react';
import { Tag, Tooltip } from 'antd';
import { UserOutlined } from '@ant-design/icons';
import { UserRole } from '@/types/api';
import { getRoleColor, getRoleText } from '@/utils/role-utils';

interface UserRoleTagProps {
    role: UserRole;
    tooltip?: string;
}

export default function UserRoleTag({ role, tooltip = "Ваша роль в мероприятии" }: UserRoleTagProps) {
    return (
        <Tooltip title={tooltip}>
            <Tag
                color={getRoleColor(role)}
                icon={<UserOutlined />}
            >
                {getRoleText(role)}
            </Tag>
        </Tooltip>
    );
} 