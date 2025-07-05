'use client';

import { Card, Tag, Row, Col, Typography, Space } from "antd";
import { CalendarOutlined, EnvironmentOutlined, UserOutlined } from '@ant-design/icons';
import styles from "./page.module.css";
import { useGetEventsQuery } from "@/store/api/api";
import { formatDateTime } from "@/utils/datetime-utils";
import { getRoleColor, getRoleText } from "@/utils/role-utils";

const { Title, Text } = Typography;

export default function EventsPage() {
    const { data: events } = useGetEventsQuery();

    return (
        <div className={styles.page}>
            <Title level={2}>Мероприятия</Title>

            <Row gutter={[16, 16]}>
                {events?.map((event) => (
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

                                {!!event.description && (
                                    <Text type="secondary" ellipsis>
                                        {event.description}
                                    </Text>
                                )}

                                {!!event.budget && (
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
