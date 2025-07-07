import React from 'react';
import { Table, Space, Tag, App } from 'antd';
import { UserRole, UserInEvent, Event, User } from '@/types/api';
import ParticipantRoleSelect from './ParticipantRoleSelect';
import ParticipantActions from './ParticipantActions';

interface ParticipantTableProps {
    participants: UserInEvent[];
    event: Event;
    currentUser: User;
    isLoading: boolean;
}

export default function ParticipantTable({
    participants,
    event,
    currentUser,
    isLoading
}: ParticipantTableProps) {
    const { message } = App.useApp();

    const currentUserRole = event.user_role;
    const isOwner = currentUserRole === UserRole.OWNER;
    const isAdmin = currentUserRole === UserRole.ADMIN;

    const handleRoleChange = (participant: UserInEvent, newRole: UserRole) => {
        // TODO: Реализовать API запрос для изменения роли
        message.success(`Роль пользователя "${participant.name}" успешно изменена!`);
        console.log(`Changing role for user ${participant.id} to ${newRole}`);
    };

    const handleDeleteParticipant = (participant: UserInEvent) => {
        // TODO: Реализовать API запрос для удаления участника
        message.success(`Участник ${participant.name} удален из мероприятия`);
        console.log(`Deleting participant ${participant.id}`);
    };

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
                        onRoleChange={handleRoleChange}
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
                        onDeleteParticipant={handleDeleteParticipant}
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
            loading={isLoading}
            pagination={false}
            size="middle"
        />
    );
} 