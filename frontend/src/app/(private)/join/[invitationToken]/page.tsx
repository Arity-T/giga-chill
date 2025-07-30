'use client';

import React, { useEffect, useState, useRef } from 'react';
import { useRouter } from 'next/navigation';
import { Card, Spin, Typography, Button, Space, App } from 'antd';
import { TeamOutlined, CheckCircleOutlined, ExclamationCircleOutlined } from '@ant-design/icons';
import { useJoinByInvitationTokenMutation } from '@/store/api';
import type { InvitationTokenPathParam } from '@/types/path-params';
import { PAGES } from '@/config/pages.config';

const { Title, Text } = Typography;

export default function JoinByInvitationPage({ params }: InvitationTokenPathParam) {
    const { invitationToken } = React.use(params);
    const router = useRouter();
    const { message } = App.useApp();

    const [joinStatus, setJoinStatus] = useState<'loading' | 'success' | 'error'>('loading');
    const [errorMessage, setErrorMessage] = useState<string>('');
    const [eventId, setEventId] = useState<string>('');

    // Защита от повторного выполнения запроса
    const hasJoined = useRef(false);

    const [joinByInvitationToken] = useJoinByInvitationTokenMutation();

    useEffect(() => {
        // Если запрос уже был выполнен, не выполняем его повторно
        if (hasJoined.current) {
            return;
        }

        const handleJoin = async () => {
            hasJoined.current = true;

            try {
                const result = await joinByInvitationToken({ invitation_token: invitationToken }).unwrap();

                setEventId(result.event_id);
                setJoinStatus('success');

                message.success('Вы успешно присоединились к мероприятию!');

                // Перенаправляем на страницу мероприятия через 2 секунды
                setTimeout(() => {
                    router.push(PAGES.EVENT_DETAILS(result.event_id));
                }, 2000);

            } catch (err: any) {
                setJoinStatus('error');

                // Обрабатываем различные типы ошибок
                let errorMsg = '';
                if (err?.status === 404) {
                    errorMsg = 'Ссылка-приглашение недействительна или истекла';
                } else if (err?.status === 409) {
                    errorMsg = 'Вы уже являетесь участником этого мероприятия';
                } else if (err?.status === 401) {
                    errorMsg = 'Необходимо войти в систему для присоединения к мероприятию';
                } else {
                    errorMsg = 'Не удалось присоединиться к мероприятию. Попробуйте еще раз';
                }

                setErrorMessage(errorMsg);
                message.error(errorMsg);
            }
        };

        handleJoin();
    }, [invitationToken, joinByInvitationToken, router, message]);

    const handleRetry = async () => {
        setJoinStatus('loading');
        setErrorMessage('');
        hasJoined.current = false;

        try {
            const result = await joinByInvitationToken({ invitation_token: invitationToken }).unwrap();

            setEventId(result.event_id);
            setJoinStatus('success');

            message.success('Вы успешно присоединились к мероприятию!');

            setTimeout(() => {
                router.push(PAGES.EVENT_DETAILS(result.event_id));
            }, 2000);

        } catch (err: any) {
            hasJoined.current = true; // Предотвращаем повторные попытки
            setJoinStatus('error');

            let errorMsg = '';
            if (err?.status === 404) {
                errorMsg = 'Ссылка-приглашение недействительна или истекла';
            } else if (err?.status === 409) {
                errorMsg = 'Вы уже являетесь участником этого мероприятия';
            } else if (err?.status === 401) {
                errorMsg = 'Необходимо войти в систему для присоединения к мероприятию';
            } else {
                errorMsg = 'Не удалось присоединиться к мероприятию. Попробуйте еще раз';
            }

            setErrorMessage(errorMsg);
            message.error(errorMsg);
        }
    };

    const handleGoToEvent = () => {
        if (eventId) {
            router.push(PAGES.EVENT_DETAILS(eventId));
        } else {
            router.push(PAGES.EVENTS);
        }
    };

    const handleGoToEvents = () => {
        router.push(PAGES.EVENTS);
    };

    return (
        <div style={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            minHeight: '60vh',
            padding: '24px'
        }}>
            <Card
                style={{
                    maxWidth: '500px',
                    width: '100%',
                    textAlign: 'center'
                }}
            >
                {joinStatus === 'loading' && (
                    <div>
                        <Spin size="large" style={{ marginBottom: '24px' }} />
                        <Title level={3}>
                            <TeamOutlined style={{ marginRight: '8px', color: '#8c8c8c' }} />
                            Присоединение к мероприятию...
                        </Title>
                        <Text type="secondary">
                            Пожалуйста, подождите, мы обрабатываем ваш запрос
                        </Text>
                    </div>
                )}

                {joinStatus === 'success' && (
                    <div>
                        <CheckCircleOutlined
                            style={{
                                fontSize: '48px',
                                color: '#52c41a',
                                marginBottom: '24px'
                            }}
                        />
                        <Title level={3} style={{ color: '#52c41a' }}>
                            Успешно присоединились!
                        </Title>
                        <Text type="secondary" style={{ display: 'block', marginBottom: '24px' }}>
                            Вы успешно присоединились к мероприятию.
                            Через несколько секунд вы будете перенаправлены на страницу мероприятия.
                        </Text>
                        <Button
                            type="primary"
                            onClick={handleGoToEvent}
                            size="large"
                        >
                            Перейти к мероприятию
                        </Button>
                    </div>
                )}

                {joinStatus === 'error' && (
                    <div>
                        <ExclamationCircleOutlined
                            style={{
                                fontSize: '48px',
                                color: '#ff4d4f',
                                marginBottom: '24px'
                            }}
                        />
                        <Title level={3} style={{ color: '#ff4d4f' }}>
                            Ошибка присоединения
                        </Title>
                        <Text type="secondary" style={{ display: 'block', marginBottom: '24px' }}>
                            {errorMessage}
                        </Text>
                        <Space>
                            <Button
                                type="primary"
                                onClick={handleRetry}
                                size="large"
                            >
                                Попробовать еще раз
                            </Button>
                            <Button
                                onClick={handleGoToEvents}
                                size="large"
                            >
                                К мероприятиям
                            </Button>
                        </Space>
                    </div>
                )}
            </Card>
        </div>
    );
}
