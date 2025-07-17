'use client';

import React from 'react';
import { Typography } from 'antd';
import InlineEditControls from '@/components/inline-edit-controls';
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
                <div className={styles.buttonContainer}>
                    <InlineEditControls
                        hasChanges={hasChanges}
                        isLoading={isLoading}
                        onSave={onSave}
                        onReset={onReset}
                    />
                </div>
            </div>
        </div>
    );
} 