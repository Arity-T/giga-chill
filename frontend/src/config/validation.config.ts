export interface ValidationRule {
    pattern: RegExp;
    message: string;
}

export const LOGIN_VALIDATION_RULES: ValidationRule[] = [
    {
        pattern: /.{4,}/,
        message: 'Логин должен содержать минимум 4 символа',
    },
    {
        pattern: /^[a-zA-Z0-9]+$/,
        message: 'Логин может содержать только латинские буквы и цифры',
    },
];

export const PASSWORD_VALIDATION_RULES: ValidationRule[] = [
    {
        pattern: /.{8,}/,
        message: 'Пароль должен содержать минимум 8 символов',
    },
    {
        pattern: /^[a-zA-Z0-9!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]+$/,
        message: 'Пароль может содержать только латинские буквы, цифры и некоторые специальные символы',
    },
]; 