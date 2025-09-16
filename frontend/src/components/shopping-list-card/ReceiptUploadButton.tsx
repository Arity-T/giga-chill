import React, { useMemo, useRef, useState } from "react";
import { Button, Image, Space, App, Popconfirm } from "antd";
import { UploadOutlined, DeleteOutlined } from "@ant-design/icons";
import SparkMD5 from "spark-md5";
import { getReceiptImagePath, useCreateReceiptUploadPolicyMutation, useConfirmReceiptUploadMutation, useDeleteReceiptMutation, Content_type, type ReceiptConfirmRequest } from '@/store/api';


interface ReceiptUploadButtonProps {
    eventId: string;
    shoppingListId: string;
    can_edit?: boolean;
    receiptId?: string;
}

export default function ReceiptUploadButton({
    eventId,
    shoppingListId,
    can_edit = true,
    receiptId,
}: ReceiptUploadButtonProps) {
    const accept = "image/jpeg,image/png";
    const maxBytes = 10 * 1024 * 1024;

    const { message } = App.useApp();
    const fileInputRef = useRef<HTMLInputElement | null>(null);

    const [isBusy, setIsBusy] = useState(false);

    const [createPolicy] = useCreateReceiptUploadPolicyMutation();
    const [confirmUpload] = useConfirmReceiptUploadMutation();
    const [deleteReceipt] = useDeleteReceiptMutation();

    const disabled = useMemo(() => !can_edit || isBusy, [can_edit, isBusy]);

    const handleChooseClick = () => {
        if (!can_edit) return;
        fileInputRef.current?.click();
    };

    const handleFileSelected: React.ChangeEventHandler<HTMLInputElement> = async (
        e
    ) => {
        const file = e.target.files?.[0];
        // Сбрасываем значение, чтобы повторный выбор того же файла тоже срабатывал
        e.currentTarget.value = "";
        if (!file) return;

        if (!accept.split(",").some((t) => file.type === t)) {
            message.error("Неверный тип файла. Разрешены: " + accept);
            return;
        }
        if (file.size > maxBytes) {
            message.error(
                `Файл слишком большой. Лимит ${(maxBytes / (1024 * 1024)).toFixed(
                    1
                )} МБ`
            );
            return;
        }

        try {
            setIsBusy(true);

            const md5Base64 = await computeMd5Base64(file);

            const policy = await createPolicy({
                eventId,
                shoppingListId,
                receiptUploadPolicyCreate: {
                    content_type: file.type as Content_type,
                    content_length: file.size,
                    original_file_name: file.name,
                    md5_hash: md5Base64,
                },
            }).unwrap();

            const form = new FormData();
            Object.entries(policy.fields).forEach(([k, v]) => form.append(k, v));
            form.append("file", file);

            const s3Resp = await fetch(policy.upload_url, { method: "POST", body: form });

            if (!(s3Resp.status === 204 || s3Resp.ok)) {
                const txt = await safeText(s3Resp);
                throw new Error(`S3 ответ ${s3Resp.status}: ${txt || "ошибка загрузки"}`);
            }

            await confirmUpload({
                eventId,
                shoppingListId,
                receiptConfirmRequest: {
                    receipt_id: policy.receipt_id,
                },
            }).unwrap();

            message.success("Чек загружен");
        } catch (error) {
            console.error(error);
            message.error("Не удалось загрузить фото чека");
        } finally {
            setIsBusy(false);
        }
    };

    const handleDelete = async () => {
        if (!receiptId) return;
        try {
            setIsBusy(true);
            await deleteReceipt({
                eventId,
                shoppingListId,
                receiptId,
            }).unwrap();
            message.success("Чек удалён");
        } catch (error) {
            message.error("Не удалось удалить чек");
        } finally {
            setIsBusy(false);
        }
    };

    return (
        <Space direction="vertical" size="small">
            {receiptId ? (
                <Image
                    src={getReceiptImagePath({ eventId, shoppingListId, receiptId })}
                    width={200}
                    height={200}
                    style={{ objectFit: "cover", borderRadius: 8 }}
                    alt="Квитанция"
                />
            ) : (
                <div style={{
                    width: 200, height: 200, border: "1px dashed #d9d9d9",
                    borderRadius: 8, display: "flex", alignItems: "center",
                    justifyContent: "center", color: "#999"
                }}>
                    Нет изображения
                </div>
            )}

            <Space>
                <Button
                    icon={<UploadOutlined />}
                    onClick={handleChooseClick}
                    disabled={disabled}
                    loading={isBusy}
                >
                    Загрузить чек
                </Button>

                <input
                    ref={fileInputRef}
                    type="file"
                    accept={accept}
                    style={{ display: "none" }}
                    onChange={handleFileSelected}
                />

                {can_edit && receiptId && (
                    <Popconfirm
                        title="Удалить чек?"
                        okText="Удалить"
                        cancelText="Отмена"
                        onConfirm={handleDelete}
                    >
                        <Button
                            danger
                            icon={<DeleteOutlined />}
                            disabled={disabled}
                            loading={isBusy}
                        >
                            Удалить
                        </Button>
                    </Popconfirm>
                )}
            </Space>
        </Space>
    );
}


async function computeMd5Base64(file: File): Promise<string> {
    // SparkMD5 выдаёт hex. Конвертируем hex -> base64, т.к. для S3 Content-MD5 требуется base64.
    const buf = await file.arrayBuffer();
    const hex = SparkMD5.ArrayBuffer.hash(buf); // строка hex длиной 32 байта * 2 символа = 64 символа
    const bytes = hexToBytes(hex);
    // Преобразуем в base64
    let binary = "";
    for (let i = 0; i < bytes.length; i++) binary += String.fromCharCode(bytes[i]);
    return btoa(binary);
}

function hexToBytes(hex: string): Uint8Array {
    if (hex.length % 2 !== 0) throw new Error("Неверная hex-строка");
    const arr = new Uint8Array(hex.length / 2);
    for (let i = 0; i < hex.length; i += 2) {
        arr[i / 2] = parseInt(hex.slice(i, i + 2), 16);
    }
    return arr;
}

async function safeText(resp: Response) {
    try {
        return await resp.text();
    } catch {
        return "";
    }
}