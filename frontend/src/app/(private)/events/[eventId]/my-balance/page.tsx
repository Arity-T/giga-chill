'use client';

import React from 'react';
import { Typography, Alert, Card, List, Statistic, Row, Col, Spin, Badge } from 'antd';
import { DollarOutlined, ArrowDownOutlined, ArrowUpOutlined } from '@ant-design/icons';
import type { EventIdPathParam } from '@/types/path-params';
import { useGetEventQuery, useGetMyBalanceQuery } from '@/store/api';
import { UserRole } from '@/store/api';
import { FinalizeEventButton } from '@/components/finalize-event-button';

const { Title, Text } = Typography;

export default function BalancePage({ params }: EventIdPathParam) {
    const { eventId } = React.use(params);

    const { data: event, isLoading: isEventLoading } = useGetEventQuery(eventId);
    const { data: balance, isLoading: isBalanceLoading, error: balanceError } = useGetMyBalanceQuery(eventId, {
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
                    <DollarOutlined style={{ marginRight: '8px' }} />
                    Мой баланс
                </Title>

                <Alert
                    message="Мероприятие ещё не завершено"
                    description="Расчёт долгов произойдёт после завершения мероприятия. Организатор может завершить мероприятие, когда все задачи будут выполнены и покупки сделаны."
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
    if (isBalanceLoading) {
        return (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', width: '100%' }}>
                <Title level={3} style={{ margin: 0 }}>
                    <DollarOutlined style={{ marginRight: '8px' }} />
                    Мой баланс
                </Title>
                <div style={{ display: 'flex', justifyContent: 'center', padding: '48px' }}>
                    <Spin size="large" />
                </div>
            </div>
        );
    }

    // Если ошибка при загрузке баланса
    if (balanceError) {
        return (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', width: '100%' }}>
                <Title level={3} style={{ margin: 0 }}>
                    <DollarOutlined style={{ marginRight: '8px' }} />
                    Мой баланс
                </Title>
                <Alert
                    message="Ошибка при загрузке баланса"
                    description="Не удалось загрузить данные о балансе. Попробуйте обновить страницу."
                    type="error"
                    showIcon
                />
            </div>
        );
    }

    if (!balance) {
        return (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', width: '100%' }}>
                <Title level={3} style={{ margin: 0 }}>
                    <DollarOutlined style={{ marginRight: '8px' }} />
                    Мой баланс
                </Title>
                <Alert
                    message="Данные не найдены"
                    type="warning"
                    showIcon
                />
            </div>
        );
    }

    // Подсчитываем общие суммы
    const totalDebt = balance.my_debts.reduce((sum, debt) => sum + debt.amount, 0);
    const totalOwed = balance.debts_to_me.reduce((sum, debt) => sum + debt.amount, 0);
    const netBalance = totalOwed - totalDebt;

    return (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', width: '100%' }}>
            <Title level={3} style={{ margin: 0 }}>
                <DollarOutlined style={{ marginRight: '8px' }} />
                Мой баланс
            </Title>

            {/* Общий баланс */}
            <Row gutter={[16, 16]}>
                <Col xs={24} sm={8}>
                    <Card>
                        <Statistic
                            title={
                                <Text strong>
                                    Мои долги
                                </Text>
                            }
                            value={totalDebt}
                            suffix=" ₽"
                            valueStyle={{ color: totalDebt > 0 ? '#cf1322' : '#8c8c8c' }}
                            prefix={totalDebt > 0 ? <ArrowDownOutlined /> : undefined}
                        />
                    </Card>
                </Col>
                <Col xs={24} sm={8}>
                    <Card>
                        <Statistic
                            title={
                                <Text strong>
                                    Должны мне
                                </Text>
                            }
                            value={totalOwed}
                            suffix=" ₽"
                            valueStyle={{ color: totalOwed > 0 ? '#3f8600' : '#8c8c8c' }}
                            prefix={totalOwed > 0 ? <ArrowUpOutlined /> : undefined}
                        />
                    </Card>
                </Col>
                <Col xs={24} sm={8}>
                    <Card>
                        <Statistic
                            title={
                                <Text strong>
                                    Итоговый баланс
                                </Text>
                            }
                            value={netBalance}
                            suffix=" ₽"
                            valueStyle={{
                                color: netBalance > 0 ? '#3f8600' : netBalance < 0 ? '#cf1322' : '#8c8c8c'
                            }}
                            prefix={netBalance > 0 ? <ArrowUpOutlined /> : netBalance < 0 ? <ArrowDownOutlined /> : undefined}
                        />
                    </Card>
                </Col>
            </Row>

            {/* Детализация долгов */}
            <Row gutter={[16, 16]}>
                {/* Мои долги */}
                <Col xs={24} lg={12}>
                    <Card
                        title={
                            <span>
                                Мои долги{' '}
                                <Badge
                                    count={balance.my_debts.length}
                                    style={{ backgroundColor: '#cf1322', marginLeft: '8px' }}
                                    showZero
                                />
                            </span>
                        }
                        size="small"
                    >
                        {balance.my_debts.length === 0 ? (
                            <Text type="secondary">У вас нет долгов</Text>
                        ) : (
                            <List
                                dataSource={balance.my_debts}
                                renderItem={(debt) => (
                                    <List.Item>
                                        <div>
                                            <Text>{debt.user.name}</Text>{' '}
                                            <Text type="secondary">(@{debt.user.login})</Text>
                                        </div>
                                        <Text strong>
                                            - {debt.amount.toLocaleString('ru-RU')} ₽
                                        </Text>
                                    </List.Item>
                                )}
                            />
                        )}
                    </Card>
                </Col>

                {/* Должны мне */}
                <Col xs={24} lg={12}>
                    <Card
                        title={
                            <span>
                                Должны мне{' '}
                                <Badge
                                    count={balance.debts_to_me.length}
                                    style={{ backgroundColor: '#3f8600', marginLeft: '8px' }}
                                    showZero
                                />
                            </span>
                        }
                        size="small"
                    >
                        {balance.debts_to_me.length === 0 ? (
                            <Text type="secondary">Никто вам не должен</Text>
                        ) : (
                            <List
                                dataSource={balance.debts_to_me}
                                renderItem={(debt) => (
                                    <List.Item>
                                        <div>
                                            <Text>{debt.user.name}</Text>{' '}
                                            <Text type="secondary">(@{debt.user.login})</Text>
                                        </div>
                                        <Text strong>
                                            + {debt.amount.toLocaleString('ru-RU')} ₽
                                        </Text>
                                    </List.Item>
                                )}
                            />
                        )}
                    </Card>
                </Col>
            </Row>
        </div>
    );
} 