export const formatDateTime = (dateTimeStr: string | null | undefined) => {
    if (!dateTimeStr) {
        return 'Не указано';
    }

    const date = new Date(dateTimeStr);

    // Проверяем, что дата валидна
    if (isNaN(date.getTime())) {
        return 'Некорректная дата';
    }

    return date.toLocaleString('ru-RU', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
};

export const formatDate = (dateTimeStr: string | null | undefined) => {
    if (!dateTimeStr) {
        return 'Не указано';
    }

    const date = new Date(dateTimeStr);

    // Проверяем, что дата валидна
    if (isNaN(date.getTime())) {
        return 'Некорректная дата';
    }

    return date.toLocaleDateString('ru-RU', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
    });
};