import { TaskStatus } from '@/types/api';

export const getTaskStatusText = (status: TaskStatus): string => {
    switch (status) {
        case TaskStatus.OPEN:
            return 'Открыта';
        case TaskStatus.IN_PROGRESS:
            return 'В работе';
        case TaskStatus.UNDER_REVIEW:
            return 'На проверке';
        case TaskStatus.COMPLETED:
            return 'Завершена';
        default:
            return status;
    }
};

export const getTaskStatusColor = (status: TaskStatus): string => {
    switch (status) {
        case TaskStatus.OPEN:
            return 'default';
        case TaskStatus.IN_PROGRESS:
            return 'processing';
        case TaskStatus.UNDER_REVIEW:
            return 'warning';
        case TaskStatus.COMPLETED:
            return 'success';
        default:
            return 'default';
    }
};

export const getTaskStatusTooltip = (status: TaskStatus): string => {
    switch (status) {
        case TaskStatus.OPEN:
            return 'Задача создана, но еще не взята в работу';
        case TaskStatus.IN_PROGRESS:
            return 'Задача выполняется';
        case TaskStatus.UNDER_REVIEW:
            return 'Задача выполнена и ожидает проверки';
        case TaskStatus.COMPLETED:
            return 'Задача полностью завершена';
        default:
            return status;
    }
};

export const getAllTaskStatuses = (): TaskStatus[] => {
    return [TaskStatus.OPEN, TaskStatus.IN_PROGRESS, TaskStatus.UNDER_REVIEW, TaskStatus.COMPLETED];
}; 