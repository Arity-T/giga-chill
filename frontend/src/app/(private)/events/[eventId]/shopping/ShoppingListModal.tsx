'use client';

import React, { useEffect } from 'react';
import { Modal, Form, Input, Button, App } from 'antd';
import { useCreateShoppingListMutation, useUpdateShoppingListMutation } from '@/store/api/api';
import type { ShoppingListRequest, ShoppingListWithItems } from '@/types/api';

const { TextArea } = Input;

interface ShoppingListModalProps {
    open: boolean;
    onCancel: () => void;
    eventId: string;
    shoppingList?: ShoppingListWithItems; // Если передан, то режим редактирования
}

interface ShoppingListFormData {
    title: string;
    description: string;
}

export default function ShoppingListModal({ open, onCancel, eventId, shoppingList }: ShoppingListModalProps) {
    const [form] = Form.useForm<ShoppingListFormData>();
    const [createShoppingList, { isLoading: isCreating }] = useCreateShoppingListMutation();
    const [updateShoppingList, { isLoading: isUpdating }] = useUpdateShoppingListMutation();
    const { message } = App.useApp();

    const isEditMode = !!shoppingList;
    const isLoading = isCreating || isUpdating;

    // Управление состоянием формы
    useEffect(() => {
        if (open) {
            if (isEditMode && shoppingList) {
                // Режим редактирования - заполняем форму
                form.setFieldsValue({
                    title: shoppingList.title,
                    description: shoppingList.description,
                });
            } else {
                // Режим создания - очищаем форму
                form.resetFields();
            }
        }
    }, [open, isEditMode, shoppingList, form]);

    const handleSubmit = async (values: ShoppingListFormData) => {
        try {
            const shoppingListData: ShoppingListRequest = {
                title: values.title,
                description: values.description || '',
            };

            if (isEditMode && shoppingList) {
                await updateShoppingList({
                    eventId,
                    shoppingListId: shoppingList.shopping_list_id,
                    shoppingList: shoppingListData
                }).unwrap();
                message.success('Список покупок успешно обновлен!');
            } else {
                await createShoppingList({ eventId, shoppingList: shoppingListData }).unwrap();
                message.success('Список покупок успешно создан!');
            }

            onCancel(); // Закрываем модалку (сброс формы произойдет в useEffect)
        } catch (error) {
            message.error(isEditMode
                ? 'Ошибка при обновлении списка покупок'
                : 'Ошибка при создании списка покупок'
            );
        }
    };

    const handleCancel = () => {
        onCancel(); // Сброс формы произойдет в useEffect при следующем открытии
    };

    return (
        <Modal
            title={isEditMode ? 'Редактировать список покупок' : 'Создать список покупок'}
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
                        {isEditMode ? 'Сохранить' : 'Создать'}
                    </Button>
                </Form.Item>
            </Form>
        </Modal>
    );
} 