import { TaskStatus } from '@/store/api';

export const getTaskStatusText = (status: TaskStatus): string => {
    switch (status) {
        case TaskStatus.Open:
            return 'Открыта';
        case TaskStatus.InProgress:
            return 'В работе';
        case TaskStatus.UnderReview:
            return 'На проверке';
        case TaskStatus.Completed:
            return 'Завершена';
        default:
            return status;
    }
};

export const getTaskStatusColor = (status: TaskStatus): string => {
    switch (status) {
        case TaskStatus.Open:
            return 'default';
        case TaskStatus.InProgress:
            return 'processing';
        case TaskStatus.UnderReview:
            return 'warning';
        case TaskStatus.Completed:
            return 'success';
        default:
            return 'default';
    }
};

export const getTaskStatusTooltip = (status: TaskStatus): string => {
    switch (status) {
        case TaskStatus.Open:
            return 'Задача создана, но еще не взята в работу';
        case TaskStatus.InProgress:
            return 'Задача выполняется';
        case TaskStatus.UnderReview:
            return 'Задача выполнена и ожидает проверки';
        case TaskStatus.Completed:
            return 'Задача полностью завершена';
        default:
            return status;
    }
};

export const getAllTaskStatuses = (): TaskStatus[] => {
    return [TaskStatus.Open, TaskStatus.InProgress, TaskStatus.UnderReview, TaskStatus.Completed];
}; 