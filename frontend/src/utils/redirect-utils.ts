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