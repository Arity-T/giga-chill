import React from 'react';
import { Button, App } from 'antd';
import { DeleteOutlined } from '@ant-design/icons';
import { UserInEvent } from '@/types/api';
import type { Event } from '@/store/api';

interface ParticipantActionsProps {
    participant: UserInEvent;
    event: Event;
    onDeleteParticipant: (participant: UserInEvent) => Promise<void>;
    canDelete: boolean;
}

export default function ParticipantActions({
    participant,
    event,
    onDeleteParticipant,
    canDelete
}: ParticipantActionsProps) {
    const { modal } = App.useApp();

    if (!canDelete) {
        return null;
    }

    const handleDelete = () => {
        modal.confirm({
            title: 'Удаление участника',
            content: (
                <div>
                    <p>
                        Вы уверены, что хотите удалить <strong>{participant.name}</strong> из мероприятия{' '}
                        <strong>{event.title}</strong>?
                    </p>
                    <p>Это действие нельзя отменить.</p>
                </div>
            ),
            okText: 'Да, удалить',
            okType: 'danger',
            cancelText: 'Отмена',
            onOk: () => onDeleteParticipant(participant),
        });
    };

    return (
        <Button
            type="text"
            icon={<DeleteOutlined />}
            size="small"
            onClick={handleDelete}
            style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                color: '#8c8c8c',
            }}
        />
    );
} 