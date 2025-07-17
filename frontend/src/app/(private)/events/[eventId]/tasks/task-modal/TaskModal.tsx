'use client';

import React, { useState } from 'react';
import { Modal, Typography, Tag, Tooltip, Row, Col, App, Space, Spin, Button, Popconfirm } from 'antd';
import { EditOutlined, ExclamationCircleOutlined } from '@ant-design/icons';
import { User, TaskRequest, TaskStatus } from '@/types/api';
import { useGetTaskQuery, useUpdateTaskMutation, useDeleteTaskMutation, useAssignTaskMutation, useAssignShoppingListsMutation, useGetShoppingListsQuery, useTakeTaskInWorkMutation, useGetMeQuery, useGetEventQuery } from '@/store/api';
import { getTaskStatusText, getTaskStatusColor, getTaskStatusTooltip } from '@/utils/task-status-utils';
import TaskDescription from './TaskDescription';
import TaskExecutor from './TaskExecutor';
import TaskDeadline from './TaskDeadline';
import TaskShoppingLists from './TaskShoppingLists';

const { Title } = Typography;

interface TaskModalProps {
    taskId: string | null;
    open: boolean;
    onClose: () => void;
    participants: User[];
    eventId: string;
}

export default function TaskModal({
    taskId,
    open,
    onClose,
    participants,
    eventId
}: TaskModalProps) {
    const { message, modal } = App.useApp();

    // Получаем информацию о текущем пользователе
    const { data: currentUser } = useGetMeQuery();

    // Получаем информацию о событии
    const { data: event } = useGetEventQuery(eventId);

    // Состояние для раскрытых списков покупок (для исполнителя)
    const [expandedListId, setExpandedListId] = useState<string>('');

    // Получаем полную информацию о задаче
    const { data: task, isLoading } = useGetTaskQuery(
        { eventId, taskId: taskId! },
        { skip: !taskId || !open }
    );

    const [updateTask, { isLoading: isUpdating }] = useUpdateTaskMutation();
    const [deleteTask, { isLoading: isDeleting }] = useDeleteTaskMutation();
    const [assignTask, { isLoading: isAssigning }] = useAssignTaskMutation();
    const [assignShoppingLists, { isLoading: isAssigningLists }] = useAssignShoppingListsMutation();
    const [takeTaskInWork, { isLoading: isTakingInWork }] = useTakeTaskInWorkMutation();

    // Получаем все списки покупок для события
    const { data: allShoppingLists = [] } = useGetShoppingListsQuery(eventId);

    // Проверяем, является ли текущий пользователь исполнителем задачи в статусе in_progress
    const isExecutorInProgress = task?.status === TaskStatus.IN_PROGRESS &&
        currentUser?.id === task?.executor?.id;

    const handleToggleExpand = (listId: string) => {
        setExpandedListId(listId);
    };

    const handleUpdate = async (field: string, value: any) => {
        if (!task?.permissions.can_edit) {
            message.warning('У вас нет прав для редактирования этой задачи');
            return;
        }

        // Проверяем, изменились ли данные
        const currentValue = (task as any)[field];
        if (value === currentValue) {
            return; // Данные не изменились, ничего не делаем
        }

        try {
            const updates: Partial<TaskRequest> = {
                [field]: value,
                title: task.title,
                description: task.description,
                deadline_datetime: task.deadline_datetime,
                executor_id: task.executor?.id || null,
                shopping_lists_ids: task.shopping_lists?.map(list => list.shopping_list_id) || []
            };

            // Обновляем конкретное поле
            if (field === 'deadline_datetime') {
                updates.deadline_datetime = value;
            } else {
                updates[field as keyof TaskRequest] = value;
            }

            await updateTask({
                eventId,
                taskId: task.task_id,
                task: updates as TaskRequest
            }).unwrap();

            message.success('Задача обновлена');
        } catch (error) {
            message.error('Ошибка при обновлении задачи');
        }
    };

    const handleDelete = async () => {
        if (!task?.permissions.can_edit) {
            message.warning('У вас нет прав для удаления этой задачи');
            return;
        }

        modal.confirm({
            title: 'Удалить задачу?',
            content: (
                <div>
                    <p>
                        Вы уверены, что хотите удалить задачу <strong>{task.title}</strong>?
                    </p>
                    <p>Это действие нельзя отменить.</p>
                </div>
            ),
            icon: <ExclamationCircleOutlined />,
            okText: 'Удалить',
            okType: 'danger',
            cancelText: 'Отмена',
            onOk: async () => {
                try {
                    await deleteTask({
                        eventId,
                        taskId: task.task_id
                    }).unwrap();

                    message.success('Задача удалена');
                    onClose();
                } catch (error) {
                    message.error('Ошибка при удалении задачи');
                }
            },
        });
    };

    const handleUpdateDescription = async (description: string) => {
        await handleUpdate('description', description);
    };

    const handleUpdateExecutor = async (executor: User | null) => {
        if (!task?.permissions.can_edit) {
            message.warning('У вас нет прав для редактирования этой задачи');
            return;
        }

        // Проверяем, изменился ли исполнитель
        const currentExecutorId = task.executor?.id || null;
        const newExecutorId = executor?.id || null;
        if (newExecutorId === currentExecutorId) {
            return; // Исполнитель не изменился, ничего не делаем
        }

        try {
            await assignTask({
                eventId,
                taskId: task.task_id,
                executorData: { executor_id: newExecutorId }
            }).unwrap();

            message.success('Исполнитель обновлен');
        } catch (error) {
            message.error('Ошибка при обновлении исполнителя');
        }
    };

    const handleUpdateDeadline = async (deadline: string) => {
        await handleUpdate('deadline_datetime', deadline);
    };

    const handleUpdateShoppingLists = async (shoppingListIds: string[]) => {
        if (!task?.permissions.can_edit) {
            message.warning('У вас нет прав для редактирования этой задачи');
            return;
        }

        try {
            await assignShoppingLists({
                eventId,
                taskId: task.task_id,
                shoppingListIds
            }).unwrap();

            message.success('Списки покупок обновлены');
        } catch (error) {
            message.error('Ошибка при обновлении списков покупок');
        }
    };

    const handleTakeInWork = async () => {
        if (!task) return;

        try {
            await takeTaskInWork({
                eventId,
                taskId: task.task_id
            }).unwrap();

            message.success('Задача взята в работу');
        } catch (error) {
            message.error('Ошибка при взятии задачи в работу');
        }
    };

    return (
        <Modal
            title={null}
            open={open}
            onCancel={onClose}
            footer={null}
            width={800}
            styles={{ body: { padding: '24px' } }}
        >
            {isLoading ? (
                <div style={{ display: 'flex', justifyContent: 'center', padding: '40px' }}>
                    <Spin size="large" />
                </div>
            ) : task ? (
                <div style={{ marginBottom: '24px' }}>
                    {/* Заголовок и статус */}
                    <div style={{ position: 'relative', height: '12px' }}>
                        <Tooltip title={getTaskStatusTooltip(task.status)}>
                            <Tag color={getTaskStatusColor(task.status)} style={{ position: 'absolute', top: -20 }}>
                                {getTaskStatusText(task.status)}
                            </Tag>
                        </Tooltip>
                    </div>

                    <Title
                        level={2}
                        editable={task.permissions.can_edit ? {
                            icon: null,
                            onChange: (value) => handleUpdate('title', value),
                            tooltip: 'Нажмите для редактирования',
                            triggerType: ['text']
                        } : false}
                        style={{ margin: 0, flex: 1, marginBottom: '24px' }}
                    >
                        {task.title}
                    </Title>

                    {/* Основная информация */}
                    <Row gutter={[24, 16]} style={{ marginBottom: '24px' }}>
                        <Col span={8}>
                            <TaskExecutor
                                executor={task.executor}
                                canEdit={task.permissions.can_edit}
                                participants={participants}
                                onUpdate={handleUpdateExecutor}
                            />
                        </Col>

                        <Col span={8} offset={4}>
                            <TaskDeadline
                                deadlineDateTime={task.deadline_datetime}
                                canEdit={task.permissions.can_edit}
                                onUpdate={handleUpdateDeadline}
                                eventEndDateTime={event?.end_datetime}
                            />
                        </Col>
                    </Row>

                    {/* Описание */}
                    <TaskDescription
                        description={task.description}
                        canEdit={task.permissions.can_edit}
                        onUpdate={handleUpdateDescription}
                    />

                    {/* Списки покупок */}
                    <TaskShoppingLists
                        shoppingLists={task.shopping_lists || []}
                        allShoppingLists={allShoppingLists}
                        canEdit={task.permissions.can_edit && task.status === TaskStatus.OPEN}
                        onUpdate={handleUpdateShoppingLists}
                        isExecutorInProgress={isExecutorInProgress}
                        eventId={eventId}
                        expandedListId={expandedListId}
                        onToggleExpand={handleToggleExpand}
                        task={task}
                    />

                    {/* Кнопка "Взять в работу" */}
                    {task.permissions.can_take_in_work && (
                        <div style={{ marginTop: '24px', marginBottom: '16px' }}>
                            <Button
                                type="primary"
                                loading={isTakingInWork}
                                onClick={handleTakeInWork}
                                style={{ width: '100%' }}
                            >
                                Взять в работу
                            </Button>
                        </div>
                    )}

                    {/* Автор */}
                    <div style={{ marginTop: '24px', paddingTop: '16px', borderTop: '1px solid #f0f0f0' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                            <Space>
                                <span style={{ color: '#8c8c8c' }}>Автор:</span>
                                <span>{task.author.name} (@{task.author.login})</span>
                            </Space>

                            {task.permissions.can_edit && (
                                <span
                                    onClick={handleDelete}
                                    style={{
                                        color: '#8c8c8c',
                                        cursor: 'pointer',
                                        fontSize: '14px',
                                        userSelect: 'none'
                                    }}
                                >
                                    Удалить задачу
                                </span>
                            )}
                        </div>
                    </div>

                    {!task.permissions.can_edit && (
                        <div style={{
                            marginTop: '16px',
                            padding: '12px',
                            backgroundColor: '#f6f6f6',
                            borderRadius: '6px',
                            fontSize: '13px',
                            color: '#8c8c8c'
                        }}>
                            <EditOutlined style={{ marginRight: '6px' }} />
                            У вас нет прав для редактирования этой задачи
                        </div>
                    )}
                </div>
            ) : null}
        </Modal>
    );
} 