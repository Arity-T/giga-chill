'use client';

import { Button } from "antd";
import styles from "./page.module.css";
import { useGetMeQuery, useLogoutMutation } from '@/store/api/api';

export default function ProfileContent() {
  const { data: user } = useGetMeQuery();
  const [logout] = useLogoutMutation();
  if (!user) {
    return <div>Пользователь не найден</div>;
  }

  return (
    <div className={styles.page}>
      <h1>Профиль пользователя</h1>
      <p><strong>ID:</strong> {user.id}</p>
      <p><strong>Логин:</strong> {user.login}</p>
      <p><strong>Имя:</strong> {user.name}</p>
      <Button onClick={() => logout()}>Выйти</Button>
    </div>
  );
}
