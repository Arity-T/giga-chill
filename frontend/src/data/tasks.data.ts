import { Task, TaskStatus, User } from '@/types/api';

// Моковые пользователи
const mockUsers: User[] = [
    { id: '1', login: 'alexey_dev', name: 'Алексей Петров' },
    { id: '2', login: 'maria_design', name: 'Мария Сидорова' },
    { id: '3', login: 'ivan_pm', name: 'Иван Козлов' },
    { id: '4', login: 'anna_qa', name: 'Анна Морозова' },
];

// Моковые задачи
export const mockTasks: Task[] = [
    {
        task_id: '1',
        title: 'Подготовить презентацию для клиента',
        description: 'Создать презентацию с демонстрацией новых возможностей продукта. Включить статистику использования и планы развития.',
        status: TaskStatus.IN_PROGRESS,
        deadline_datetime: '2024-02-15T18:00:00Z',
        actual_approval_id: '',
        author: mockUsers[2],
        executor: mockUsers[0],
    },
    {
        task_id: '2',
        title: 'Протестировать новую функциональность',
        description: 'Провести полное тестирование модуля авторизации и выявить потенциальные баги.',
        status: TaskStatus.OPEN,
        deadline_datetime: '2024-02-20T12:00:00Z',
        actual_approval_id: '',
        author: mockUsers[2],
        executor: mockUsers[3],
    },
    {
        task_id: '3',
        title: 'Создать макеты для мобильной версии',
        description: 'Разработать адаптивные макеты основных страниц приложения для мобильных устройств.',
        status: TaskStatus.UNDER_REVIEW,
        deadline_datetime: '2024-02-18T15:30:00Z',
        actual_approval_id: '',
        author: mockUsers[2],
        executor: mockUsers[1],
    },
    {
        task_id: '4',
        title: 'Обновить документацию API',
        description: 'Актуализировать документацию в соответствии с последними изменениями в API.',
        status: TaskStatus.COMPLETED,
        deadline_datetime: '2024-02-10T17:00:00Z',
        actual_approval_id: '',
        author: mockUsers[2],
        executor: mockUsers[0],
    },
    {
        task_id: '5',
        title: 'Настроить CI/CD pipeline',
        description: 'Настроить автоматизированную сборку и деплой для тестовой среды.',
        status: TaskStatus.OPEN,
        deadline_datetime: '2024-02-25T14:00:00Z',
        actual_approval_id: '',
        author: mockUsers[2],
        executor: mockUsers[0],
    },
    {
        task_id: '6',
        title: 'Исследовать новые технологии',
        description: 'Изучить возможности внедрения GraphQL и подготовить рекомендации.',
        status: TaskStatus.IN_PROGRESS,
        deadline_datetime: '2024-03-01T16:00:00Z',
        actual_approval_id: '',
        author: mockUsers[2],
        executor: mockUsers[0],
    },
    {
        task_id: '7',
        title: 'Провести анализ конкурентов',
        description: 'Изучить решения конкурентов и выявить лучшие практики.',
        status: TaskStatus.OPEN,
        deadline_datetime: '2024-02-28T12:00:00Z',
        actual_approval_id: '',
        author: mockUsers[2],
        executor: null,
    },
];

export const mockCurrentUser = mockUsers[0]; 