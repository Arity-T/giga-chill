'use client';

import { useEffect, ReactNode } from 'react';
import { useRouter } from 'next/navigation';
import { PAGES } from '@/config/pages.config';
import { useGetMeQuery } from '@/store/api/api';

type Props = {
  children: ReactNode;
};

export function AuthGuard({ children }: Props) {
  const router = useRouter();
  const { data, error, isLoading } = useGetMeQuery();

  useEffect(() => {
    if (!isLoading && error) {
      router.replace(PAGES.LOGIN);
    }
  }, [error, isLoading, router]);

  // Пока грузим /me — не отображаем контент (можно вставить спиннер)
  if (isLoading) return null;

  // Уже редиректимся — не показываем ничего
  if (!data) return null;

  return <>{children}</>;
}