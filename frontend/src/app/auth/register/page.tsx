'use client';

import { Form, Input, Button, message } from 'antd';
import { LockOutlined, UserOutlined } from '@ant-design/icons';
import AuthWrapper from '@/components/auth-wrapper/AuthWrapper';
import { useRegisterMutation, useLazyGetMeQuery } from '@/store/api/api';

export default function RegisterForm() {
  const [register, { isLoading: registerLoading }] = useRegisterMutation();
  const [getMe] = useLazyGetMeQuery();

  const onFinish = async (values: any) => {
    console.log('Данные формы регистрации:', values);

    try {
      await register({ 
        name: values.name,
        login: values.login, 
        password: values.password 
      }).unwrap();

      console.log('Регистрация успешна');

      const user = await getMe().unwrap();

      console.log('Пользователь после регистрации:');
      console.log(user);

    } catch (err) {
      console.log('Ошибка регистрации:');
      console.log(err);
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
          rules={[{ required: true, message: 'Введите логин!' }]}
        >
          <Input prefix={<UserOutlined />} placeholder="Логин" />
        </Form.Item>

        <Form.Item name="password" rules={[{ required: true }]}>
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
          или <a href="/auth/login">Войти!</a>
        </Form.Item>
      </Form>
    </AuthWrapper>
  );
}
