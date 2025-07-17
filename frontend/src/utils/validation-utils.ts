import { ValidationRule } from '@/config/validation.config';

export const validateField = (value: string, rules: ValidationRule[]): string | undefined => {
    if (!value) {
        return undefined; // Пустые значения обрабатываются отдельным правилом required
    }

    for (const rule of rules) {
        if (!rule.pattern.test(value)) {
            return rule.message;
        }
    }

    return undefined;
};

export const createFieldValidator = (rules: ValidationRule[]) => {
    return (_: any, value: string) => {
        const error = validateField(value, rules);
        if (error) {
            return Promise.reject(new Error(error));
        }
        return Promise.resolve();
    };
}; 