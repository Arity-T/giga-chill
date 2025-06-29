'use client';

import { Form, Input, Button, Card, Typography, Flex, Checkbox } from 'antd';
import { LockOutlined, UserOutlined } from '@ant-design/icons';

import styles from './page.module.css';

const { Title } = Typography;

export default function LoginForm() {
  const onFinish = (values: any) => {
    console.log('Успешная отправка:', values);
  };

  return (
    <div className={styles.container}>
      <Card className={styles.loginCard}>
        <Title level={2} className={styles.title}>
          Регистрация
        </Title>
        <Form
          name="register"
          initialValues={{ remember: true }}
          style={{ maxWidth: 360 }}
          onFinish={onFinish}
        >
          <Form.Item
            name="name"
            rules={[{ required: true, message: 'Введите имя!' }]}
          >
            <Input placeholder="Имя" />
          </Form.Item>

          <Form.Item
            name="login"
            rules={[{ required: true, message: 'Введите логин!' }]}
          >
            <Input prefix={<UserOutlined />} placeholder="Логин" />
          </Form.Item>

          <Form.Item name="password" rules={[{ required: true }]}>
            <Input prefix={<LockOutlined />} type="password" placeholder="Пароль" />
          </Form.Item>

          {/* Field */}
          <Form.Item
            name="password2"
            dependencies={['password']}
            rules={[
              {
                required: true,
              },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('password') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('The new password that you entered do not match!'));
                },
              }),
            ]}
          >
            <Input prefix={<LockOutlined />} type="password" placeholder="Повторите пароль" />
          </Form.Item>

          <Form.Item>
            <Button block type="primary" htmlType="submit">
              Зарегистрироваться
            </Button>
            или <a href="/auth/login">Войти!</a>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
}
