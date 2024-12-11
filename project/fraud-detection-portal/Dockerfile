# Stage 1: Build the application
FROM node:22-alpine AS builder

RUN apk update && apk upgrade && adduser -D svelteuser
USER svelteuser

WORKDIR /app

# Copy package files
COPY --chown=svelteuser:svelteuser . /app
RUN npm install && npm run build


# Stage 2: Run the application
FROM node:22-alpine AS deployer

RUN apk update && apk upgrade && apk add dumb-init && adduser -D svelteuser
USER svelteuser

WORKDIR /app

# Copy package files
COPY --from=builder --chown=svelteuser:svelteuser /app/build /app/package.json ./

EXPOSE 8080

ENV HOST=0.0.0.0
ENV PORT=3000
ENV NODE_ENV=production
ENV VITE_PAYMENT_REQUEST_API_URL=http://localhost:8080

CMD ["dumb-init", "node", "index.js"]