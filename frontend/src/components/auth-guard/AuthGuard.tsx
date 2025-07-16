'use client';

import { useEffect, ReactNode } from 'react';
import { useRouter, usePathname } from 'next/navigation';
import { PAGES } from '@/config/pages.config';
import { useGetMeQuery } from '@/store/api';

type Props = {
  children: ReactNode;
};

export function AuthGuard({ children }: Props) {
  const router = useRouter();
  const pathname = usePathname();
  const { data, error, isLoading } = useGetMeQuery();

  useEffect(() => {
    if (!isLoading && error) {
      // Сохраняем текущий путь как returnUrl для перенаправления после логина
      const returnUrl = encodeURIComponent(pathname);
      router.replace(`${PAGES.LOGIN}?returnUrl=${returnUrl}`);
    }
  }, [error, isLoading, router, pathname]);

  // Пока грузим /me — не отображаем контент (можно вставить спиннер)
  if (isLoading) return null;

  // Уже редиректимся — не показываем ничего
  if (!data) return null;

  return <>{children}</>;
}