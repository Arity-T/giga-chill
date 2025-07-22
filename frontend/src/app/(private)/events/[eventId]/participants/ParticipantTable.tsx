import React from 'react';
import { Table, Space, Tag } from 'antd';
import { UserRole, UserInEvent, Event } from '@/types/api';
import type { User } from '@/store/api';
import ParticipantRoleSelect from './ParticipantRoleSelect';
import ParticipantActions from './ParticipantActions';

interface ParticipantTableProps {
    participants: UserInEvent[];
    event: Event;
    currentUser: User;
    onRoleChange: (participant: UserInEvent, newRole: UserRole) => Promise<void>;
    onDeleteParticipant: (participant: UserInEvent) => Promise<void>;
}

export default function ParticipantTable({
    participants,
    event,
    currentUser,
    onRoleChange,
    onDeleteParticipant
}: ParticipantTableProps) {
    const currentUserRole = event.user_role;
    const isOwner = currentUserRole === UserRole.OWNER;
    const isAdmin = currentUserRole === UserRole.ADMIN;

    const columns = [
        {
            title: 'Имя',
            dataIndex: 'name',
            key: 'name',
            render: (text: string, record: UserInEvent) => (
                <Space>
                    <span>{text}</span>
                    {record.login && (
                        <Tag color="default" style={{ fontSize: '11px' }}>
                            @{record.login}
                        </Tag>
                    )}
                </Space>
            ),
        },
        {
            title: 'Роль',
            dataIndex: 'user_role',
            key: 'user_role',
            render: (_: unknown, record: UserInEvent) => {
                const isCurrentUser = record.id === currentUser.id;
                const canChangeRole = isOwner && record.user_role !== UserRole.OWNER && !isCurrentUser;

                return (
                    <ParticipantRoleSelect
                        participant={record}
                        onRoleChange={onRoleChange}
                        disabled={!canChangeRole}
                    />
                );
            },
        },
        {
            title: 'Действия',
            key: 'actions',
            width: 100,
            render: (_: unknown, record: UserInEvent) => {
                const isCurrentUser = record.id === currentUser.id;
                const canDelete =
                    !isCurrentUser && (
                        (isOwner && record.user_role !== UserRole.OWNER) ||
                        (isAdmin && record.user_role === UserRole.PARTICIPANT)
                    );

                return (
                    <ParticipantActions
                        participant={record}
                        event={event}
                        onDeleteParticipant={onDeleteParticipant}
                        canDelete={canDelete}
                    />
                );
            },
        },
    ];

    return (
        <Table
            dataSource={participants}
            columns={columns}
            rowKey="id"
            pagination={false}
            size="middle"
        />
    );
} 