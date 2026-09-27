FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY public ./public
COPY src ./src
COPY server.js ./
RUN npm run build

FROM node:20-alpine
WORKDIR /app
COPY --from=build /app/build ./build
COPY --from=build /app/node_modules ./node_modules
COPY --from=build /app/package*.json ./
COPY --from=build /app/server.js ./
EXPOSE 3000
CMD ["node", "server.js"]
