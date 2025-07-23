'use client';

import React, { useState } from 'react';
import { Typography, Alert, Table, Tag, Spin, List, Badge, Divider } from 'antd';
import { CalculatorOutlined, ArrowUpOutlined, ArrowDownOutlined } from '@ant-design/icons';
import type { EventIdPathParam } from '@/types/path-params';
import { UserRole, useGetEventQuery, useGetBalanceSummaryQuery } from '@/store/api';
import type { ParticipantBalanceSummary } from '@/store/api';
import { FinalizeEventButton } from '@/components/finalize-event-button';
import type { ColumnsType } from 'antd/es/table';

const { Title, Text } = Typography;

export default function BalanceSummaryPage({ params }: EventIdPathParam) {
    const { eventId } = React.use(params);
    const [expandedRowKeys, setExpandedRowKeys] = useState<React.Key[]>([]);

    const { data: event, isLoading: isEventLoading } = useGetEventQuery(eventId);
    const { data: balanceSummary, isLoading: isSummaryLoading, error: summaryError } = useGetBalanceSummaryQuery(eventId, {
        skip: !event?.is_finalized
    });

    if (isEventLoading) {
        return (
            <div style={{ display: 'flex', justifyContent: 'center', padding: '48px' }}>
                <Spin size="large" />
            </div>
        );
    }

    if (!event) {
        return (
            <Alert
                message="Мероприятие не найдено"
                type="error"
                showIcon
            />
        );
    }

    const isOwner = event.user_role === UserRole.Owner;

    // Если мероприятие не завершено
    if (!event.is_finalized) {
        return (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', width: '100%' }}>
                <Title level={3} style={{ margin: 0 }}>
                    <CalculatorOutlined style={{ marginRight: '8px' }} />
                    Общие расчёты
                </Title>

                <Alert
                    message="Мероприятие ещё не завершено"
                    description="Общие расчёты долгов станут доступны после завершения мероприятия. Только организатор может завершить мероприятие."
                    type="info"
                    showIcon
                />

                {isOwner && (
                    <div style={{ marginTop: '24px' }}>
                        <FinalizeEventButton event={event} />
                    </div>
                )}
            </div>
        );
    }

    // Если мероприятие завершено, но данные загружаются
    if (isSummaryLoading) {
        return (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', width: '100%' }}>
                <Title level={3} style={{ margin: 0 }}>
                    <CalculatorOutlined style={{ marginRight: '8px' }} />
                    Общие расчёты
                </Title>
                <div style={{ display: 'flex', justifyContent: 'center', padding: '48px' }}>
                    <Spin size="large" />
                </div>
            </div>
        );
    }

    // Если ошибка при загрузке
    if (summaryError) {
        return (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', width: '100%' }}>
                <Title level={3} style={{ margin: 0 }}>
                    <CalculatorOutlined style={{ marginRight: '8px' }} />
                    Общие расчёты
                </Title>
                <Alert
                    message="Ошибка при загрузке данных"
                    description="Не удалось загрузить сводку по балансу. Попробуйте обновить страницу."
                    type="error"
                    showIcon
                />
            </div>
        );
    }

    if (!balanceSummary || balanceSummary.length === 0) {
        return (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', width: '100%' }}>
                <Title level={3} style={{ margin: 0 }}>
                    <CalculatorOutlined style={{ marginRight: '8px' }} />
                    Общие расчёты
                </Title>
                <Alert
                    message="Нет данных для отображения"
                    description="Участники мероприятия не найдены или балансы не рассчитаны."
                    type="warning"
                    showIcon
                />
            </div>
        );
    }

    // Колонки для основной таблицы
    const columns: ColumnsType<ParticipantBalanceSummary> = [
        {
            title: 'Участник',
            dataIndex: 'user',
            key: 'user',
            render: (user) => (
                <div>
                    <Text strong>{user.name}</Text>{' '}
                    <Text type="secondary">(@{user.login})</Text>
                </div>
            ),
        },
        {
            title: 'Общий баланс',
            dataIndex: 'total_balance',
            key: 'total_balance',
            render: (balance: number) => (
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    {balance > 0 ? (
                        <ArrowUpOutlined style={{ color: '#3f8600' }} />
                    ) : balance < 0 ? (
                        <ArrowDownOutlined style={{ color: '#cf1322' }} />
                    ) : null}
                    <Text strong>
                        {balance.toLocaleString('ru-RU')} ₽
                    </Text>
                </div>
            ),
            sorter: (a, b) => a.total_balance - b.total_balance,
            sortDirections: ['descend', 'ascend'],
        },
        {
            title: 'Статус',
            dataIndex: 'total_balance',
            key: 'status',
            render: (balance: number) => {
                if (balance > 0) {
                    return <Tag color="green">Кредитор</Tag>;
                } else if (balance < 0) {
                    return <Tag color="red">Должник</Tag>;
                } else {
                    return <Tag color="default">Квиты</Tag>;
                }
            },
            filters: [
                { text: 'Кредиторы', value: 'creditor' },
                { text: 'Должники', value: 'debtor' },
                { text: 'Квиты', value: 'even' },
            ],
            onFilter: (value, record) => {
                if (value === 'creditor') return record.total_balance > 0;
                if (value === 'debtor') return record.total_balance < 0;
                if (value === 'even') return record.total_balance === 0;
                return true;
            },
        },
    ];

    // Компонент для раскрываемого содержимого
    const expandedRowRender = (record: ParticipantBalanceSummary) => {
        const { user_balance } = record;

        return (
            <div style={{ padding: '16px 0' }}>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr auto 1fr', gap: '32px', alignItems: 'start' }}>
                    {/* Долги участника */}
                    <div>
                        <Title level={5} style={{ marginBottom: '12px', marginTop: '0px' }}>
                            Долги участника{' '}
                            <Badge
                                count={user_balance.my_debts.length}
                                style={{ backgroundColor: '#cf1322' }}
                                showZero
                            />
                        </Title>
                        {user_balance.my_debts.length === 0 ? (
                            <Text type="secondary">Нет долгов</Text>
                        ) : (
                            <List
                                size="small"
                                dataSource={user_balance.my_debts}
                                renderItem={(debt) => (
                                    <List.Item>
                                        <div>
                                            <Text strong>{debt.user.name}</Text>{' '}
                                            <Text type="secondary">(@{debt.user.login})</Text>
                                        </div>
                                        <Text strong>
                                            - {debt.amount.toLocaleString('ru-RU')} ₽
                                        </Text>
                                    </List.Item>
                                )}
                            />
                        )}
                    </div>

                    {/* Визуальный разделитель */}
                    <Divider
                        type="vertical"
                        style={{
                            height: '100%',
                            minHeight: '60px',
                            margin: '0'
                        }}
                    />

                    {/* Должны участнику */}
                    <div>
                        <Title level={5} style={{ marginBottom: '12px', marginTop: '0px' }}>
                            Должны участнику{' '}
                            <Badge
                                count={user_balance.debts_to_me.length}
                                style={{ backgroundColor: '#3f8600' }}
                                showZero
                            />
                        </Title>
                        {user_balance.debts_to_me.length === 0 ? (
                            <Text type="secondary">Никто не должен</Text>
                        ) : (
                            <List
                                size="small"
                                dataSource={user_balance.debts_to_me}
                                renderItem={(debt) => (
                                    <List.Item>
                                        <div>
                                            <Text strong>{debt.user.name}</Text>{' '}
                                            <Text type="secondary">(@{debt.user.login})</Text>
                                        </div>
                                        <Text strong>
                                            + {debt.amount.toLocaleString('ru-RU')} ₽
                                        </Text>
                                    </List.Item>
                                )}
                            />
                        )}
                    </div>
                </div>
            </div>
        );
    };

    return (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', width: '100%' }}>
            <Title level={3} style={{ margin: 0 }}>
                <CalculatorOutlined style={{ marginRight: '8px' }} />
                Общие расчёты
            </Title>

            <Table
                columns={columns}
                dataSource={balanceSummary}
                rowKey={(record) => record.user.id}
                expandable={{
                    expandedRowRender,
                    rowExpandable: () => true,
                    expandedRowKeys,
                    onExpand: (expanded, record) => {
                        if (expanded) {
                            // Разрешаем раскрыть только одну строку за раз
                            setExpandedRowKeys([record.user.id]);
                        } else {
                            // Закрываем все строки
                            setExpandedRowKeys([]);
                        }
                    },
                }}
                pagination={false}
                scroll={{ x: 600 }}
            />

        </div>
    );
} 