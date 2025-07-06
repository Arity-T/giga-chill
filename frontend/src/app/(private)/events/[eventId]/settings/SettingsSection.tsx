'use client';

import React from 'react';
import { Card, Typography } from 'antd';

const { Title } = Typography;

interface SettingsSectionProps {
    title: string;
    children: React.ReactNode;
}

export default function SettingsSection({ title, children }: SettingsSectionProps) {
    return (
        <Card
            title={<Title level={4} style={{ margin: 0, paddingTop: 8, paddingBottom: 8 }}>{title}</Title>}
            size="small"
        >
            {children}
        </Card>
    );
} 