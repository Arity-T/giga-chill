import React, { useState, useMemo, useEffect } from 'react';
import { Modal, Input, Checkbox, List, Typography, Space, App } from 'antd';
import { SearchOutlined } from '@ant-design/icons';
import { useGetEventParticipantsQuery, useSetShoppingListConsumersMutation } from '@/store/api';
import { UserInEvent } from '@/types/api';

const { Text } = Typography;

interface AddConsumersModalProps {
    open: boolean;
    onCancel: () => void;
    eventId: string;
    shoppingListId: string;
    currentConsumers: UserInEvent[];
}

export default function AddConsumersModal({
    open,
    onCancel,
    eventId,
    shoppingListId,
    currentConsumers
}: AddConsumersModalProps) {
    const [searchText, setSearchText] = useState('');
    const [selectedConsumerIds, setSelectedConsumerIds] = useState<string[]>(
        currentConsumers.map(consumer => consumer.id)
    );
    const { message } = App.useApp();

    // Обновляем selectedConsumerIds при изменении currentConsumers
    useEffect(() => {
        setSelectedConsumerIds(currentConsumers.map(consumer => consumer.id));
    }, [currentConsumers]);

    const { data: participants = [], isLoading } = useGetEventParticipantsQuery(eventId);
    const [setShoppingListConsumers, { isLoading: isUpdating }] = useSetShoppingListConsumersMutation();

    // Фильтрация участников по поисковому запросу
    const filteredParticipants = useMemo(() => {
        if (!searchText.trim()) return participants;

        const searchLower = searchText.toLowerCase().trim();
        return participants.filter(participant =>
            participant.name.toLowerCase().includes(searchLower) ||
            participant.login.toLowerCase().includes(searchLower)
        );
    }, [participants, searchText]);

    const handleConsumerToggle = (userId: string) => {
        setSelectedConsumerIds(prev =>
            prev.includes(userId)
                ? prev.filter(id => id !== userId)
                : [...prev, userId]
        );
    };

    const handleSelectAll = () => {
        const allFilteredIds = filteredParticipants.map(p => p.id);
        const allSelected = allFilteredIds.every(id => selectedConsumerIds.includes(id));

        if (allSelected) {
            // Убираем всех отфильтрованных участников
            setSelectedConsumerIds(prev => prev.filter(id => !allFilteredIds.includes(id)));
        } else {
            // Добавляем всех отфильтрованных участников
            setSelectedConsumerIds(prev => {
                const newIds = allFilteredIds.filter(id => !prev.includes(id));
                return [...prev, ...newIds];
            });
        }
    };

    const handleSubmit = async () => {
        try {
            await setShoppingListConsumers({
                eventId,
                shoppingListId,
                consumers: selectedConsumerIds
            }).unwrap();

            message.success('Потребители успешно обновлены');
            onCancel();
        } catch (error) {
            message.error('Не удалось обновить потребителей');
            console.error('Ошибка при обновлении потребителей:', error);
        }
    };

    const handleCancel = () => {
        // Сбрасываем состояние к изначальным потребителям
        setSelectedConsumerIds(currentConsumers.map(consumer => consumer.id));
        setSearchText('');
        onCancel();
    };

    const allFilteredSelected = filteredParticipants.length > 0 &&
        filteredParticipants.every(p => selectedConsumerIds.includes(p.id));
    const someFilteredSelected = filteredParticipants.some(p => selectedConsumerIds.includes(p.id));

    // Проверяем, что выбран хотя бы один потребитель
    const hasSelectedConsumers = selectedConsumerIds.length > 0;

    return (
        <Modal
            title="Выбрать потребителей"
            open={open}
            onCancel={handleCancel}
            onOk={handleSubmit}
            okText="Сохранить"
            cancelText="Отмена"
            confirmLoading={isUpdating}
            okButtonProps={{ disabled: !hasSelectedConsumers }}
            width={600}
            styles={{
                body: { maxHeight: '60vh', padding: '16px 0' }
            }}
        >
            <Space direction="vertical" style={{ width: '100%' }} size="middle">
                <Input
                    placeholder="Поиск по имени или логину..."
                    prefix={<SearchOutlined style={{ color: '#8c8c8c' }} />}
                    value={searchText}
                    onChange={(e) => setSearchText(e.target.value)}
                    allowClear
                />

                {filteredParticipants.length > 0 && (
                    <div style={{ padding: '0 24px' }}>
                        <Checkbox
                            indeterminate={someFilteredSelected && !allFilteredSelected}
                            checked={allFilteredSelected}
                            onChange={handleSelectAll}
                        >
                            <Text strong>
                                {allFilteredSelected ? 'Снять выделение со всех' : 'Выбрать всех'}
                                {searchText ? ' (в результатах поиска)' : ''}
                            </Text>
                        </Checkbox>
                    </div>
                )}

                <div style={{ maxHeight: '400px', overflowY: 'auto' }}>
                    <List
                        loading={isLoading}
                        dataSource={filteredParticipants}
                        locale={{ emptyText: 'Участники не найдены' }}
                        renderItem={(participant) => (
                            <List.Item
                                style={{
                                    padding: '8px 24px',
                                    cursor: 'pointer',
                                    transition: 'background-color 0.2s ease'
                                }}
                                onClick={() => handleConsumerToggle(participant.id)}
                                onMouseEnter={(e) => {
                                    e.currentTarget.style.backgroundColor = '#f5f5f5';
                                }}
                                onMouseLeave={(e) => {
                                    e.currentTarget.style.backgroundColor = 'transparent';
                                }}
                            >
                                <Space>
                                    <Checkbox
                                        checked={selectedConsumerIds.includes(participant.id)}
                                        onChange={() => handleConsumerToggle(participant.id)}
                                        onClick={(e) => e.stopPropagation()}
                                    />
                                    <div>
                                        <Text strong>{participant.name}</Text>
                                        <Text type="secondary" style={{ fontSize: '12px', marginLeft: '8px' }}>
                                            @{participant.login}
                                        </Text>
                                    </div>
                                </Space>
                            </List.Item>
                        )}
                    />
                </div>

                <div style={{ padding: '0 24px', borderTop: '1px solid #f0f0f0', paddingTop: '12px' }}>
                    <Text type="secondary">
                        Выбрано: {selectedConsumerIds.length} из {participants.length} участников
                    </Text>
                    {!hasSelectedConsumers && (
                        <div style={{ marginTop: '4px' }}>
                            <Text type="warning" style={{ fontSize: '12px' }}>
                                Выберите хотя бы одного потребителя
                            </Text>
                        </div>
                    )}
                </div>
            </Space>
        </Modal>
    );
} 