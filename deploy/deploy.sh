#!/usr/bin/env bash
set -euo pipefail
cd /opt/nira
git fetch origin
git reset --hard origin/main          # ветка main у Ниры
docker compose -f compose.nira.yml pull
docker compose -f compose.nira.yml up -d

# health-check: у бота нет HTTP-эндпоинта, проверяем что контейнер жив и не рестартует
sleep 10
STATE=$(docker inspect -f '{{.State.Status}}' nira 2>/dev/null || echo "missing")
RESTARTS=$(docker inspect -f '{{.RestartCount}}' nira 2>/dev/null || echo "0")

if [ "$STATE" = "running" ]; then
  echo "Nira is running (restarts: $RESTARTS)"
  # покажем последние логи для подтверждения старта
  docker compose -f compose.nira.yml logs --tail=30 nira
  exit 0
else
  echo "Nira failed to start (state: $STATE)"
  docker compose -f compose.nira.yml logs --tail=100 nira
  exit 1
fi