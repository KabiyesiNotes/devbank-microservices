# ---- Build/install stage ----
FROM node:18-alpine AS build

WORKDIR /app

# Copy package files and install production dependencies
COPY package*.json ./
RUN npm install --omit=dev

# ---- Runtime stage ----
FROM node:18-alpine

WORKDIR /app

# Create a non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy node_modules from build stage and application source
COPY --from=build /app/node_modules ./node_modules
COPY server.js ./
COPY public   ./public

RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 3000

# BACKEND_URL is supplied at runtime via environment variable.
# Docker Compose sets: BACKEND_URL=http://backend:8080
# AWS ECS will set the appropriate internal endpoint.
ENV BACKEND_URL=http://backend:8080

CMD ["node", "server.js"]
