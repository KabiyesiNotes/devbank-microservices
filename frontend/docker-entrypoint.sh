#!/bin/sh
# docker-entrypoint.sh
#
# Generates /usr/share/nginx/html/env-config.js at container start-up.
# This file exposes runtime environment variables to the browser-side
# JavaScript without rebuilding the Docker image.
#
# Usage in docker-compose.yml / ECS task definition:
#   environment:
#     BACKEND_URL: http://backend:8080
#
# The generated file will look like:
#   window.ENV = { "BACKEND_URL": "http://backend:8080" };
#

set -e

# Default fallback only when BACKEND_URL is completely unset (not when empty).
# Use ${VAR-default} rather than ${VAR:-default}: the latter treats an empty
# string as "unset", which would override an intentional BACKEND_URL="" set in
# docker-compose.yml.  An empty BACKEND_URL tells the browser to use the Nginx
# proxy at /api/proxy/* on the same origin.
BACKEND_URL="${BACKEND_URL-http://backend:8080}"

cat > /usr/share/nginx/html/env-config.js <<EOF
// Auto-generated at container start-up by docker-entrypoint.sh
// DO NOT EDIT – changes will be overwritten on the next container start.
window.ENV = {
  "BACKEND_URL": "${BACKEND_URL}"
};
EOF

echo "[entrypoint] env-config.js written with BACKEND_URL=${BACKEND_URL}"

# Hand off to the CMD defined in the Dockerfile (nginx in foreground)
exec "$@"
