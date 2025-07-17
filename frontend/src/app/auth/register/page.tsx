'use client';

import { Form, Input, Button, App } from 'antd';
import { LockOutlined, UserOutlined } from '@ant-design/icons';
import AuthWrapper from '@/components/auth-wrapper/AuthWrapper';
import { useRegisterMutation } from '@/store/api';
import { PAGES } from '@/config/pages.config';
import { useRouter, useSearchParams } from 'next/navigation';
import Link from 'next/link';
import { handleSuccessfulAuth, createAuthLinkWithReturnUrl } from '@/utils/redirect-utils';
import { createFieldValidator } from '@/utils/validation-utils';
import { LOGIN_VALIDATION_RULES, PASSWORD_VALIDATION_RULES } from '@/config/validation.config';

export default function RegisterForm() {
  const router = useRouter();
  const { message } = App.useApp();
  const searchParams = useSearchParams();
  const [register, { isLoading: registerLoading }] = useRegisterMutation();

  const onFinish = async (values: any) => {
    try {
      await register({
        name: values.name,
        login: values.login,
        password: values.password
      }).unwrap();

      handleSuccessfulAuth(searchParams, router);
    } catch (err: any) {
      if (err?.status === 409) {
        message.error('Пользователь с таким логином уже существует');
      } else if (err?.status === 400) {
        message.error('Некорректные данные. Проверьте правильность заполнения полей');
      } else if (err?.status >= 500) {
        message.error('Ошибка сервера. Попробуйте позже');
      } else if (!err?.status) {
        message.error('Проблемы с подключением к серверу');
      } else {
        message.error('Произошла ошибка при регистрации');
      }
    }
  };

  return (
    <AuthWrapper title="Регистрация">
      <Form
        name="register"
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

        <Form.Item
          name="password2"
          style={{ marginBottom: 30 }}
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
                return Promise.reject(new Error('Пароли не совпадают!'));
              },
            }),
          ]}
        >
          <Input prefix={<LockOutlined />} type="password" placeholder="Повторите пароль" />
        </Form.Item>

        <Form.Item>
          <Button block type="primary" htmlType="submit" loading={registerLoading}>
            Зарегистрироваться
          </Button>
          или <Link href={createAuthLinkWithReturnUrl(PAGES.LOGIN, searchParams)}>Войти!</Link>
        </Form.Item>
      </Form>
    </AuthWrapper>
  );
}
