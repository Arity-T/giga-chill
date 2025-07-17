import React from 'react';
import { Button, Space, Tooltip } from 'antd';
import { DeleteOutlined, EditOutlined } from '@ant-design/icons';

interface ActionButtonsProps {
    isVisible: boolean;
    onEdit: () => void;
    onDelete: () => void;
}

export default function ActionButtons({ isVisible, onEdit, onDelete }: ActionButtonsProps) {
    return (
        <Space style={{ opacity: isVisible ? 1 : 0, transition: 'opacity 0.2s ease' }}>
            <Tooltip title="Редактировать список">
                <Button
                    type="text"
                    icon={<EditOutlined style={{ color: '#8c8c8c' }} />}
                    size="small"
                    onClick={(e) => {
                        e.stopPropagation();
                        onEdit();
                    }}
                />
            </Tooltip>
            <Button
                type="text"
                icon={<DeleteOutlined style={{ color: '#8c8c8c' }} />}
                size="small"
                onClick={(e) => {
                    e.stopPropagation();
                    onDelete();
                }}
            />
        </Space>
    );
} 