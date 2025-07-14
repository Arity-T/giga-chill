import React, { useEffect } from 'react';
import { Modal, Form, Input, DatePicker, Select, App } from 'antd';
import { TaskRequest, User, ShoppingListWithItems } from '@/types/api';
import dayjs from 'dayjs';

const { TextArea } = Input;
const { Option } = Select;

interface CreateTaskModalProps {
    open: boolean;
    onCancel: () => void;
    onSubmit: (taskData: TaskRequest) => void;
    participants: User[];
    shoppingLists: ShoppingListWithItems[];
    loading?: boolean;
}

export default function CreateTaskModal({ open, onCancel, onSubmit, participants, shoppingLists, loading }: CreateTaskModalProps) {
    const [form] = Form.useForm();
    const { message } = App.useApp();

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
                executor_id: values.executor_id || '',
                shopping_lists_ids: values.shopping_lists_ids || [],
            };

            onSubmit(taskData);
            form.resetFields();
        } catch (error) {
            message.error('Пожалуйста, заполните все обязательные поля');
        }
    };

    return (
        <Modal
            title="Создать задачу"
            open={open}
            onCancel={onCancel}
            onOk={handleSubmit}
            confirmLoading={loading}
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
                        disabledDate={(current) => current && current < dayjs().startOf('day')}
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
                        filterOption={(input, option) => {
                            const shoppingList = shoppingLists.find(list => list.shopping_list_id === option?.value);
                            if (shoppingList) {
                                return shoppingList.title.toLowerCase().includes(input.toLowerCase());
                            }
                            return false;
                        }}
                    >
                        {shoppingLists.map((shoppingList) => (
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