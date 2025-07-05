'use client';

import { Card, Tag, Row, Col, Typography, Space } from "antd";
import { CalendarOutlined, EnvironmentOutlined, UserOutlined } from '@ant-design/icons';
import styles from "./page.module.css";

const { Title, Text } = Typography;

// Временные данные для демонстрации
const mockEvents = [
    {
        event_id: "1",
        title: "Корпоративная вечеринка",
        location: "Офис компании",
        start_datetime: "2024-01-15T18:00:00",
        end_datetime: "2024-01-15T22:00:00",
        description: "Новогодняя корпоративная вечеринка с развлечениями",
        budget: 50000,
        user_role: "ORGANIZER"
    },
    {
        event_id: "2",
        title: "День рождения коллеги",
        location: "Ресторан 'Космос'",
        start_datetime: "2024-01-20T19:00:00",
        end_datetime: "2024-01-20T23:00:00",
        description: "Празднование дня рождения в уютном ресторане",
        budget: 25000,
        user_role: "PARTICIPANT"
    },
    {
        event_id: "3",
        title: "Тимбилдинг на природе",
        location: "Загородный клуб 'Лесная сказка'",
        start_datetime: "2024-01-25T10:00:00",
        end_datetime: "2024-01-25T18:00:00",
        description: "Активный отдых на свежем воздухе с командными играми",
        budget: 75000,
        user_role: "ORGANIZER"
    }
];

const getRoleColor = (role: string) => {
    switch (role) {
        case 'ORGANIZER':
            return 'blue';
        case 'PARTICIPANT':
            return 'green';
        default:
            return 'default';
    }
};

const getRoleText = (role: string) => {
    switch (role) {
        case 'ORGANIZER':
            return 'Организатор';
        case 'PARTICIPANT':
            return 'Участник';
        default:
            return role;
    }
};

const formatDateTime = (dateTimeStr: string) => {
    const date = new Date(dateTimeStr);
    return date.toLocaleString('ru-RU', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
};

export default function EventsPage() {
    return (
        <div className={styles.page}>
            <Title level={2}>Мероприятия</Title>

            <Row gutter={[16, 16]}>
                {mockEvents.map((event) => (
                    <Col xs={24} sm={12} lg={8} key={event.event_id}>
                        <Card
                            title={event.title}
                            extra={
                                <Tag
                                    color={getRoleColor(event.user_role)}
                                    icon={<UserOutlined />}
                                >
                                    {getRoleText(event.user_role)}
                                </Tag>
                            }
                            hoverable
                        >
                            <Space direction="vertical" size="small" style={{ width: '100%' }}>
                                <div>
                                    <EnvironmentOutlined style={{ marginRight: 8, color: '#1890ff' }} />
                                    <Text>{event.location}</Text>
                                </div>

                                <div>
                                    <CalendarOutlined style={{ marginRight: 8, color: '#52c41a' }} />
                                    <Text>
                                        {formatDateTime(event.start_datetime)} — {formatDateTime(event.end_datetime)}
                                    </Text>
                                </div>

                                {event.description && (
                                    <Text type="secondary" ellipsis>
                                        {event.description}
                                    </Text>
                                )}

                                {event.budget && (
                                    <Text strong>
                                        Бюджет: {event.budget.toLocaleString('ru-RU')} ₽
                                    </Text>
                                )}
                            </Space>
                        </Card>
                    </Col>
                ))}
            </Row>
        </div>
    );
}
