'use client';

import React from 'react';
import { Button, Typography } from 'antd';
import { CheckOutlined, CloseOutlined } from '@ant-design/icons';
import styles from './EditableField.module.css';

const { Text } = Typography;

interface EditableFieldProps {
    label: string;
    hasChanges: boolean;
    isLoading: boolean;
    onSave: () => void;
    onReset: () => void;
    children: React.ReactNode;
}

export default function EditableField({
    label,
    hasChanges,
    isLoading,
    onSave,
    onReset,
    children
}: EditableFieldProps) {
    return (
        <div className={styles.fieldContainer}>
            <Text strong className={styles.label}>
                {label}
            </Text>
            <div className={styles.inputWrapper}>
                <div className={styles.inputContent}>
                    {children}
                </div>
                <div className={`${styles.buttonContainer} ${hasChanges ? styles.visible : styles.hidden}`}>
                    <Button
                        type="text"
                        icon={<CheckOutlined />}
                        onClick={onSave}
                        loading={isLoading}
                        size="small"
                    />
                    <Button
                        type="text"
                        icon={<CloseOutlined />}
                        onClick={onReset}
                        disabled={isLoading}
                        size="small"
                    />
                </div>
            </div>
        </div>
    );
} 