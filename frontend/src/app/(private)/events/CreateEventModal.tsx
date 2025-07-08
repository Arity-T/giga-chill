'use client';

import React from 'react';
import { Modal, Form, Input, DatePicker, Button, App } from 'antd';
import { useCreateEventMutation } from '@/store/api/api';
import type { CreateEventRequest } from '@/types/api';
import dayjs from 'dayjs';

const { TextArea } = Input;
const { RangePicker } = DatePicker;

interface CreateEventModalProps {
    open: boolean;
    onCancel: () => void;
}

interface CreateEventFormData {
    title: string;
    location: string;
    dateRange: [dayjs.Dayjs, dayjs.Dayjs];
    description?: string;
}

export default function CreateEventModal({ open, onCancel }: CreateEventModalProps) {
    const [form] = Form.useForm<CreateEventFormData>();
    const [createEvent, { isLoading }] = useCreateEventMutation();
    const { message } = App.useApp();

    const handleSubmit = async (values: CreateEventFormData) => {
        try {
            const createEventData: CreateEventRequest = {
                title: values.title,
                location: values.location,
                start_datetime: values.dateRange[0].toISOString(),
                end_datetime: values.dateRange[1].toISOString(),
                description: values.description || '',
            };

            await createEvent(createEventData).unwrap();

            message.success('Мероприятие успешно создано!');
            form.resetFields();
            onCancel();
        } catch (error) {
            message.error('Ошибка при создании мероприятия');
        }
    };

    const handleCancel = () => {
        form.resetFields();
        onCancel();
    };

    return (
        <Modal
            title="Создать мероприятие"
            open={open}
            onCancel={handleCancel}
            footer={null}
            width={600}
        >
            <Form
                form={form}
                layout="vertical"
                onFinish={handleSubmit}
                requiredMark={false}
            >
                <Form.Item
                    name="title"
                    label="Название мероприятия"
                    rules={[
                        { required: true, message: 'Пожалуйста, введите название мероприятия' },
                        { max: 100, message: 'Название не должно превышать 100 символов' }
                    ]}
                >
                    <Input placeholder="Введите название мероприятия" />
                </Form.Item>

                <Form.Item
                    name="location"
                    label="Место проведения"
                    rules={[
                        { required: true, message: 'Пожалуйста, укажите место проведения' },
                        { max: 200, message: 'Адрес не должен превышать 200 символов' }
                    ]}
                >
                    <Input placeholder="Введите адрес или место проведения" />
                </Form.Item>

                <Form.Item
                    name="dateRange"
                    label="Дата начала и окончания мероприятия"
                    rules={[
                        { required: true, message: 'Пожалуйста, выберите дату и время' }
                    ]}
                >
                    <RangePicker
                        showTime
                        format="DD.MM.YYYY HH:mm"
                        placeholder={['Начало', 'Окончание']}
                        style={{ width: '100%' }}
                        disabledDate={(current) => current && current < dayjs().startOf('day')}
                    />
                </Form.Item>

                <Form.Item
                    name="description"
                    label="Описание (необязательно)"
                    rules={[
                        { max: 500, message: 'Описание не должно превышать 500 символов' }
                    ]}
                >
                    <TextArea
                        rows={4}
                        placeholder="Введите описание мероприятия"
                        showCount
                        maxLength={500}
                    />
                </Form.Item>

                <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
                    <Button onClick={handleCancel} style={{ marginRight: 8 }}>
                        Отмена
                    </Button>
                    <Button type="primary" htmlType="submit" loading={isLoading}>
                        Создать
                    </Button>
                </Form.Item>
            </Form>
        </Modal>
    );
} 