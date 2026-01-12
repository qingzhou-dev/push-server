# syntax=docker/dockerfile:1.6
FROM debian:12-slim

WORKDIR /app
COPY --chmod=755 target/push-server /app/push-server

EXPOSE 8000
USER 65532:65532
ENTRYPOINT ["/app/push-server"]
