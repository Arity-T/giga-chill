import React from 'react';
import { Table, Space, Tag } from 'antd';
import type { Participant, Participants } from '@/store/api';
import { UserRole } from '@/store/api';
import type { User, Event } from '@/store/api';
import ParticipantRoleSelect from './ParticipantRoleSelect';
import ParticipantActions from './ParticipantActions';

interface ParticipantTableProps {
    participants: Participants;
    event: Event;
    currentUser: User;
    onRoleChange: (participant: Participant, newRole: UserRole) => Promise<void>;
    onDeleteParticipant: (participant: Participant) => Promise<void>;
}

export default function ParticipantTable({
    participants,
    event,
    currentUser,
    onRoleChange,
    onDeleteParticipant
}: ParticipantTableProps) {
    const currentUserRole = event.user_role;
    const isOwner = currentUserRole === UserRole.Owner;
    const isAdmin = currentUserRole === UserRole.Admin;

    const columns = [
        {
            title: 'Имя',
            dataIndex: 'name',
            key: 'name',
            render: (text: string, record: Participant) => (
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
            render: (_: unknown, record: Participant) => {
                const isCurrentUser = record.id === currentUser.id;
                const canChangeRole = isOwner && record.user_role !== UserRole.Owner && !isCurrentUser;

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
            render: (_: unknown, record: Participant) => {
                const isCurrentUser = record.id === currentUser.id;
                const canDelete =
                    !isCurrentUser && (
                        (isOwner && record.user_role !== UserRole.Owner) ||
                        (isAdmin && record.user_role === UserRole.Participant)
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