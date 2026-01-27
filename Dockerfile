# syntax=docker/dockerfile:1.6
FROM debian:12-slim AS base

WORKDIR /app
RUN mkdir -p /app/data

EXPOSE 8000

FROM base AS branch-amd64
COPY --chmod=755 target/push-server-amd64 /app/push-server

FROM base AS branch-arm64
COPY --chmod=755 target/push-server-arm64 /app/push-server

FROM branch-${TARGETARCH} AS final
ENTRYPOINT ["/app/push-server"]