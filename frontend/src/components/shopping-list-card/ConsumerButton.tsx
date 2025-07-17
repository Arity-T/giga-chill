import React, { useState } from 'react';
import { Typography, Tooltip } from 'antd';
import { UserAddOutlined } from '@ant-design/icons';

const { Text } = Typography;

interface ConsumerButtonProps {
    consumersCount: number;
    onAddConsumers: () => void;
}

export default function ConsumerButton({ consumersCount, onAddConsumers }: ConsumerButtonProps) {
    const [isHovered, setIsHovered] = useState(false);

    return (
        <Tooltip title="Добавить потребителей">
            <div
                style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '4px',
                    padding: '4px 8px',
                    borderRadius: '6px',
                    cursor: 'pointer',
                    backgroundColor: isHovered ? '#f5f5f5' : 'transparent',
                    transition: 'background-color 0.2s ease'
                }}
                onMouseEnter={() => setIsHovered(true)}
                onMouseLeave={() => setIsHovered(false)}
                onClick={(e) => {
                    e.stopPropagation();
                    onAddConsumers();
                }}
            >
                <UserAddOutlined style={{ fontSize: '14px', color: '#000' }} />
                <Text style={{ fontSize: '14px', color: '#000', fontWeight: 500 }}>
                    {consumersCount}
                </Text>
            </div>
        </Tooltip>
    );
} 