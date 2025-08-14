# TODO: https://github.com/vercel/next.js/blob/canary/examples/with-docker/Dockerfile
# 1. Build stage
FROM node:22-alpine AS builder

WORKDIR /app/frontend

# Install dependencies
COPY package.json package-lock.json ./
RUN npm ci

# Copy API schema
COPY --from=openapi . /app/openapi/

# Copy other frontend files
COPY . .

# Generate code from API schema
RUN npm run codegen

# Build frontend
ARG NEXT_PUBLIC_API_BASE_URL
ARG NEXT_PUBLIC_BASE_URL
RUN npm run build

# 2. Production image
FROM node:22-alpine AS runner
WORKDIR /app

ENV NODE_ENV=production

# Если используете public/static файлы, скопируйте их
COPY --from=builder /app/frontend/public ./public
COPY --from=builder /app/frontend/.next ./.next
COPY --from=builder /app/frontend/node_modules ./node_modules
COPY --from=builder /app/frontend/package.json ./package.json

CMD ["npm", "start"]