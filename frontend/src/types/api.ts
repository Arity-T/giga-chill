export interface UserLoginPassword {
  login: string;
  password: string;
}

export interface User {
  id: string;
  login: string;
  name: string;
}

export type UserId = string;

export interface RegisterRequest {
  name: string;
  login: string;
  password: string;
}

export interface Event {
  event_id: string;
  title: string;
  location: string;
  start_datetime: string;
  end_datetime: string;
  description: string;
  budget: number;
  user_role: UserRole;
}

export interface CreateEventRequest {
  title: string;
  location: string;
  start_datetime: string;
  end_datetime: string;
  description?: string;
}

export interface UpdateEventRequest {
  title?: string;
  location?: string;
  start_datetime?: string;
  end_datetime?: string;
  description?: string;
}

export interface UserInEvent extends User {
  user_role: UserRole;
  balance: number;
}

export enum UserRole {
  OWNER = 'owner',
  ADMIN = 'admin',
  PARTICIPANT = 'participant',
}

export interface ShoppingItemPurchasedState {
  is_purchased: boolean;
}

export interface ShoppingItem {
  shopping_item_id: string;
  title: string;
  quantity: number;
  unit: string;
  is_purchased: boolean;
}

export interface ShoppingListWithItems {
  shopping_list_id: string;
  task_id: string;
  title: string;
  description: string;
  status: ShoppingListStatus;
  can_edit: boolean;
  shopping_items: ShoppingItem[];
  consumers: UserInEvent[];
}

export interface ShoppingListRequest {
  title: string;
  description: string;
}

export enum ShoppingListStatus {
  UNASSIGNED = 'unassigned',
  ASSIGNED = 'assigned',
  IN_PROGRESS = 'in_progress',
  BOUGHT = 'bought',
  PARTIALLY_BOUGHT = 'partially_bought',
  CANCELLED = 'cancelled',
}

export interface ShoppingItemRequest {
  title: string;
  quantity: number;
  unit: string;
}

export interface ShoppingItemPurchasedStateRequest {
  is_purchased: boolean;
}

export interface TaskRequest {
  title: string;
  description: string;
  deadline_datetime: string;
  executor_id: string;
  shopping_lists_ids: string[];
}

export enum TaskStatus {
  OPEN = 'open',
  IN_PROGRESS = 'in_progress',
  UNDER_REVIEW = 'under_review',
  COMPLETED = 'completed',
}

export interface Task {
  task_id: string;
  title: string;
  description: string;
  status: TaskStatus;
  deadline_datetime: string;
  actual_approval_id: string;
  author: User;
  executor: User;
}

export interface TaskWithShoppingLists extends Task {
  shopping_lists: ShoppingListWithItems[];
}