'use client';

import React from 'react';
import { Modal, Form, Input, Button, App } from 'antd';
import { useCreateShoppingListMutation } from '@/store/api';
import type { ShoppingListCreate } from '@/store/api';

const { TextArea } = Input;

interface ShoppingListModalProps {
    open: boolean;
    onCancel: () => void;
    eventId: string;
}

interface ShoppingListFormData {
    title: string;
    description: string;
}

export default function ShoppingListModal({ open, onCancel, eventId }: ShoppingListModalProps) {
    const [form] = Form.useForm<ShoppingListFormData>();
    const [createShoppingList, { isLoading }] = useCreateShoppingListMutation();
    const { message } = App.useApp();

    const handleSubmit = async (values: ShoppingListFormData) => {
        try {
            const shoppingListData: ShoppingListCreate = {
                title: values.title,
                description: values.description || '',
            };

            await createShoppingList({
                eventId,
                shoppingListCreate: shoppingListData
            }).unwrap();
            message.success('Список покупок успешно создан!');

            form.resetFields();
            onCancel();
        } catch (error) {
            message.error('Ошибка при создании списка покупок');
        }
    };

    const handleCancel = () => {
        form.resetFields();
        onCancel();
    };

    return (
        <Modal
            title="Создать список покупок"
            open={open}
            onCancel={handleCancel}
            footer={null}
            width={500}
        >
            <Form
                form={form}
                layout="vertical"
                onFinish={handleSubmit}
                requiredMark={false}
            >
                <Form.Item
                    name="title"
                    label="Название списка"
                    rules={[
                        { required: true, message: 'Пожалуйста, введите название списка' },
                        { max: 100, message: 'Название не должно превышать 100 символов' }
                    ]}
                >
                    <Input placeholder="Введите название списка покупок" />
                </Form.Item>

                <Form.Item
                    name="description"
                    label="Описание"
                    rules={[
                        { max: 500, message: 'Описание не должно превышать 500 символов' }
                    ]}
                >
                    <TextArea
                        rows={4}
                        placeholder="Введите описание списка покупок"
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