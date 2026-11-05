# Deepfake A.I Detector Frontend — Multi-stage: Node build → nginx serve

# --- Stage 1: Build ---
FROM node:20-alpine AS builder

WORKDIR /app

COPY package.json package-lock.json* ./
RUN npm ci || npm install

COPY . .
RUN npm run build

# --- Stage 2: Serve ---
FROM nginx:alpine

RUN apk add --no-cache curl

# Copy built static files
COPY --from=builder /app/dist /usr/share/nginx/html

# Copy nginx config with API proxy
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

HEALTHCHECK --interval=15s --timeout=5s --retries=3 \
    CMD curl -f http://localhost/ || exit 1

CMD ["nginx", "-g", "daemon off;"]
