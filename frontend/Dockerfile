# TODO: https://github.com/vercel/next.js/blob/canary/examples/with-docker/Dockerfile

# 1. Build stage
FROM node:22-alpine AS builder

WORKDIR /app

# Install dependencies
COPY frontend/package.json frontend/package-lock.json ./frontend/
RUN cd frontend && npm ci

# Copy API schema
COPY openapi/ ./openapi/

# Copy other frontend files
COPY frontend/ ./frontend/

# Generate code from API schema and build frontend
WORKDIR /app/frontend
RUN npm run codegen
RUN npm run build

# 2. Production image
FROM node:22-alpine AS runner
WORKDIR /app

# Если используете public/static файлы, скопируйте их
COPY --from=builder /app/frontend/public ./public
COPY --from=builder /app/frontend/.next ./.next
COPY --from=builder /app/frontend/node_modules ./node_modules
COPY --from=builder /app/frontend/package.json ./package.json

CMD ["npm", "start"]