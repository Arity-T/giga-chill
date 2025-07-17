'use client';

import React, { useEffect } from 'react';
import { Modal, Form, Input, InputNumber, Button, App, Select } from 'antd';
import { useUpdateShoppingItemMutation } from '@/store/api';
import type { ShoppingItemRequest, ShoppingItem } from '@/types/api';

interface EditShoppingItemModalProps {
    open: boolean;
    onCancel: () => void;
    eventId: string;
    shoppingListId: string;
    item: ShoppingItem | null;
}

interface EditShoppingItemFormData {
    title: string;
    quantity: number;
    unit: string;
}

const popularUnits = [
    { value: 'шт', label: 'шт' },
    { value: 'кг', label: 'кг' },
    { value: 'г', label: 'г' },
    { value: 'л', label: 'л' },
    { value: 'мл', label: 'мл' },
];

export default function EditShoppingItemModal({
    open,
    onCancel,
    eventId,
    shoppingListId,
    item
}: EditShoppingItemModalProps) {
    const [form] = Form.useForm<EditShoppingItemFormData>();
    const [updateShoppingItem, { isLoading }] = useUpdateShoppingItemMutation();
    const { message } = App.useApp();
    const [units, setUnits] = React.useState(popularUnits);

    // Заполняем форму данными товара при открытии модального окна
    useEffect(() => {
        if (open && item) {
            form.setFieldsValue({
                title: item.title,
                quantity: item.quantity,
                unit: item.unit
            });

            // Добавляем единицу товара в список, если её там нет
            if (!popularUnits.some(unit => unit.value === item.unit)) {
                setUnits([...popularUnits, { value: item.unit, label: item.unit }]);
            }
        }
    }, [open, item, form]);

    const handleSubmit = async (values: EditShoppingItemFormData) => {
        if (!item) return;

        try {
            const shoppingItemData: ShoppingItemRequest = {
                title: values.title,
                quantity: values.quantity,
                unit: values.unit,
            };

            await updateShoppingItem({
                eventId,
                shoppingListId,
                shoppingItemId: item.shopping_item_id,
                shoppingItem: shoppingItemData
            }).unwrap();

            message.success('Товар успешно обновлен!');
            onCancel();
        } catch (error) {
            message.error('Ошибка при обновлении товара');
        }
    };

    const handleCancel = () => {
        form.resetFields();
        setUnits(popularUnits); // Сбрасываем список единиц к исходному
        onCancel();
    };

    return (
        <Modal
            title="Редактировать товар"
            open={open}
            onCancel={handleCancel}
            footer={null}
            width={400}
        >
            <Form
                form={form}
                layout="vertical"
                onFinish={handleSubmit}
                requiredMark={false}
            >
                <Form.Item
                    name="title"
                    label="Название товара"
                    rules={[
                        { required: true, message: 'Пожалуйста, введите название товара' },
                        { max: 100, message: 'Название не должно превышать 100 символов' }
                    ]}
                >
                    <Input placeholder="Введите название товара" />
                </Form.Item>

                <div style={{ display: 'flex', gap: '16px' }}>
                    <Form.Item
                        name="quantity"
                        label="Количество"
                        rules={[
                            { required: true, message: 'Укажите количество' },
                            { type: 'number', min: 0.01, message: 'Количество должно быть больше 0' }
                        ]}
                        style={{ flex: 1 }}
                    >
                        <InputNumber
                            placeholder="1"
                            min={0.01}
                            step={1}
                            style={{ width: '100%' }}
                        />
                    </Form.Item>

                    <Form.Item
                        name="unit"
                        label="Единица"
                        rules={[
                            { required: true, message: 'Укажите единицу измерения' },
                            { max: 20, message: 'Единица не должна превышать 20 символов' }
                        ]}
                        style={{ flex: 1 }}
                    >
                        <Select
                            placeholder="Выберите единицу"
                            options={units}
                            showSearch
                            allowClear
                            filterOption={(input, option) =>
                                (option?.label ?? '').toLowerCase().includes(input.toLowerCase())
                            }
                            onSearch={(value) => {
                                // Если введенное значение не найдено в списке, добавляем его
                                if (value && !units.some(unit => unit.value === value)) {
                                    setUnits([...popularUnits, { value, label: value }]);
                                }
                            }}
                            notFoundContent={
                                <div style={{ padding: '8px 12px', color: '#999' }}>
                                    Введите свою единицу измерения
                                </div>
                            }
                        />
                    </Form.Item>
                </div>

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