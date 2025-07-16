import { PAGES } from '@/config/pages.config';

/**
 * Валидирует returnUrl для предотвращения open redirect атак
 * Разрешает только внутренние относительные пути
 */
export function validateReturnUrl(returnUrl: string | null): string | null {
    if (!returnUrl) {
        return null;
    }

    // Удаляем пробелы
    const trimmed = returnUrl.trim();

    // Проверяем что это внутренний относительный путь
    if (
        // Должен начинаться с /
        trimmed.startsWith('/') &&
        // Не должен начинаться с // (protocol-relative URL)
        !trimmed.startsWith('//') &&
        // Не должен содержать протокол
        !trimmed.includes('://') &&
        // Не должен начинаться с обратного слеша (для Windows)
        !trimmed.startsWith('\\')
    ) {
        return trimmed;
    }

    // Если не прошел валидацию - возвращаем null
    return null;
}

/**
 * Обрабатывает успешную аутентификацию и выполняет редирект
 */
export function handleSuccessfulAuth(
    searchParams: URLSearchParams,
    router: { replace: (url: string) => void }
): void {
    // Получаем и валидируем URL для перенаправления из параметров запроса
    const returnUrl = searchParams.get('returnUrl');
    const validatedReturnUrl = validateReturnUrl(returnUrl);
    const redirectTo = validatedReturnUrl || PAGES.HOME;

    router.replace(redirectTo);
}

/**
 * Создает ссылку для перехода между страницами аутентификации с сохранением returnUrl
 */
export function createAuthLinkWithReturnUrl(
    targetPage: string,
    searchParams: URLSearchParams
): string {
    const returnUrl = searchParams.get('returnUrl');
    return `${targetPage}${returnUrl ? `?returnUrl=${encodeURIComponent(returnUrl)}` : ''}`;
} 