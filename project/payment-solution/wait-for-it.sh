#!/bin/sh
set -e

host="${POSTGRES_HOST:-localhost}"
port="${POSTGRES_PORT:-5432}"
cmd="$@"

# Check if DB_LIVENESS_ENABLED is set to true
if [ "$DB_LIVENESS_ENABLED" = "true" ]; then
  until pg_isready -h "$host" -p $port; do
    >&2 echo "Postgres is unavailable - sleeping"
    sleep 1
  done
  >&2 echo "Postgres is up - executing command"
else
  >&2 echo "DB liveness check is disabled - executing command"
fi

exec $cmd