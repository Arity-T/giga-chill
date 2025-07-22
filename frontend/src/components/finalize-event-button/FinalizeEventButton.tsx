import React from 'react';
import { Button, App } from 'antd';
import { CheckCircleOutlined } from '@ant-design/icons';
import {
    useFinalizeEventMutation,
    useGetTasksQuery,
    useGetShoppingListsQuery
} from '@/store/api';
import { TaskStatus, ShoppingListStatus } from '@/types/api';
import type { Event } from '@/store/api';

export interface FinalizeEventButtonProps {
    event: Event;
    onFinalized?: () => void;
}

export function FinalizeEventButton({ event, onFinalized }: FinalizeEventButtonProps) {
    const { modal, message } = App.useApp();
    const [finalizeEvent, { isLoading }] = useFinalizeEventMutation();
    const { data: tasks = [] } = useGetTasksQuery(event.event_id);
    const { data: shoppingLists = [] } = useGetShoppingListsQuery(event.event_id);

    const handleFinalizeEvent = () => {
        // Подсчитываем незавершенные задачи и списки
        const incompleteTasks = tasks.filter(task => task.status !== TaskStatus.COMPLETED);
        const incompleteShoppingLists = shoppingLists.filter(list =>
            ![ShoppingListStatus.BOUGHT, ShoppingListStatus.PARTIALLY_BOUGHT, ShoppingListStatus.CANCELLED].includes(list.status)
        );

        const incompleteTasksCount = incompleteTasks.length;
        const incompleteShoppingListsCount = incompleteShoppingLists.length;

        modal.confirm({
            title: 'Завершение мероприятия',
            content: (
                <div>
                    <p>Вы уверены, что хотите завершить мероприятие <strong>"{event.title}"</strong>?</p>
                    <p>После завершения будут произведены расчеты долгов между участниками.</p>

                    {incompleteTasksCount > 0 && (
                        <div>Незавершенных задач: <strong>{incompleteTasksCount}</strong></div>
                    )}
                    {incompleteShoppingListsCount > 0 && (
                        <div>Некупленных списков покупок: <strong>{incompleteShoppingListsCount}</strong></div>
                    )}

                    <p style={{ marginTop: '12px', marginBottom: 0 }}>
                        <strong>Это действие нельзя отменить.</strong>
                    </p>
                </div>
            ),
            okText: 'Да, завершить мероприятие',
            okType: 'primary',
            cancelText: 'Отмена',
            width: 520,
            onOk: async () => {
                try {
                    await finalizeEvent(event.event_id).unwrap();
                    message.success('Мероприятие завершено! Долги рассчитаны.');
                    onFinalized?.();
                } catch (error) {
                    console.error('Ошибка при завершении мероприятия:', error);
                    message.error('Ошибка при завершении мероприятия');
                }
            },
        });
    };

    return (
        <Button
            type="primary"
            icon={<CheckCircleOutlined />}
            onClick={handleFinalizeEvent}
            loading={isLoading}
            size="large"
        >
            Завершить мероприятие
        </Button>
    );
} 