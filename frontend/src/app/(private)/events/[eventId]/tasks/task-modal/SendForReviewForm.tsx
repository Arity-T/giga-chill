'use client';

import React, { useState } from 'react';
import { Typography, Input, Button, App } from 'antd';
import { CheckCircleOutlined } from '@ant-design/icons';
import { useSendTaskForReviewMutation } from '@/store/api';

const { Title } = Typography;
const { TextArea } = Input;

interface SendForReviewFormProps {
    eventId: string;
    taskId: string;
    onSuccess?: () => void;
}

export default function SendForReviewForm({
    eventId,
    taskId,
    onSuccess
}: SendForReviewFormProps) {
    const { message } = App.useApp();
    const [comment, setComment] = useState('');
    const [sendForReview, { isLoading }] = useSendTaskForReviewMutation();

    const handleSendForReview = async () => {
        if (!comment.trim()) {
            message.warning('Пожалуйста, добавьте комментарий о выполненной работе');
            return;
        }

        try {
            await sendForReview({
                eventId,
                taskId,
                reviewData: { executor_comment: comment.trim() }
            }).unwrap();

            message.success('Задача отправлена на проверку');
            setComment('');
            onSuccess?.();
        } catch (error) {
            message.error('Ошибка при отправке задачи на проверку');
        }
    };

    return (
        <div style={{ marginTop: '24px', marginBottom: '16px' }}>
            <div style={{ display: 'flex', alignItems: 'center', marginBottom: '12px' }}>
                <CheckCircleOutlined style={{ color: '#8c8c8c', marginRight: '8px' }} />
                <Title level={5} style={{ margin: 0 }}>
                    Отправить на проверку
                </Title>
            </div>

            <div style={{ marginBottom: '16px' }}>
                <TextArea
                    value={comment}
                    onChange={(e) => setComment(e.target.value)}
                    placeholder="Опишите выполненную работу, результаты и другие важные детали..."
                    rows={4}
                    style={{ marginBottom: '12px' }}
                />
            </div>

            <Button
                type="primary"
                loading={isLoading}
                onClick={handleSendForReview}
                style={{ width: '100%' }}
                disabled={!comment.trim()}
            >
                На проверку
            </Button>
        </div>
    );
} 