'use client';

import { Button, App } from 'antd';
import { DeleteOutlined } from '@ant-design/icons';
import { useRouter } from 'next/navigation';
import { useDeleteEventMutation } from '@/store/api';
import type { Event } from '@/store/api';
import { PAGES } from '@/config/pages.config';

interface DeleteEventButtonProps {
    event: Event;
}

export default function DeleteEventButton({ event }: DeleteEventButtonProps) {
    const router = useRouter();
    const { modal, message } = App.useApp();
    const [deleteEvent, { isLoading: isDeleting }] = useDeleteEventMutation();

    const handleDeleteEvent = () => {
        modal.confirm({
            title: 'Удаление мероприятия',
            content: (
                <div>
                    <p>Вы уверены, что хотите удалить мероприятие <strong>"{event.title}"</strong>?</p>
                    <p>Это действие нельзя отменить.</p>
                </div>
            ),
            okText: 'Да, удалить',
            okType: 'danger',
            cancelText: 'Отмена',
            onOk: async () => {
                try {
                    await deleteEvent(event.event_id).unwrap();
                    message.info('Мероприятие удалено!');
                    router.replace(PAGES.EVENTS);
                } catch (error) {
                    console.error('Ошибка при удалении мероприятия:', error);
                    message.error('Ошибка при удалении мероприятия');
                }
            },
        });
    };

    return (
        <Button
            type="primary"
            danger
            icon={<DeleteOutlined />}
            onClick={handleDeleteEvent}
            loading={isDeleting}
            style={{ alignSelf: 'flex-start' }}
        >
            Удалить мероприятие
        </Button>
    );
} 