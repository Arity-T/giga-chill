'use client';

import React from 'react';
import { Typography, Alert, Spin } from 'antd';
import { TeamOutlined } from '@ant-design/icons';
import { EventIdPathParam } from '@/types/path-params';
import { useGetEventQuery, useGetMeQuery } from '@/store/api/api';
import { UserRole, UserInEvent } from '@/types/api';
import ParticipantTable from './ParticipantTable';

const { Title } = Typography;

// Моковые данные для демонстрации
const mockParticipants: UserInEvent[] = [
    {
        id: '1',
        login: 'owner_user',
        name: 'Владелец Мероприятия',
        user_role: UserRole.OWNER,
        balance: 0,
    },
    {
        id: '2',
        login: 'admin_user',
        name: 'Администратор Петров',
        user_role: UserRole.ADMIN,
        balance: -150,
    },
    {
        id: '1108dddd-b3e5-4b2d-87de-6d8ef392a1d7',
        login: 'participant1',
        name: 'Участник Иванов',
        user_role: UserRole.PARTICIPANT,
        balance: 300,
    },
    {
        id: '4',
        login: 'participant2',
        name: 'Участница Сидорова',
        user_role: UserRole.PARTICIPANT,
        balance: -75,
    },
    {
        id: '5',
        login: 'participant3',
        name: 'Участник Козлов',
        user_role: UserRole.PARTICIPANT,
        balance: 0,
    },
];

export default function ParticipantsPage({ params }: EventIdPathParam) {
    const { eventId } = React.use(params);

    // Получаем информацию о мероприятии и текущем пользователе
    const { data: event, isLoading: eventLoading, error: eventError } = useGetEventQuery(eventId);
    const { data: currentUser, isLoading: userLoading, error: userError } = useGetMeQuery();

    // В будущем будем использовать реальные данные:
    // const { data: participants, isLoading: participantsLoading } = useGetEventParticipantsQuery(eventId);
    // Пока используем моковые данные
    const participants = mockParticipants;
    const participantsLoading = false;

    // Показываем спиннер пока загружаются данные
    if (eventLoading || userLoading || participantsLoading) {
        return (
            <div style={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                height: '200px'
            }}>
                <Spin size="large" />
            </div>
        );
    }

    // Показываем ошибку если что-то пошло не так
    if (eventError || userError) {
        return (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', width: '100%' }}>
                <Title level={3} style={{ margin: 0 }}>
                    <TeamOutlined style={{ marginRight: '8px' }} />
                    Участники
                </Title>
                <Alert
                    message="Ошибка загрузки"
                    description="Не удалось загрузить информацию о мероприятии или текущем пользователе"
                    type="error"
                    showIcon
                />
            </div>
        );
    }

    // Проверяем что все данные загружены
    if (!event || !currentUser) {
        return (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', width: '100%' }}>
                <Title level={3} style={{ margin: 0 }}>
                    <TeamOutlined style={{ marginRight: '8px' }} />
                    Участники
                </Title>
                <Alert
                    message="Данные недоступны"
                    description="Информация о мероприятии или пользователе недоступна"
                    type="warning"
                    showIcon
                />
            </div>
        );
    }

    return (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', width: '100%' }}>
            <Title level={3} style={{ margin: 0 }}>
                <TeamOutlined style={{ marginRight: '8px' }} />
                Участники
            </Title>

            <ParticipantTable
                participants={participants}
                event={event}
                currentUser={currentUser}
                isLoading={participantsLoading}
            />
        </div>
    );
} 