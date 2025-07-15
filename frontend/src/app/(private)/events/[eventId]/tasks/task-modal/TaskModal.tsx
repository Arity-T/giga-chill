'use client';

import React, { useState } from 'react';
import { Modal, Typography, Tag, Tooltip, Row, Col, App, Space } from 'antd';
import { EditOutlined } from '@ant-design/icons';
import { Task, User } from '@/types/api';
import { getTaskStatusText, getTaskStatusColor, getTaskStatusTooltip } from '@/utils/task-status-utils';
import TaskDescription from './TaskDescription';
import TaskExecutor from './TaskExecutor';
import TaskDeadline from './TaskDeadline';

const { Title } = Typography;

interface TaskModalProps {
    task: Task | null;
    open: boolean;
    onClose: () => void;
    participants: User[];
    onUpdateTask: (taskId: string, updates: Partial<Task>) => void;
}

export default function TaskModal({
    task,
    open,
    onClose,
    participants,
    onUpdateTask
}: TaskModalProps) {
    const { message } = App.useApp();
    const [isUpdating, setIsUpdating] = useState(false);

    if (!task) return null;

    const handleUpdate = async (field: string, value: any) => {
        if (!task.can_edit) {
            message.warning('У вас нет прав для редактирования этой задачи');
            return;
        }

        // Проверяем, изменились ли данные
        const currentValue = (task as any)[field];
        if (value === currentValue) {
            return; // Данные не изменились, ничего не делаем
        }

        setIsUpdating(true);
        try {
            const updates: Partial<Task> = { [field]: value };
            onUpdateTask(task.task_id, updates);
            message.success('Задача обновлена');
        } catch (error) {
            message.error('Ошибка при обновлении задачи');
        } finally {
            setIsUpdating(false);
        }
    };

    const handleUpdateDescription = async (description: string) => {
        await handleUpdate('description', description);
    };

    const handleUpdateExecutor = async (executor: User | null) => {
        await handleUpdate('executor', executor);
    };

    const handleUpdateDeadline = async (deadline: string) => {
        await handleUpdate('deadline_datetime', deadline);
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
                    editable={task.can_edit ? {
                        icon: <EditOutlined style={{ color: '#8c8c8c' }} />,
                        onChange: (value) => handleUpdate('title', value),
                        tooltip: 'Нажмите для редактирования',
                        triggerType: ['icon', 'text']
                    } : false}
                    style={{ margin: 0, flex: 1, marginBottom: '24px' }}
                >
                    {task.title}
                </Title>


                {/* Основная информация */}
                <Row gutter={[24, 16]} style={{ marginBottom: '24px' }}>
                    <Col span={12}>
                        <TaskExecutor
                            executor={task.executor}
                            canEdit={task.can_edit}
                            participants={participants}
                            onUpdate={handleUpdateExecutor}
                        />
                    </Col>

                    <Col span={12}>
                        <TaskDeadline
                            deadlineDateTime={task.deadline_datetime}
                            canEdit={task.can_edit}
                            onUpdate={handleUpdateDeadline}
                        />
                    </Col>
                </Row>

                {/* Описание */}
                <TaskDescription
                    description={task.description}
                    canEdit={task.can_edit}
                    onUpdate={handleUpdateDescription}
                />


                {/* Автор */}
                <div style={{ marginTop: '24px', paddingTop: '16px', borderTop: '1px solid #f0f0f0' }}>
                    <Space>
                        <span style={{ color: '#8c8c8c' }}>Автор:</span>
                        <span>{task.author.name} (@{task.author.login})</span>
                    </Space>
                </div>

                {!task.can_edit && (
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
        </Modal>
    );
} 