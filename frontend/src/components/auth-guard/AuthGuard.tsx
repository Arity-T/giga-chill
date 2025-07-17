'use client';

import { useEffect, ReactNode } from 'react';
import { useRouter, usePathname, useSearchParams } from 'next/navigation';
import { PAGES } from '@/config/pages.config';
import { useGetMeQuery } from '@/store/api';

type Props = {
  children: ReactNode;
};

export function AuthGuard({ children }: Props) {
  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();
  const { data, error, isLoading } = useGetMeQuery();

  useEffect(() => {
    if (!isLoading && error) {
      // Сохраняем полный URL (путь + query параметры) как returnUrl для перенаправления после логина
      const queryString = searchParams.toString();
      const fullUrl = queryString ? `${pathname}?${queryString}` : pathname;
      const returnUrl = encodeURIComponent(fullUrl);
      router.replace(`${PAGES.LOGIN}?returnUrl=${returnUrl}`);
    }
  }, [error, isLoading, router, pathname, searchParams]);

  // Пока грузим /me — не отображаем контент (можно вставить спиннер)
  if (isLoading) return null;

  // Уже редиректимся — не показываем ничего
  if (!data) return null;

  return <>{children}</>;
}