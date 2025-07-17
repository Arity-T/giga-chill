'use client';

import React from 'react';
import { Button } from 'antd';
import { CheckOutlined, CloseOutlined } from '@ant-design/icons';

interface InlineEditControlsProps {
    hasChanges: boolean;
    isLoading: boolean;
    onSave: () => void;
    onReset: () => void;
    style?: React.CSSProperties;
}

export default function InlineEditControls({
    hasChanges,
    isLoading,
    onSave,
    onReset,
    style
}: InlineEditControlsProps) {
    if (!hasChanges) {
        return null;
    }

    return (
        <div style={{ display: 'flex', gap: '4px', ...style }}>
            <Button
                type="text"
                icon={<CheckOutlined style={{ color: '#8c8c8c' }} />}
                onClick={onSave}
                loading={isLoading}
                size="small"
            />
            <Button
                type="text"
                icon={<CloseOutlined style={{ color: '#8c8c8c' }} />}
                onClick={onReset}
                disabled={isLoading}
                size="small"
            />
        </div>
    );
} 