'use client';

import { Form, Input, Button, message } from 'antd';
import { LockOutlined, UserOutlined } from '@ant-design/icons';
import AuthWrapper from '@/components/auth-wrapper/AuthWrapper';
import { useLoginMutation, useLazyGetMeQuery } from '@/store/api/api';

export default function LoginForm() {
  const [login, { isLoading: loginLoading }] = useLoginMutation();
  const [getMe] = useLazyGetMeQuery();

  const onFinish = async (values: any) => {
    try {
      await login(values).unwrap();

      const user = await getMe().unwrap();

      console.log('user');
      console.log(user);
    } catch (err) {
      console.log('error');
      console.log(err);
    }
  };

  return (
    <AuthWrapper title="Вход в систему">
      <Form
        name="login"
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
          или <a href="/auth/register">Зарегистрироваться!</a>
        </Form.Item>
      </Form>
    </AuthWrapper>
  );
}
