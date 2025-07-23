import React, { useEffect, useMemo } from 'react';
import { Modal, Form, Input, DatePicker, Select, App } from 'antd';
import { TaskRequest } from '@/types/api';
import type { User, ShoppingListWithItems } from '@/store/api';
import { useCreateTaskMutation, useGetEventQuery } from '@/store/api';
import { getAvailableShoppingLists } from '@/utils/shopping-list-utils';
import dayjs from 'dayjs';

const { TextArea } = Input;
const { Option } = Select;

interface CreateTaskModalProps {
    open: boolean;
    onCancel: () => void;
    participants: User[];
    shoppingLists: ShoppingListWithItems[];
    eventId: string;
}

export default function CreateTaskModal({ open, onCancel, participants, shoppingLists, eventId }: CreateTaskModalProps) {
    const [form] = Form.useForm();
    const { message } = App.useApp();
    const [createTask, { isLoading }] = useCreateTaskMutation();

    // Получаем информацию о событии
    const { data: event } = useGetEventQuery(eventId);

    // Фильтруем только доступные для выбора списки покупок
    const availableShoppingLists = useMemo(() => {
        return getAvailableShoppingLists(shoppingLists);
    }, [shoppingLists]);

    useEffect(() => {
        if (open) {
            form.resetFields();
        }
    }, [open, form]);

    const handleSubmit = async () => {
        try {
            const values = await form.validateFields();

            const taskData: TaskRequest = {
                title: values.title,
                description: values.description || '',
                deadline_datetime: values.deadline_datetime.toISOString(),
                executor_id: values.executor_id || null,
                shopping_lists_ids: values.shopping_lists_ids || [],
            };

            await createTask({ eventId, task: taskData }).unwrap();
            message.success('Задача создана');
            form.resetFields();
            onCancel();
        } catch (error) {
            message.error('Ошибка при создании задачи');
        }
    };

    return (
        <Modal
            title="Создать задачу"
            open={open}
            onCancel={onCancel}
            onOk={handleSubmit}
            confirmLoading={isLoading}
            okText="Создать"
            cancelText="Отмена"
            destroyOnHidden
            width={600}
        >
            <Form
                form={form}
                layout="vertical"
                requiredMark={false}
            >
                <Form.Item
                    name="title"
                    label="Название задачи"
                    rules={[
                        { required: true, message: 'Пожалуйста, введите название задачи' },
                        { max: 200, message: 'Название не должно превышать 200 символов' }
                    ]}
                >
                    <Input placeholder="Введите название задачи" />
                </Form.Item>

                <Form.Item
                    name="description"
                    label="Описание"
                    rules={[
                        { max: 1000, message: 'Описание не должно превышать 1000 символов' }
                    ]}
                >
                    <TextArea
                        rows={3}
                        placeholder="Введите описание задачи (необязательно)"
                    />
                </Form.Item>

                <Form.Item
                    name="deadline_datetime"
                    label="Срок выполнения"
                    rules={[
                        { required: true, message: 'Пожалуйста, выберите срок выполнения' }
                    ]}
                >
                    <DatePicker
                        showTime
                        format="DD.MM.YYYY HH:mm"
                        placeholder="Выберите дату и время"
                        style={{ width: '100%' }}
                        disabledDate={(current) => {
                            if (!current) return false;

                            // Нельзя выбрать дату раньше сегодня
                            if (current < dayjs().startOf('day')) {
                                return true;
                            }

                            // Нельзя выбрать дату позже окончания события
                            if (event?.end_datetime && current > dayjs(event.end_datetime).endOf('day')) {
                                return true;
                            }

                            return false;
                        }}
                        disabledTime={(current) => {
                            if (!current || !event?.end_datetime) return {};

                            const eventEnd = dayjs(event.end_datetime);

                            // Если выбранная дата не равна дате окончания события, то все времена доступны
                            if (!current.isSame(eventEnd, 'day')) {
                                return {};
                            }

                            // Если выбранная дата равна дате окончания события
                            const endHour = eventEnd.hour();
                            const endMinute = eventEnd.minute();

                            return {
                                disabledHours: () => {
                                    const hours = [];
                                    for (let i = endHour + 1; i < 24; i++) {
                                        hours.push(i);
                                    }
                                    return hours;
                                },
                                disabledMinutes: (selectedHour) => {
                                    if (selectedHour === endHour) {
                                        const minutes = [];
                                        for (let i = endMinute + 1; i < 60; i++) {
                                            minutes.push(i);
                                        }
                                        return minutes;
                                    }
                                    return [];
                                }
                            };
                        }}
                    />
                </Form.Item>

                <Form.Item
                    name="executor_id"
                    label="Исполнитель"
                >
                    <Select
                        placeholder="Выберите исполнителя (необязательно)"
                        allowClear
                        showSearch
                        filterOption={(input, option) => {
                            const participant = participants.find(p => p.id === option?.value);
                            if (participant) {
                                const searchText = `${participant.name} @${participant.login}`.toLowerCase();
                                return searchText.includes(input.toLowerCase());
                            }
                            return false;
                        }}
                    >
                        {participants.map((participant) => (
                            <Option key={participant.id} value={participant.id}>
                                {participant.name} (@{participant.login})
                            </Option>
                        ))}
                    </Select>
                </Form.Item>

                <Form.Item
                    name="shopping_lists_ids"
                    label="Списки покупок"
                >
                    <Select
                        mode="multiple"
                        placeholder="Выберите списки покупок (необязательно)"
                        allowClear
                        showSearch
                        notFoundContent="Нет доступных списков покупок"
                        filterOption={(input, option) => {
                            const shoppingList = availableShoppingLists.find(list => list.shopping_list_id === option?.value);
                            if (shoppingList) {
                                return shoppingList.title.toLowerCase().includes(input.toLowerCase());
                            }
                            return false;
                        }}
                    >
                        {availableShoppingLists.map((shoppingList) => (
                            <Option key={shoppingList.shopping_list_id} value={shoppingList.shopping_list_id}>
                                {shoppingList.title}
                            </Option>
                        ))}
                    </Select>
                </Form.Item>
            </Form>
        </Modal>
    );
} 