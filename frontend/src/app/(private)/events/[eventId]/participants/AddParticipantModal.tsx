'use client';

import React from 'react';
import { Modal, Tabs, Form, Input, Button, Alert, Typography, Space, App, Spin } from 'antd';
import { UserAddOutlined, LinkOutlined, UserOutlined, CopyOutlined, ReloadOutlined } from '@ant-design/icons';
import { useAddParticipantMutation, useGetInvitationTokenQuery, useCreateInvitationTokenMutation } from '@/store/api';
import { APP_CONFIG } from '@/config/app.config';
import { PAGES } from '@/config/pages.config';
import { UserRole } from '@/store/api';

export interface AddParticipantModalProps {
    visible: boolean;
    onCancel: () => void;
    eventId: string;
    onSuccess: () => void;
    userRole?: UserRole;
}


export default function AddParticipantModal({ visible, onCancel, eventId, onSuccess, userRole }: AddParticipantModalProps) {
    const [form] = Form.useForm();
    const [addParticipant, { isLoading }] = useAddParticipantMutation();
    const { message } = App.useApp();

    // Хуки для работы с токенами приглашений
    const { data: tokenData, isLoading: isTokenLoading, refetch: refetchToken } = useGetInvitationTokenQuery(eventId, {
        skip: !visible,
    });
    const [createToken, { isLoading: isCreatingToken }] = useCreateInvitationTokenMutation();

    const handleSubmit = async (values: { login: string }) => {
        try {
            await addParticipant({
                eventId,
                participantCreate: {
                    login: values.login,
                },
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

    const handleCreateToken = async () => {
        try {
            await createToken(eventId).unwrap();
            message.success('Ссылка-приглашение создана');
            refetchToken();
        } catch (err: any) {
            message.error('Не удалось создать ссылку-приглашение');
        }
    };

    const handleCopyLink = () => {
        if (tokenData?.invitation_token) {
            const inviteLink = new URL(PAGES.JOIN_BY_INVITATION(tokenData.invitation_token), APP_CONFIG.BASE_URL).href;
            navigator.clipboard.writeText(inviteLink);
            message.success('Ссылка скопирована в буфер обмена');
        }
    };

    const isOwner = userRole === UserRole.Owner;

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
                <div style={{ padding: '16px 0' }}>
                    {isTokenLoading ? (
                        <div style={{ textAlign: 'center', padding: '32px' }}>
                            <Spin />
                        </div>
                    ) : !tokenData?.invitation_token ? (
                        // Если токена нет - показываем кнопку создания
                        <div style={{ textAlign: 'center', padding: '16px' }}>
                            <Typography.Title level={5} style={{ marginBottom: '16px' }}>
                                Ссылка-приглашение не создана
                            </Typography.Title>
                            <Typography.Text
                                type="secondary"
                                style={{ display: 'block', marginBottom: '24px' }}
                            >
                                Создайте ссылку-приглашение, чтобы пользователи могли присоединиться к мероприятию самостоятельно
                            </Typography.Text>
                            <Button
                                type="primary"
                                icon={<LinkOutlined />}
                                loading={isCreatingToken}
                                onClick={handleCreateToken}
                                size="large"
                            >
                                Создать ссылку-приглашение
                            </Button>
                        </div>
                    ) : (
                        // Если токен есть - показываем ссылку и кнопки
                        <div>
                            <div style={{
                                background: '#f5f5f5',
                                padding: '12px',
                                borderRadius: '6px',
                                marginBottom: '16px',
                                border: '1px solid #d9d9d9'
                            }}>
                                <Typography.Text code copyable={false} style={{ fontSize: '14px' }}>
                                    {new URL(PAGES.JOIN_BY_INVITATION(tokenData.invitation_token), APP_CONFIG.BASE_URL).href}
                                </Typography.Text>
                            </div>
                            <Space>
                                <Button
                                    type="primary"
                                    icon={<CopyOutlined />}
                                    onClick={handleCopyLink}
                                >
                                    Скопировать ссылку
                                </Button>
                                {isOwner && (
                                    <Button
                                        icon={<ReloadOutlined />}
                                        loading={isCreatingToken}
                                        onClick={handleCreateToken}
                                    >
                                        Создать новую ссылку
                                    </Button>
                                )}
                            </Space>
                            <Alert
                                message="Поделитесь этой ссылкой"
                                description="Пользователи смогут присоединиться к мероприятию, перейдя по этой ссылке"
                                type="info"
                                showIcon
                                style={{ marginTop: '16px' }}
                            />
                        </div>
                    )}
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