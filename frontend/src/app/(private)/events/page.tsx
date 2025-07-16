'use client';

import { Row, Col, Typography, Button, Flex, Empty } from "antd";
import { PlusOutlined } from '@ant-design/icons';
import { useState } from "react";
import styles from "./page.module.css";
import { useGetEventsQuery } from "@/store/api";
import CreateEventModal from "./CreateEventModal";
import EventCard from "./EventCard";

const { Title } = Typography;

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
                {events && events.length > 0 ? (
                    events.map((event) => (
                        <Col xs={24} sm={12} lg={8} key={event.event_id}>
                            <EventCard event={event} />
                        </Col>
                    ))
                ) : (
                    <Col span={24}>
                        <Empty
                            description="Нет мероприятий"
                            image={Empty.PRESENTED_IMAGE_SIMPLE}
                        />
                    </Col>
                )}
            </Row>

            <CreateEventModal
                open={createModalOpen}
                onCancel={() => setCreateModalOpen(false)}
            />
        </div>
    );
}
