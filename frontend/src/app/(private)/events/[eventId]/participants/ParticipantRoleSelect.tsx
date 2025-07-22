import React from 'react';
import { Select } from 'antd';
import { UserInEvent } from '@/types/api';
import { UserRole } from '@/store/api';
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
    const selectWidth = 170;

    if (disabled) {
        return (
            <div
                style={{
                    width: selectWidth,
                    height: 24, // Высота как у Select size="small"
                    display: 'flex',
                    alignItems: 'center',
                    paddingLeft: 7, // Отступ как у Select
                    paddingRight: 32, // Компенсируем место для стрелочки
                }}
            >
                <UserRoleTag role={participant.user_role} tooltip="" />
            </div>
        );
    }

    return (
        <Select
            value={participant.user_role}
            style={{ width: selectWidth }}
            onChange={(newRole) => onRoleChange(participant, newRole)}
            size="small"
            variant="borderless"
            styles={{
                popup: {
                    root: { padding: '4px 0' }
                }
            }}
        >
            <Option value={UserRole.Admin} style={{ padding: '6px 12px' }}>
                <UserRoleTag role={UserRole.Admin} tooltip="" />
            </Option>
            <Option value={UserRole.Participant} style={{ padding: '6px 12px' }}>
                <UserRoleTag role={UserRole.Participant} tooltip="" />
            </Option>
        </Select>
    );
} 