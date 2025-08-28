import hashlib
import mimetypes
import os
import sys
import uuid

import boto3
import requests
from dotenv import load_dotenv


def main():
    load_dotenv()
    s3_api_url = os.getenv("S3_API_URL")
    access_key = os.getenv("SVC_USER")
    secret_key = os.getenv("SVC_PASSWORD")
    bucket_incoming = os.getenv("BUCKET_INCOMING")
    bucket_receipt = os.getenv("BUCKET_RECEIPT")

    img_path = "test.jpg"
    if not os.path.isfile(img_path):
        print("Файл не найден.")
        sys.exit(1)

    # Читаем файл, считаем MD5 и генерируем уникальный ключ объекта
    with open(img_path, "rb") as f:
        data = f.read()
    size = len(data)
    md5hex = hashlib.md5(data).hexdigest()
    ctype = mimetypes.guess_type(img_path)[0] or "application/octet-stream"
    if not ctype.startswith("image/"):
        print("Файл не является изображением.")
        sys.exit(1)

    _, ext = os.path.splitext(img_path)
    key = f"receipts/{uuid.uuid4()}{ext}"
    print(f"Ключ объекта: {key}")
    print()

    s3 = boto3.client(
        "s3",
        endpoint_url=s3_api_url,
        aws_access_key_id=access_key,
        aws_secret_access_key=secret_key,
    )

    print()
    print("(1) BACKEND: Создаём pre-signed POST (10 МБ максимум)")
    post = s3.generate_presigned_post(
        Bucket=bucket_incoming,
        Key=key,
        Fields={
            "Content-Type": ctype,
            "x-amz-meta-md5": md5hex,
        },
        Conditions=[
            {"Content-Type": ctype},
            {"x-amz-meta-md5": md5hex},
            ["content-length-range", 1, 10 * 1024 * 1024],
        ],
        ExpiresIn=300,
    )
    print(f"URL: {post['url']}")
    print(f"Fields: {post['fields']}")
    print()

    print(
        "(2) FRONTEND: Загружаем объект в MinIO с помощью multipart/form-data запроса"
    )
    r = requests.post(
        post["url"],
        data=post["fields"],
        files={"file": (os.path.basename(img_path), data, ctype)},
        timeout=60,
    )
    if not (200 <= r.status_code < 300):
        print(f"Загрузка не удалась: HTTP {r.status_code}: {r.text[:400]}")
        sys.exit(1)
    print(f"Загружено в {bucket_incoming}/{key}")
    print()

    print("(3) BACKEND: Выполняем HEAD запрос и проверяем наличие объекта")
    meta = s3.head_object(Bucket=bucket_incoming, Key=key)
    etag = meta["ETag"].strip('"')
    if etag != md5hex:
        sys.exit(1)
    print("Хэш загруженного объекта совпадает с хэшем исходного файла")
    print()

    print("(4) BACKEND: Копируем объект в receipt и удаляем из incoming")
    s3.copy_object(
        Bucket=bucket_receipt,
        Key=key,
        CopySource={"Bucket": bucket_incoming, "Key": key},
    )
    s3.delete_object(Bucket=bucket_incoming, Key=key)
    print()

    print("(5) BACKEND: Генерируем pre-signed GET")
    url = s3.generate_presigned_url(
        "get_object", Params={"Bucket": bucket_receipt, "Key": key}, ExpiresIn=300
    )
    print(f"URL: {url}")
    print()

    print("(6) FRONTEND: Скачиваем объект с помощью pre-signed GET")
    out_name = f"downloaded_{os.path.basename(key)}"
    g = requests.get(url, timeout=60)
    g.raise_for_status()
    with open(out_name, "wb") as f:
        f.write(g.content)
    print(f"Объект сохранён в файл '{out_name}'")
    print()


if __name__ == "__main__":
    main()
