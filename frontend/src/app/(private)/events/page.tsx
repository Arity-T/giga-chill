'use client';

import { Card, Tag, Row, Col, Typography, Space, Button, Flex } from "antd";
import { CalendarOutlined, EnvironmentOutlined, UserOutlined, PlusOutlined } from '@ant-design/icons';
import { useState } from "react";
import styles from "./page.module.css";
import { useGetEventsQuery } from "@/store/api/api";
import { formatDateTime } from "@/utils/datetime-utils";
import { getRoleColor, getRoleText } from "@/utils/role-utils";
import CreateEventModal from "./CreateEventModal";

const { Title, Text } = Typography;

export default function EventsPage() {
    const { data: events } = useGetEventsQuery();
    const [createModalOpen, setCreateModalOpen] = useState(false);

    return (
        <div className={styles.page}>
            <Flex justify="space-between" align="center" style={{ marginBottom: 24 }}>
                <Title level={2} style={{ margin: 0 }}>Мероприятия</Title>
                <Button
                    type="primary"
                    icon={<PlusOutlined />}
                    onClick={() => setCreateModalOpen(true)}
                >
                    Создать
                </Button>
            </Flex>

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

            <CreateEventModal
                open={createModalOpen}
                onCancel={() => setCreateModalOpen(false)}
            />
        </div>
    );
}
