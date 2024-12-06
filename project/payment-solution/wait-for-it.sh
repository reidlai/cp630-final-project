#!/bin/sh
set -e

host="${POSTGRES_HOST:-localhost}"
port="${POSTGRES_PORT:-5432}"
cmd="$@"

until pg_isready -h "$host" -p $port; do
  >&2 echo "Postgres is unavailable - sleeping"
  sleep 1
done

>&2 echo "Postgres is up - executing command"
exec $cmd