'use client';

import { Form, Input, Button } from 'antd';

export default function LoginForm() {
  const onFinish = (values: any) => {
    console.log('Успешная отправка:', values);
  };

  return (
    <Form
      name="login"
      onFinish={onFinish}
      style={{ maxWidth: 300, margin: '0 auto' }}
    >
      <Form.Item
        label="Email"
        name="email"
        rules={[{ required: true, message: 'Введите email!' }]}
      >
        <Input />
      </Form.Item>

      <Form.Item
        label="Пароль"
        name="password"
        rules={[{ required: true, message: 'Введите пароль!' }]}
      >
        <Input.Password />
      </Form.Item>

      <Form.Item>
        <Button type="primary" htmlType="submit">
          Войти
        </Button>
      </Form.Item>
    </Form>
  );
}
