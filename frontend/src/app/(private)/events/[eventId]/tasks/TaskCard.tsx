import React from 'react';
import { Card, Tag, Typography, Tooltip, Space } from 'antd';
import { CalendarOutlined, UserOutlined } from '@ant-design/icons';
import type { Task } from '@/store/api';
import { getTaskStatusText, getTaskStatusColor, getTaskStatusTooltip } from '@/utils/task-status-utils';
import { formatDate } from '@/utils/datetime-utils';
import styles from './TaskCard.module.css';

const { Title, Text } = Typography;

interface TaskCardProps {
    task: Task;
    onClick?: () => void;
}

export default function TaskCard({ task, onClick }: TaskCardProps) {
    // const isOverdue = new Date(task.deadline_datetime) < new Date() && task.status !== 'completed';
    const isOverdue = false;

    return (
        <Card
            hoverable
            className={styles.taskCard}
            styles={{ body: { padding: '16px' } }}
            onClick={onClick}
            style={{ cursor: onClick ? 'pointer' : 'default' }}
        >
            <div className={styles.cardContent}>
                <div className={styles.header}>
                    <Title level={4} style={{ margin: 0, fontSize: '16px' }}>
                        {task.title}
                    </Title>
                    <Tooltip title={getTaskStatusTooltip(task.status)}>
                        <Tag color={getTaskStatusColor(task.status)} className={styles.statusTag}>
                            {getTaskStatusText(task.status)}
                        </Tag>
                    </Tooltip>
                </div>

                <div className={styles.footer}>
                    <div className={styles.executor}>
                        <UserOutlined style={{ color: '#8c8c8c', marginRight: '4px' }} />
                        {task.executor ? (
                            <Space direction="vertical" size={0}>
                                <Text style={{ fontSize: '13px' }}>
                                    {task.executor.name}
                                </Text>
                            </Space>
                        ) : (
                            <Text type="secondary" style={{ fontSize: '13px' }}>
                                Не назначена
                            </Text>
                        )}
                    </div>

                    <div className={styles.deadline}>
                        <CalendarOutlined style={{ color: isOverdue ? '#ff4d4f' : '#8c8c8c', marginRight: '4px' }} />
                        <Text
                            style={{
                                fontSize: '13px',
                                color: isOverdue ? '#ff4d4f' : '#595959'
                            }}
                        >
                            {formatDate(task.deadline_datetime)}
                        </Text>
                    </div>
                </div>
            </div>
        </Card>
    );
} 