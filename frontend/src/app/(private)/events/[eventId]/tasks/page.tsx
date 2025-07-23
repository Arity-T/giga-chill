'use client';

import React, { useState, useMemo } from 'react';
import { Typography, Tabs, Button, Row, Col, Empty, Modal, App } from 'antd';
import { CheckSquareOutlined, PlusOutlined } from '@ant-design/icons';
import { EventIdPathParam } from '@/types/path-params';
import { TaskStatus, useGetParticipantsQuery, useGetShoppingListsQuery, useGetTasksQuery, useDeleteTaskMutation } from '@/store/api';
import type { Task } from '@/store/api';
import { getTaskStatusText, getAllTaskStatuses } from '@/utils/task-status-utils';
import TaskCard from './TaskCard';
import CreateTaskModal from './CreateTaskModal';
import TaskModal from './task-modal/TaskModal';

const { Title } = Typography;

export default function TasksPage({ params }: EventIdPathParam) {
    const { eventId } = React.use(params);
    const { message } = App.useApp();

    // Получаем задачи из API
    const { data: tasks = [], isLoading: isTasksLoading } = useGetTasksQuery(eventId);

    // Получаем участников мероприятия для выбора исполнителя
    const { data: participants = [] } = useGetParticipantsQuery(eventId);

    // Получаем списки покупок для выбора в задаче
    const { data: shoppingLists = [] } = useGetShoppingListsQuery(eventId);

    // Мутация для удаления задачи
    const [deleteTask] = useDeleteTaskMutation();

    const [activeStatusFilter, setActiveStatusFilter] = useState<TaskStatus | 'all'>('all');
    const [isModalOpen, setIsModalOpen] = useState(false);

    // Состояние для модального окна просмотра задачи
    const [isTaskModalOpen, setIsTaskModalOpen] = useState(false);
    const [selectedTaskId, setSelectedTaskId] = useState<string | null>(null);

    // Фильтрация задач по статусу
    const filteredTasks = useMemo(() => {
        if (activeStatusFilter === 'all') {
            return tasks;
        }
        return tasks.filter(task => task.status === activeStatusFilter);
    }, [tasks, activeStatusFilter]);

    // Создание табов для фильтрации
    const filterTabs = useMemo(() => {
        const allStatuses = getAllTaskStatuses();
        const statusCounts = tasks.reduce((acc, task) => {
            acc[task.status] = (acc[task.status] || 0) + 1;
            return acc;
        }, {} as Record<TaskStatus, number>);

        const tabs = [
            {
                key: 'all',
                label: `Все (${tasks.length})`,
            },
            ...allStatuses.map(status => ({
                key: status,
                label: `${getTaskStatusText(status)} (${statusCounts[status] || 0})`,
            }))
        ];

        return tabs;
    }, [tasks]);

    const handleCreateTask = () => {
        setIsModalOpen(true);
    };

    const handleDeleteTask = async (taskId: string) => {
        Modal.confirm({
            title: 'Удалить задачу?',
            content: 'Это действие нельзя отменить.',
            okText: 'Удалить',
            cancelText: 'Отмена',
            okType: 'danger',
            onOk: async () => {
                try {
                    await deleteTask({ eventId, taskId }).unwrap();
                    message.success('Задача удалена');
                } catch (error) {
                    message.error('Ошибка при удалении задачи');
                }
            },
        });
    };

    const handleModalCancel = () => {
        setIsModalOpen(false);
    };

    const handleTaskClick = (task: Task) => {
        setSelectedTaskId(task.task_id);
        setIsTaskModalOpen(true);
    };

    const handleTaskModalClose = () => {
        setIsTaskModalOpen(false);
        setSelectedTaskId(null);
    };

    return (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', width: '100%' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Title level={3} style={{ margin: 0 }}>
                    <CheckSquareOutlined style={{ marginRight: '8px' }} />
                    Задачи
                </Title>
                <Button
                    type="primary"
                    icon={<PlusOutlined />}
                    onClick={handleCreateTask}
                >
                    Создать задачу
                </Button>
            </div>

            <Tabs
                activeKey={activeStatusFilter}
                onChange={(key) => setActiveStatusFilter(key as TaskStatus | 'all')}
                items={filterTabs}
                size="large"
            />

            {filteredTasks.length === 0 ? (
                <Empty
                    description={
                        activeStatusFilter === 'all'
                            ? 'Задач пока нет'
                            : `Задач со статусом "${getTaskStatusText(activeStatusFilter as TaskStatus)}" нет`
                    }
                />
            ) : (
                <Row gutter={[16, 16]}>
                    {filteredTasks.map(task => (
                        <Col xs={24} sm={12} lg={8} xl={6} key={task.task_id}>
                            <TaskCard
                                task={task}
                                onClick={() => handleTaskClick(task)}
                            />
                        </Col>
                    ))}
                </Row>
            )}

            <CreateTaskModal
                open={isModalOpen}
                onCancel={handleModalCancel}
                participants={participants}
                shoppingLists={shoppingLists}
                eventId={eventId}
            />

            <TaskModal
                taskId={selectedTaskId}
                open={isTaskModalOpen}
                onClose={handleTaskModalClose}
                participants={participants}
                eventId={eventId}
            />
        </div>
    );
} 