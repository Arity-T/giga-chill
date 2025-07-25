import { UserRole } from "@/store/api";

export const getRoleColor = (role: string) => {
    switch (role) {
        case UserRole.Owner:
            return 'green';
        case UserRole.Admin:
            return 'blue';
        default:
            return 'default';
    }
};

export const getRoleText = (role: string) => {
    switch (role) {
        case UserRole.Owner:
            return 'Организатор';
        case UserRole.Participant:
            return 'Участник';
        case UserRole.Admin:
            return 'Администратор';
        default:
            return role;
    }
}; 