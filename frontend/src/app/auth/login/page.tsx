'use client';

import { Form, Input, Button, message } from 'antd';
import { LockOutlined, UserOutlined } from '@ant-design/icons';
import AuthWrapper from '@/components/auth-wrapper/AuthWrapper';
import { useLoginMutation } from '@/store/api';
import { PAGES } from '@/config/pages.config';
import { useRouter, useSearchParams } from 'next/navigation';
import Link from 'next/link';
import { handleSuccessfulAuth, createAuthLinkWithReturnUrl } from '@/utils/redirect-utils';
import { createFieldValidator } from '@/utils/validation-utils';
import { LOGIN_VALIDATION_RULES, PASSWORD_VALIDATION_RULES } from '@/config/validation.config';

export default function LoginForm() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [login, { isLoading: loginLoading }] = useLoginMutation();

  const onFinish = async (values: any) => {
    try {
      await login(values).unwrap();
      handleSuccessfulAuth(searchParams, router);
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
          rules={[
            { required: true, message: 'Введите логин!' },
            { validator: createFieldValidator(LOGIN_VALIDATION_RULES) }
          ]}
        >
          <Input prefix={<UserOutlined />} placeholder="Логин" />
        </Form.Item>
        <Form.Item
          name="password"
          rules={[
            { required: true, message: 'Введите пароль!' },
            { validator: createFieldValidator(PASSWORD_VALIDATION_RULES) }
          ]}
        >
          <Input prefix={<LockOutlined />} type="password" placeholder="Пароль" />
        </Form.Item>

        <Form.Item>
          <Button block type="primary" htmlType="submit" loading={loginLoading}>
            Войти
          </Button>
          или <Link href={createAuthLinkWithReturnUrl(PAGES.REGISTER, searchParams)}>Зарегистрироваться!</Link>
        </Form.Item>
      </Form>
    </AuthWrapper>
  );
}
