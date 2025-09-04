#!/usr/bin/env bash
set -euo pipefail

if [[ -f ".env" ]]; then
  source ".env"
fi

# Required env
: "${MINIO_ALIAS:?MINIO_ALIAS is required}"
: "${MC_HOST_GIGACHILL:?MC_HOST_GIGACHILL is required}"
: "${SVC_USER:?SVC_USER is required}"
: "${SVC_PASSWORD:?SVC_PASSWORD is required}"
: "${BUCKET_INCOMING:?BUCKET_INCOMING is required}"
: "${BUCKET_RECEIPT:?BUCKET_RECEIPT is required}"

# Идемпотентное создание бакетов
create_bucket() {
  local b="$1"
  if mc ls "${MINIO_ALIAS}/${b}" >/dev/null 2>&1; then
    echo "Bucket '${b}' already exists"
  else
    echo "Creating bucket '${b}'"
    mc mb "${MINIO_ALIAS}/${b}"
  fi
}
create_bucket "${BUCKET_INCOMING}"
create_bucket "${BUCKET_RECEIPT}"

# Применение ILM к 'incoming' бакету
if [[ ! -f "ilm/incoming.json" ]]; then
  echo "ilm/incoming.json not found"
  exit 1
fi
echo "Applying ILM to '${BUCKET_INCOMING}' from ilm/incoming.json"
mc ilm import "${MINIO_ALIAS}/${BUCKET_INCOMING}" < "ilm/incoming.json"

# Создание/обновление пользователя в MinIO
if mc admin user info "${MINIO_ALIAS}" "${SVC_USER}" >/dev/null 2>&1; then
  echo "User '${SVC_USER}' already exists, updating password and enabling"
  mc admin user disable "${MINIO_ALIAS}" "${SVC_USER}" || true
  mc admin user add "${MINIO_ALIAS}" "${SVC_USER}" "${SVC_PASSWORD}"
  mc admin user enable "${MINIO_ALIAS}" "${SVC_USER}"
else
  echo "Creating user '${SVC_USER}'"
  mc admin user add "${MINIO_ALIAS}" "${SVC_USER}" "${SVC_PASSWORD}"
fi

# Создание временного файла с политикой прав для сервиса
if [[ ! -f "policies/backend-policy-template.json" ]]; then
  echo "policies/backend-policy-template.json not found"
  exit 1
fi
echo "Building service policy from policies/backend-policy-template.json"
tmp_policy="$(mktemp)"
trap 'rm -f "${tmp_policy}"' EXIT
sed -e "s|\${BUCKET_INCOMING}|${BUCKET_INCOMING}|g" \
    -e "s|\${BUCKET_RECEIPT}|${BUCKET_RECEIPT}|g" \
    "policies/backend-policy-template.json" > "${tmp_policy}"

# Создание/обновление политики в MinIO
policy_name="${SVC_USER}-policy"
echo "Create/update policy '${policy_name}'"
mc admin policy create "${MINIO_ALIAS}" "${policy_name}" "${tmp_policy}"

# Прикрепление политики к пользователю
echo "Attaching policy '${policy_name}' to user '${SVC_USER}'"
mc admin policy attach "${MINIO_ALIAS}" "${policy_name}" --user "${SVC_USER}"

echo "MinIO setup finished."