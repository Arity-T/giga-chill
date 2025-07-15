'use client';

import React, { useState, useMemo } from 'react';
import { Typography, Tabs, Button, Row, Col, Empty, Modal, App } from 'antd';
import { CheckSquareOutlined, PlusOutlined } from '@ant-design/icons';
import { EventIdPathParam } from '@/types/path-params';
import { useGetEventParticipantsQuery, useGetShoppingListsQuery } from '@/store/api';
import { Task, TaskRequest, TaskStatus, User } from '@/types/api';
import { mockTasks, mockCurrentUser } from '@/data/tasks.data';
import { getTaskStatusText, getAllTaskStatuses } from '@/utils/task-status-utils';
import TaskCard from './TaskCard';
import CreateTaskModal from './CreateTaskModal';
import TaskModal from './task-modal/TaskModal';

const { Title } = Typography;

export default function TasksPage({ params }: EventIdPathParam) {
    const { eventId } = React.use(params);
    const { message } = App.useApp();

    // Получаем участников мероприятия для выбора исполнителя
    const { data: participants = [] } = useGetEventParticipantsQuery(eventId);

    // Получаем списки покупок для выбора в задаче
    const { data: shoppingLists = [] } = useGetShoppingListsQuery(eventId);

    // Локальное состояние для моковых данных
    const [tasks, setTasks] = useState<Task[]>(mockTasks);
    const [activeStatusFilter, setActiveStatusFilter] = useState<TaskStatus | 'all'>('all');
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingTask, setEditingTask] = useState<Task | null>(null);

    // Состояние для модального окна просмотра задачи
    const [isTaskModalOpen, setIsTaskModalOpen] = useState(false);
    const [selectedTask, setSelectedTask] = useState<Task | null>(null);

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
        setEditingTask(null);
        setIsModalOpen(true);
    };

    const handleEditTask = (task: Task) => {
        setEditingTask(task);
        setIsModalOpen(true);
    };

    const handleDeleteTask = (taskId: string) => {
        Modal.confirm({
            title: 'Удалить задачу?',
            content: 'Это действие нельзя отменить.',
            okText: 'Удалить',
            cancelText: 'Отмена',
            okType: 'danger',
            onOk: () => {
                setTasks(prev => prev.filter(task => task.task_id !== taskId));
                message.success('Задача удалена');
            },
        });
    };

    const handleModalSubmit = (taskData: TaskRequest) => {
        if (editingTask) {
            // Редактирование существующей задачи
            setTasks(prev => prev.map(task =>
                task.task_id === editingTask.task_id
                    ? {
                        ...task,
                        title: taskData.title,
                        description: taskData.description,
                        deadline_datetime: taskData.deadline_datetime,
                        executor: participants.find(p => p.id === taskData.executor_id) || task.executor,
                    }
                    : task
            ));
            message.success('Задача обновлена');
        } else {
            // Создание новой задачи
            const newTask: Task = {
                task_id: Date.now().toString(),
                title: taskData.title,
                description: taskData.description,
                status: TaskStatus.OPEN,
                deadline_datetime: taskData.deadline_datetime,
                actual_approval_id: '',
                author: mockCurrentUser,
                executor: participants.find(p => p.id === taskData.executor_id) || mockCurrentUser,
                can_edit: true,
            };
            setTasks(prev => [newTask, ...prev]);
            message.success('Задача создана');
        }
        setIsModalOpen(false);
        setEditingTask(null);
    };

    const handleModalCancel = () => {
        setIsModalOpen(false);
        setEditingTask(null);
    };

    const handleTaskClick = (task: Task) => {
        setSelectedTask(task);
        setIsTaskModalOpen(true);
    };

    const handleTaskModalClose = () => {
        setIsTaskModalOpen(false);
        setSelectedTask(null);
    };

    const handleUpdateTask = (taskId: string, updates: Partial<Task>) => {
        setTasks(prev => prev.map(task =>
            task.task_id === taskId
                ? { ...task, ...updates }
                : task
        ));

        // Обновляем selectedTask если она открыта
        if (selectedTask && selectedTask.task_id === taskId) {
            setSelectedTask(prev => prev ? { ...prev, ...updates } : null);
        }
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
                onSubmit={handleModalSubmit}
                participants={participants}
                shoppingLists={shoppingLists}
            />

            <TaskModal
                task={selectedTask}
                open={isTaskModalOpen}
                onClose={handleTaskModalClose}
                participants={participants}
                onUpdateTask={handleUpdateTask}
            />
        </div>
    );
} 