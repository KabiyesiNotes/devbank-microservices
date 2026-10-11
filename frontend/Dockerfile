# ─────────────────────────────────────────────────────────────────────────────
# DevBank Inventory – Frontend Container
#
# Serves the static UI through Nginx on port 3000.
#
# The BACKEND_URL environment variable is injected at container start-up by
# docker-entrypoint.sh, which writes /usr/share/nginx/html/env-config.js.
# This allows the backend URL to be changed without rebuilding the image.
#
# docker-compose:  BACKEND_URL=http://backend:8080
# AWS ECS:         BACKEND_URL=http://<service-discovery-or-alb-dns>:8080
# ─────────────────────────────────────────────────────────────────────────────
FROM nginx:1.25-alpine

# Remove the default Nginx config and welcome page
RUN rm -rf /etc/nginx/conf.d/* \
    && rm -f /usr/share/nginx/html/*

# Copy custom Nginx site config (listens on port 3000)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy static frontend assets
COPY index.html /usr/share/nginx/html/index.html

# Copy and configure the entrypoint script
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

EXPOSE 3000

# The entrypoint script writes env-config.js then execs nginx
ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]
