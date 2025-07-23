'use client';

import React, { useState } from 'react';
import { Typography, Input, Button, App, Space, Row, Col } from 'antd';
import { MessageOutlined, CheckCircleOutlined, CloseCircleOutlined } from '@ant-design/icons';
import { useReviewTaskMutation } from '@/store/api';
import type { User } from '@/store/api';

const { Title, Paragraph } = Typography;
const { TextArea } = Input;

interface ReviewTaskFormProps {
    eventId: string;
    taskId: string;
    executorComment?: string;
    executor?: User;
    onSuccess?: () => void;
}

export default function ReviewTaskForm({
    eventId,
    taskId,
    executorComment,
    executor,
    onSuccess
}: ReviewTaskFormProps) {
    const { message } = App.useApp();
    const [reviewerComment, setReviewerComment] = useState('');
    const [reviewTask, { isLoading }] = useReviewTaskMutation();

    const handleReview = async (isApproved: boolean) => {
        if (!reviewerComment.trim()) {
            message.warning('Пожалуйста, добавьте комментарий к проверке');
            return;
        }

        try {
            await reviewTask({
                eventId,
                taskId,
                taskReviewRequest: {
                    reviewer_comment: reviewerComment.trim(),
                    is_approved: isApproved
                }
            }).unwrap();

            message.success(isApproved ? 'Задача подтверждена' : 'Задача отправлена назад в работу');
            setReviewerComment('');
            onSuccess?.();
        } catch (error) {
            message.error('Ошибка при проверке задачи');
        }
    };

    return (
        <div style={{ marginTop: '24px', marginBottom: '16px' }}>
            {/* Комментарий исполнителя */}
            <div style={{ marginBottom: '24px' }}>
                <div style={{ display: 'flex', alignItems: 'center', marginBottom: '12px' }}>
                    <MessageOutlined style={{ color: '#8c8c8c', marginRight: '8px' }} />
                    <Title level={5} style={{ margin: 0 }}>
                        Комментарий исполнителя {executor ? `(${executor.name} @${executor.login})` : ''}
                    </Title>
                </div>

                <Paragraph
                    style={{
                        minHeight: '69px',
                        backgroundColor: '#fafafa',
                        padding: '12px',
                        borderRadius: '6px',
                        margin: 0,
                        whiteSpace: 'pre-wrap'
                    }}
                >
                    {executorComment || 'Комментарий исполнителя отсутствует'}
                </Paragraph>
            </div>

            {/* Комментарий проверяющего */}
            <div style={{ marginBottom: '16px' }}>
                <div style={{ display: 'flex', alignItems: 'center', marginBottom: '12px' }}>
                    <CheckCircleOutlined style={{ color: '#8c8c8c', marginRight: '8px' }} />
                    <Title level={5} style={{ margin: 0 }}>
                        Комментарий
                    </Title>
                </div>

                <TextArea
                    value={reviewerComment}
                    onChange={(e) => setReviewerComment(e.target.value)}
                    placeholder="Добавьте комментарий к проверке..."
                    rows={4}
                    style={{ marginBottom: '16px' }}
                />
            </div>

            {/* Кнопки */}
            <Row gutter={12}>
                <Col span={12}>
                    <Button
                        type="default"
                        loading={isLoading}
                        onClick={() => handleReview(false)}
                        style={{ width: '100%' }}
                        disabled={!reviewerComment.trim()}
                        icon={<CloseCircleOutlined />}
                    >
                        Отправить назад в работу
                    </Button>
                </Col>
                <Col span={12}>
                    <Button
                        type="primary"
                        loading={isLoading}
                        onClick={() => handleReview(true)}
                        style={{ width: '100%' }}
                        disabled={!reviewerComment.trim()}
                        icon={<CheckCircleOutlined />}
                    >
                        Подтвердить выполнение
                    </Button>
                </Col>
            </Row>
        </div>
    );
} 