'use client';

import React, { useState } from 'react';
import { Typography, Alert, Spin, App, Button, Flex } from 'antd';
import { TeamOutlined, UserAddOutlined } from '@ant-design/icons';
import type { EventIdPathParam } from '@/types/path-params';
import {
    UserRole,
    useGetEventQuery,
    useGetMeQuery,
    useGetParticipantsQuery,
    useDeleteParticipantMutation,
    useSetParticipantRoleMutation
} from '@/store/api';
import type { Participant } from '@/store/api';
import ParticipantTable from './ParticipantTable';
import AddParticipantModal from './AddParticipantModal';

const { Title } = Typography;

export default function ParticipantsPage({ params }: EventIdPathParam) {
    const { eventId } = React.use(params);
    const { message } = App.useApp();
    const [isAddModalVisible, setIsAddModalVisible] = useState(false);

    // Получаем информацию о мероприятии, текущем пользователе и участниках
    const { data: event, isLoading: eventLoading, error: eventError } = useGetEventQuery(eventId);
    const { data: currentUser, isLoading: userLoading, error: userError } = useGetMeQuery();
    const {
        data: participants,
        isLoading: participantsLoading,
        isFetching: participantsFetching,
        error: participantsError
    } = useGetParticipantsQuery(eventId);

    // Мутации для работы с участниками
    const [deleteParticipant, { isLoading: isDeleting }] = useDeleteParticipantMutation();
    const [setParticipantRole, { isLoading: isUpdatingRole }] = useSetParticipantRoleMutation();

    // Обработчики
    const handleRoleChange = async (participant: Participant, newRole: UserRole) => {
        try {
            await setParticipantRole({
                eventId,
                participantId: participant.id,
                participantSetRole: {
                    role: newRole,
                },
            }).unwrap();
            message.success(`Роль пользователя "${participant.name}" успешно изменена!`);
        } catch (error) {
            message.error('Не удалось изменить роль участника');
            console.error('Error updating participant role:', error);
        }
    };

    const handleDeleteParticipant = async (participant: Participant) => {
        try {
            await deleteParticipant({
                eventId,
                participantId: participant.id,
            }).unwrap();
            message.success(`Участник "${participant.name}" удален из мероприятия`);
        } catch (error) {
            message.error('Не удалось удалить участника');
            console.error('Error deleting participant:', error);
        }
    };

    const handleAddParticipantSuccess = () => {
        message.success('Участник успешно добавлен в мероприятие!');
    };

    // Проверяем, может ли пользователь добавлять участников (owner или admin)
    const canAddParticipants = event?.user_role === UserRole.Owner || event?.user_role === UserRole.Admin;

    // Показываем спиннер пока загружаются данные или идет обновление/удаление
    const isLoadingOrFetching = eventLoading || userLoading || participantsLoading || participantsFetching || isUpdatingRole || isDeleting;

    // Определяем содержимое
    let content;

    if (isLoadingOrFetching) {
        content = (
            <div style={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                height: '200px'
            }}>
                <Spin size="large" />
            </div>
        );
    } else if (eventError || userError || participantsError || !event || !currentUser || !participants) {
        content = (
            <Alert
                message="Ошибка загрузки"
                description="Не удалось загрузить информацию о мероприятии"
                type="error"
                showIcon
            />
        );
    } else {
        content = (
            <ParticipantTable
                participants={participants}
                event={event}
                currentUser={currentUser}
                onRoleChange={handleRoleChange}
                onDeleteParticipant={handleDeleteParticipant}
            />
        );
    }

    return (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', width: '100%' }}>
            <Flex justify="space-between" align="center">
                <Title level={3} style={{ margin: 0 }}>
                    <TeamOutlined style={{ marginRight: '8px' }} />
                    Участники
                </Title>

                {canAddParticipants && (
                    <Button
                        type="primary"
                        icon={<UserAddOutlined />}
                        onClick={() => setIsAddModalVisible(true)}
                    >
                        Добавить участника
                    </Button>
                )}
            </Flex>

            {content}

            <AddParticipantModal
                visible={isAddModalVisible}
                onCancel={() => setIsAddModalVisible(false)}
                eventId={eventId}
                onSuccess={handleAddParticipantSuccess}
                userRole={event?.user_role}
            />
        </div>
    );
} 