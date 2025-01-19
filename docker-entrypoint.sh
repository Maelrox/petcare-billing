#!/bin/sh
set -e

# Function to sanitize input by removing prohibited characters
sanitize_input() {
  echo "$1" | tr -d '[:space:]' | tr -d ';' | tr -d "'" | tr -d '"' | tr -d ','
}

# Read and sanitize secrets
export DB_USERNAME=$(sanitize_input "$(cat /run/secrets/db_user || echo "")")
export DB_PASSWORD=$(sanitize_input "$(cat /run/secrets/db_password || echo "")")
export REDIS_PASSWORD=$(sanitize_input "$(cat /run/secrets/redis_password || echo "")")
export JWT_SECRET_KEY=$(sanitize_input "$(cat /run/secrets/jwt_secret_key || echo "")")
export DB_HOST=$(sanitize_input "$(cat /run/secrets/db_host || echo "")")
export REDIS_PASSWORD=$(sanitize_input "$(cat /run/secrets/redis_password || echo "")")
export REDIS_PASSWORD=$(sanitize_input "$(cat /run/secrets/redis_password || echo "")")
export REDIS_PASSWORD=$(sanitize_input "$(cat /run/secrets/redis_password || echo "")")

# Log for debugging (optional)
echo "DB_USER set to: $DB_USERNAME"
echo "DB_HOST set to: $DB_HOST"
echo "DB_PASSWORD set to: $DB_PASSWORD"

# Execute the CMD
exec "$@"
