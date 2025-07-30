import { Card, Typography } from 'antd';
import type { ReactNode } from 'react';
import styles from './AuthWrapper.module.css';

const { Title } = Typography;

interface AuthWrapperProps {
  title: string;
  children: ReactNode;
}

export default function AuthWrapper({ title, children }: AuthWrapperProps) {
  return (
    <div className={styles.container}>
      <Card className={styles.authCard}>
        <Title level={2} className={styles.title}>
          {title}
        </Title>
        {children}
      </Card>
    </div>
  );
} 