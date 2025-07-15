'use client';

import React, { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { Card, Spin, Alert, Typography, Button, Space } from 'antd';
import { TeamOutlined, CheckCircleOutlined, ExclamationCircleOutlined } from '@ant-design/icons';
import { useJoinByInvitationTokenMutation, useGetEventsQuery } from '@/store/api';
import { InvitationTokenPathParam } from '@/types/path-params';
import { PAGES } from '@/config/pages.config';

const { Title, Text } = Typography;

export default function JoinByInvitationPage({ params }: InvitationTokenPathParam) {
    const { invitationToken } = React.use(params);
    const router = useRouter();
    const [joinStatus, setJoinStatus] = useState<'loading' | 'success' | 'error'>('loading');
    const [errorMessage, setErrorMessage] = useState<string>('');
    const [eventId, setEventId] = useState<string | null>(null);

    const [joinByInvitationToken] = useJoinByInvitationTokenMutation();
    const { data: events, refetch: refetchEvents } = useGetEventsQuery();

    useEffect(() => {
        const handleJoin = async () => {
            try {
                await joinByInvitationToken(invitationToken).unwrap();

                // После успешного присоединения обновляем список мероприятий
                // чтобы найти ID нового мероприятия
                await refetchEvents();
                setJoinStatus('success');

                // Перенаправляем на страницу мероприятий через 2 секунды
                setTimeout(() => {
                    router.push(PAGES.EVENTS);
                }, 2000);

            } catch (err: any) {
                setJoinStatus('error');

                // Обрабатываем различные типы ошибок
                if (err?.status === 404) {
                    setErrorMessage('Ссылка-приглашение недействительна или истекла');
                } else if (err?.status === 409) {
                    setErrorMessage('Вы уже являетесь участником этого мероприятия');
                } else if (err?.status === 401) {
                    setErrorMessage('Необходимо войти в систему для присоединения к мероприятию');
                } else {
                    setErrorMessage('Не удалось присоединиться к мероприятию. Попробуйте еще раз');
                }
            }
        };

        handleJoin();
    }, [invitationToken, joinByInvitationToken, refetchEvents, router]);

    const handleRetry = () => {
        setJoinStatus('loading');
        setErrorMessage('');
        // Повторить попытку присоединения
        const retryJoin = async () => {
            try {
                await joinByInvitationToken(invitationToken).unwrap();
                await refetchEvents();
                setJoinStatus('success');
                setTimeout(() => {
                    router.push(PAGES.EVENTS);
                }, 2000);
            } catch (err: any) {
                setJoinStatus('error');
                if (err?.status === 404) {
                    setErrorMessage('Ссылка-приглашение недействительна или истекла');
                } else if (err?.status === 409) {
                    setErrorMessage('Вы уже являетесь участником этого мероприятия');
                } else if (err?.status === 401) {
                    setErrorMessage('Необходимо войти в систему для присоединения к мероприятию');
                } else {
                    setErrorMessage('Не удалось присоединиться к мероприятию. Попробуйте еще раз');
                }
            }
        };
        retryJoin();
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
                            <TeamOutlined style={{ marginRight: '8px' }} />
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
                            Через несколько секунд вы будете перенаправлены на страницу мероприятий.
                        </Text>
                        <Button
                            type="primary"
                            onClick={handleGoToEvents}
                            size="large"
                        >
                            Перейти к мероприятиям
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
