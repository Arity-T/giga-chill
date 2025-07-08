import { UserRole } from "@/types/api";

export const getRoleColor = (role: string) => {
    switch (role) {
        case UserRole.OWNER:
            return 'green';
        case UserRole.ADMIN:
            return 'blue';
        default:
            return 'default';
    }
};

export const getRoleText = (role: string) => {
    switch (role) {
        case UserRole.OWNER:
            return 'Организатор';
        case UserRole.PARTICIPANT:
            return 'Участник';
        case UserRole.ADMIN:
            return 'Администратор';
        default:
            return role;
    }
}; 