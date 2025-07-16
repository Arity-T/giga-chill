'use client';

import { Form, Input, Button, message } from 'antd';
import { LockOutlined, UserOutlined } from '@ant-design/icons';
import AuthWrapper from '@/components/auth-wrapper/AuthWrapper';
import { useLoginMutation } from '@/store/api';
import { PAGES } from '@/config/pages.config';
import { useRouter, useSearchParams } from 'next/navigation';
import Link from 'next/link';

export default function LoginForm() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [login, { isLoading: loginLoading }] = useLoginMutation();

  const onFinish = async (values: any) => {
    try {
      await login(values).unwrap();

      // Получаем URL для перенаправления из параметров запроса
      const returnUrl = searchParams.get('returnUrl');
      const redirectTo = returnUrl ? decodeURIComponent(returnUrl) : PAGES.HOME;

      router.replace(redirectTo);
    } catch (err) {
      console.log('error');
      console.log(err);
    }
  };

  const returnUrl = searchParams.get('returnUrl');

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
          <Button block type="primary" htmlType="submit" loading={loginLoading}>
            Войти
          </Button>
          или <Link href={`${PAGES.REGISTER}${returnUrl ? `?returnUrl=${encodeURIComponent(returnUrl)}` : ''}`}>Зарегистрироваться!</Link>
        </Form.Item>
      </Form>
    </AuthWrapper>
  );
}
