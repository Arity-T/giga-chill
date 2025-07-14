'use client';

import React from 'react';
import { Modal, Tabs, Form, Input, Button, Alert, Typography, Space, App } from 'antd';
import { UserAddOutlined, LinkOutlined, UserOutlined } from '@ant-design/icons';
import { useAddParticipantMutation } from '@/store/api';

export interface AddParticipantModalProps {
    visible: boolean;
    onCancel: () => void;
    eventId: string;
    onSuccess: () => void;
}

export default function AddParticipantModal({ visible, onCancel, eventId, onSuccess }: AddParticipantModalProps) {
    const [form] = Form.useForm();
    const [addParticipant, { isLoading }] = useAddParticipantMutation();
    const { message } = App.useApp();

    const handleSubmit = async (values: { login: string }) => {
        try {
            await addParticipant({
                eventId,
                login: values.login,
            }).unwrap();
            onSuccess();
            onCancel();
            form.resetFields();
        } catch (err: any) {
            // Обработка разных типов ошибок по статусу
            if (err?.status === 404) {
                message.error('Пользователь с таким логином не найден');
            } else if (err?.status === 409) {
                message.error('Пользователь уже является участником мероприятия');
            } else {
                message.error('Не удалось добавить участника');
            }
        }
    };

    const handleCancel = () => {
        form.resetFields();
        onCancel();
    };

    const tabItems = [
        {
            key: 'login',
            label: (
                <span>
                    <UserOutlined style={{ marginRight: '8px' }} />
                    По логину
                </span>
            ),
            children: (
                <div style={{ padding: '16px 0' }}>
                    <Form
                        form={form}
                        layout="vertical"
                        onFinish={handleSubmit}
                        autoComplete="off"
                    >
                        <Form.Item
                            name="login"
                            rules={[
                                { required: true, message: 'Пожалуйста, введите логин!' },
                                { min: 2, message: 'Логин должен содержать минимум 2 символа' },
                            ]}
                            style={{ marginBottom: '28px' }}
                        >
                            <Input
                                placeholder="Введите логин пользователя"
                                autoFocus
                            />
                        </Form.Item>

                        <Form.Item style={{ marginBottom: 0 }}>
                            <Space>
                                <Button
                                    type="primary"
                                    htmlType="submit"
                                    loading={isLoading}
                                    icon={<UserAddOutlined />}
                                >
                                    Добавить участника
                                </Button>
                                <Button onClick={handleCancel}>
                                    Отмена
                                </Button>
                            </Space>
                        </Form.Item>
                    </Form>
                </div>
            ),
        },
        {
            key: 'invite-link',
            label: (
                <span>
                    <LinkOutlined style={{ marginRight: '8px' }} />
                    По ссылке-приглашению
                </span>
            ),
            children: (
                <div style={{ padding: '32px 16px', textAlign: 'center' }}>
                    <Alert
                        message="В разработке"
                        description="Функция добавления участников по ссылке-приглашению находится в процессе разработки и скоро будет доступна."
                        type="info"
                        showIcon
                    />
                </div>
            ),
        },
    ];

    return (
        <Modal
            title={
                <span>
                    <UserAddOutlined style={{ marginRight: '8px' }} />
                    Добавить участника
                </span>
            }
            open={visible}
            onCancel={handleCancel}
            footer={null}
            width={500}
            destroyOnHidden
        >
            <Tabs
                defaultActiveKey="login"
                items={tabItems}
                size="large"
            />
        </Modal>
    );
} 