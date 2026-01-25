# syntax=docker/dockerfile:1.6
FROM debian:12-slim

WORKDIR /app
COPY --chmod=755 target/push-server /app/push-server

RUN mkdir -p /app/data

EXPOSE 8000
ENTRYPOINT ["/app/push-server"]
