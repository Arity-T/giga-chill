export interface UserLoginPassword {
  login: string;
  password: string;
}

export interface User {
  id: string;
  login: string;
  name: string;
}

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

export enum UserRole {
  OWNER = 'owner',
  ADMIN = 'admin',
  PARTICIPANT = 'participant',
}