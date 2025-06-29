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
          Вход в систему
        </Title>
        <Form
          name="login"
          initialValues={{ remember: true }}
          style={{ maxWidth: 360 }}
          onFinish={onFinish}
        >
          <Form.Item
            name="login"
            rules={[{ required: true, message: 'Введите логин!' }]}
          >
            <Input prefix={<UserOutlined />} placeholder="Логин" />
          </Form.Item>
          <Form.Item
            name="password"
            rules={[{ required: true, message: 'Введите пароль!' }]}
          >
            <Input prefix={<LockOutlined />} type="password" placeholder="Пароль" />
          </Form.Item>

          <Form.Item>
            <Button block type="primary" htmlType="submit">
              Войти
            </Button>
            или <a href="">Зарегистрироваться!</a>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
}
