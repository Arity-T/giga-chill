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