#!/bin/bash
set -e

DOCKER_COMPOSE_PATH="/home/ubuntu/genieary/docker-compose.yml"
NGINX_CONF_PATH="/home/ubuntu/genieary/nginx.conf"

CURRENT_CONTAINER=$(docker ps --format '{{.Names}}' | grep 'genieary-app-')

if [ "$CURRENT_CONTAINER" = "genieary-app-blue" ]; then
  echo "Blue에서 → Green으로 전환"
  docker-compose -f ${DOCKER_COMPOSE_PATH} up -d --build --no-deps green
  TARGET_PORT=8082
  TARGET_CONTAINER="genieary-app-green"
else
  echo "Green에서 → Blue로 전환"
  docker-compose -f ${DOCKER_COMPOSE_PATH} up -d --build --no-deps blue
  TARGET_PORT=8081
  TARGET_CONTAINER="genieary-app-blue"
fi

for i in {1..30}; do
  STATUS=$(docker inspect --format='{{.State.Health.Status}}' ${TARGET_CONTAINER})
  if [ "$STATUS" = "healthy" ]; then
    echo "${TARGET_CONTAINER} is healthy!"
    break
  fi
  echo "Waiting for ${TARGET_CONTAINER}... (status: $STATUS)"
  sleep 5
done || { echo "New container failed to become healthy."; exit 1; }

# nginx upstream 설정 변경
if [ "$CURRENT_CONTAINER" = "genieary-app-blue" ]; then
  echo "Switching nginx to green"
  sed -i 's/server blue:8080;/# server blue:8080;/' ${NGINX_CONF_PATH}
  sed -i 's/# server green:8080;/server green:8080;/' ${NGINX_CONF_PATH}
else
  echo "Switching nginx to blue"
  sed -i 's/server green:8080;/# server green:8080;/' ${NGINX_CONF_PATH}
  sed -i 's/# server blue:8080;/server blue:8080;/' ${NGINX_CONF_PATH}
fi

echo "Reloading Nginx by recreating the container..."
docker-compose -f ${DOCKER_COMPOSE_PATH} up -d --force-recreate nginx

if [ "$CURRENT_CONTAINER" = "genieary-app-blue" ]; then
  docker-compose -f ${DOCKER_COMPOSE_PATH} down --remove-orphans blue
else
  docker-compose -f ${DOCKER_COMPOSE_PATH} down --remove-orphans green
fi

echo "배포 완료"