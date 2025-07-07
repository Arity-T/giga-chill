import React from 'react';
import { Select } from 'antd';
import { UserRole, UserInEvent } from '@/types/api';
import UserRoleTag from '@/components/UserRoleTag';

const { Option } = Select;

interface ParticipantRoleSelectProps {
    participant: UserInEvent;
    onRoleChange: (participant: UserInEvent, newRole: UserRole) => void;
    disabled?: boolean;
}

export default function ParticipantRoleSelect({
    participant,
    onRoleChange,
    disabled = false
}: ParticipantRoleSelectProps) {
    if (disabled) {
        return (
            <div style={{ padding: '0 7px' }}>
                <UserRoleTag role={participant.user_role} tooltip="" />
            </div>
        );
    }

    return (
        <Select
            value={participant.user_role}
            style={{ width: 170 }}
            onChange={(newRole) => onRoleChange(participant, newRole)}
            size="small"
            variant="borderless"
            styles={{
                popup: {
                    root: { padding: '4px 0' }
                }
            }}
        >
            <Option value={UserRole.ADMIN} style={{ padding: '6px 12px' }}>
                <UserRoleTag role={UserRole.ADMIN} tooltip="" />
            </Option>
            <Option value={UserRole.PARTICIPANT} style={{ padding: '6px 12px' }}>
                <UserRoleTag role={UserRole.PARTICIPANT} tooltip="" />
            </Option>
        </Select>
    );
} 