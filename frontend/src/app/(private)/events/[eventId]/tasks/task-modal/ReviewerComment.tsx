'use client';

import React from 'react';
import { Typography } from 'antd';
import { MessageOutlined } from '@ant-design/icons';

const { Title, Paragraph } = Typography;

interface ReviewerCommentProps {
    reviewerComment: string;
}

export default function ReviewerComment({ reviewerComment }: ReviewerCommentProps) {
    if (!reviewerComment) {
        return null;
    }

    return (
        <div style={{ marginBottom: '24px' }}>
            <div style={{ display: 'flex', alignItems: 'center', marginBottom: '12px' }}>
                <MessageOutlined style={{ color: '#8c8c8c', marginRight: '8px' }} />
                <Title level={5} style={{ margin: 0 }}>
                    Комментарий проверяющего
                </Title>
            </div>

            <Paragraph
                style={{
                    minHeight: '69px',
                    backgroundColor: '#fafafa',
                    padding: '12px',
                    borderRadius: '6px',
                    margin: 0,
                    whiteSpace: 'pre-wrap'
                }}
            >
                {reviewerComment}
            </Paragraph>
        </div>
    );
} 