'use client';

import React, { useEffect } from 'react';
import { Modal, Form, Input, Button, App } from 'antd';
import { useUpdateShoppingListMutation } from '@/store/api';
import type { ShoppingListRequest, ShoppingListWithItems } from '@/types/api';

const { TextArea } = Input;

interface ShoppingListEditModalProps {
    open: boolean;
    onCancel: () => void;
    eventId: string;
    shoppingList: ShoppingListWithItems;
}

interface ShoppingListFormData {
    title: string;
    description: string;
}

export default function ShoppingListEditModal({
    open,
    onCancel,
    eventId,
    shoppingList
}: ShoppingListEditModalProps) {
    const [form] = Form.useForm<ShoppingListFormData>();
    const [updateShoppingList, { isLoading }] = useUpdateShoppingListMutation();
    const { message } = App.useApp();

    // Управление состоянием формы
    useEffect(() => {
        if (open && shoppingList) {
            form.setFieldsValue({
                title: shoppingList.title,
                description: shoppingList.description,
            });
        }
    }, [open, shoppingList, form]);

    const handleSubmit = async (values: ShoppingListFormData) => {
        try {
            const shoppingListData: ShoppingListRequest = {
                title: values.title,
                description: values.description || '',
            };

            await updateShoppingList({
                eventId,
                shoppingListId: shoppingList.shopping_list_id,
                shoppingList: shoppingListData
            }).unwrap();

            message.success('Список покупок успешно обновлен!');
            onCancel();
        } catch (error) {
            message.error('Ошибка при обновлении списка покупок');
        }
    };

    const handleCancel = () => {
        form.resetFields();
        onCancel();
    };

    return (
        <Modal
            title="Редактировать список покупок"
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
                        Сохранить
                    </Button>
                </Form.Item>
            </Form>
        </Modal>
    );
} 