/// <reference types="cypress" />

// TODO: Желательно перейти тут на кодогенерацию по спецификации OpenAPI, как уже 
// сделано на фронтенде. Пока можно просто копипастить оттуда сгенерированные типы.
// frontend/src/store/api/codegenApi.ts
// Чтобы файлик появился, надо предварительно выполнить npm run codegen

export type Login = string;
export type LoginRequest = {
    login: Login;
    /** Пароль пользователя */
    password: string;
};
export type Password = string;
export type Name = string;
export type RegisterRequest = {
    login: Login;
    password: Password;
    name: Name;
};
export type Uuid = string;
export type User = {
    id: Uuid;
    /** Логин пользователя */
    login: string;
    /** Имя пользователя */
    name: string;
};
export type Event = {
    event_id: Uuid;
    user_role: UserRole;
    /** Название мероприятия */
    title: string;
    /** Место мероприятия */
    location: string;
    /** Дата и время начала мероприятия */
    start_datetime: string;
    /** Дата и время окончания мероприятия */
    end_datetime: string;
    /** Описание мероприятия */
    description?: string;
    /** Бюджет мероприятия */
    budget?: number;
    /** Флаг, который показывает завершено ли мероприятие или нет. */
    is_finalized: boolean;
};
export type Events = Event[];
export type Title = string;
export type Location = string;
export type StartDatetime = string;
export type EndDatetime = string;
export type Description = string;
export type EventCreate = {
    title: Title;
    location: Location;
    start_datetime: StartDatetime;
    end_datetime: EndDatetime;
    description?: Description;
};
export type EventUpdate = {
    title?: Title;
    location?: Location;
    start_datetime?: StartDatetime;
    end_datetime?: EndDatetime;
    description?: Description;
};
export type InvitationToken = {
    /** Токен-приглашение */
    invitation_token: string | null;
};
export type Participant = {
    id: Uuid;
    login: Login;
    name: Name;
    user_role: UserRole;
};
export type Participants = Participant[];
export type ParticipantCreate = {
    login: Login;
};
export type ParticipantSetRole = {
    role: UserRole;
};
export type Title2 = string;
export type Description2 = string;
export type ShoppingItem = {
    shopping_item_id: Uuid;
    /** Название элемента списка покупок */
    title: string;
    /** Количество элемента списка покупок */
    quantity: number;
    /** Единицы измерения */
    unit: string;
    /** Признак того, что элемент списка покупок куплен */
    is_purchased: boolean;
};
export type ShoppingItems = ShoppingItem[];
export type ShoppingListWithItems = {
    shopping_list_id: Uuid;
    task_id: Uuid;
    title: Title2;
    description: Description2;
    status: ShoppingListStatus;
    /** Может ли пользователь редактировать список покупок */
    can_edit: boolean;
    /** Реальная стоимость списка покупок */
    budget: number;
    shopping_items: ShoppingItems;
    consumers: Participants;
};
export type ShoppingListsWithItems = ShoppingListWithItems[];
export type ShoppingListCreate = {
    /** Название списка покупок */
    title: string;
    /** Описание списка покупок */
    description?: string;
};
export type ShoppingListUpdate = {
    title?: Title2;
    description?: Description2;
};
export type Budget = number;
export type ShoppingListSetBudget = {
    budget: Budget;
};
export type UuidList = Uuid[];
export type Title3 = string;
export type Quantity = number;
export type Unit = string;
export type ShoppingItemCreate = {
    title: Title3;
    quantity: Quantity;
    unit: Unit;
};
export type ShoppingItemUpdate = {
    title?: Title3;
    quantity?: Quantity;
    unit?: Unit;
};
export type IsPurchased = boolean;
export type ShoppingItemSetPurchased = {
    is_purchased: IsPurchased;
};
export type Task = {
    task_id: Uuid;
    /** Название задачи */
    title: string;
    /** Описание задачи */
    description: string;
    status: TaskStatus;
    /** Дедлайн задачи */
    deadline_datetime: string;
    /** Комментарий исполнителя */
    executor_comment?: string;
    /** Комментарий проверяющего */
    reviewer_comment?: string;
    permissions: {
        /** Может ли пользователь редактировать задачу */
        can_edit: boolean;
        /** Может ли пользователь взять задачу в работу */
        can_take_in_work: boolean;
        /** Может ли пользователь подтвердить или отклонить задачу */
        can_review: boolean;
    };
    author: User;
    executor?: User;
};
export type Tasks = Task[];
export type Title4 = string;
export type Description3 = string;
export type DeadlineDatetime = string;
export type TaskCreate = {
    title: Title4;
    description?: Description3;
    deadline_datetime: DeadlineDatetime;
    /** Идентификатор исполнителя задачи */
    executor_id?: Uuid;
    shopping_lists_ids?: UuidList;
};
export type TaskWithShoppingLists = Task & {
    shopping_lists: ShoppingListsWithItems;
};
export type TaskUpdate = {
    title?: Title4;
    description?: Description3;
    deadline_datetime?: DeadlineDatetime;
};
export type TaskSetExecutor = {
    executor_id: Uuid | null;
};
export type ExecutorComment = string;
export type TaskSendForReviewRequest = {
    executor_comment: ExecutorComment;
};
export type ReviewerComment = string;
export type TaskReviewRequest = {
    reviewer_comment: ReviewerComment;
    /** Признак того, что задача одобрена */
    is_approved: boolean;
};
export type Debt = {
    user: User;
    /** Сумма долга */
    amount: number;
};
export type UserBalance = {
    my_debts: Debt[];
    debts_to_me: Debt[];
};
export type ParticipantBalanceSummary = {
    user: User;
    /** Сумма долгов пользователя в мероприятии (может быть отрицательной) */
    total_balance: number;
    user_balance: UserBalance;
};
export type EventBalanceSummary = ParticipantBalanceSummary[];
export enum UserRole {
    Owner = "owner",
    Admin = "admin",
    Participant = "participant",
}
export enum ShoppingListStatus {
    Unassigned = "unassigned",
    Assigned = "assigned",
    InProgress = "in_progress",
    Bought = "bought",
    PartiallyBought = "partially_bought",
    Cancelled = "cancelled",
}
export enum TaskStatus {
    Open = "open",
    InProgress = "in_progress",
    UnderReview = "under_review",
    Completed = "completed",
}