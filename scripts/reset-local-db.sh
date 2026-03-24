#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-lessonring}"
DB_USER="${DB_USER:-devyn}"
DB_PASSWORD="${DB_PASSWORD:-}"
SPRING_PROFILE="${SPRING_PROFILE:-local}"
BACKUP_DIR="${BACKUP_DIR:-$ROOT_DIR/backups}"
RUN_SEED="${RUN_SEED:-true}"
BOOT_WAIT_SECONDS="${BOOT_WAIT_SECONDS:-120}"

TIMESTAMP="$(date +%Y%m%d-%H%M%S)"
BACKUP_FILE="$BACKUP_DIR/${DB_NAME}-${TIMESTAMP}.dump"

mkdir -p "$BACKUP_DIR"

export PGPASSWORD="$DB_PASSWORD"

echo "[1/5] Backing up database to $BACKUP_FILE"
pg_dump \
  --host "$DB_HOST" \
  --port "$DB_PORT" \
  --username "$DB_USER" \
  --format custom \
  --file "$BACKUP_FILE" \
  "$DB_NAME"

echo "[2/5] Recreating public schema"
psql \
  --host "$DB_HOST" \
  --port "$DB_PORT" \
  --username "$DB_USER" \
  --dbname "$DB_NAME" \
  --command "DROP SCHEMA IF EXISTS public CASCADE; CREATE SCHEMA public;"

echo "[3/5] Running application to let Flyway migrate schema"
cd "$ROOT_DIR"
./gradlew bootRun --args="--spring.profiles.active=$SPRING_PROFILE" > /tmp/lessonring-bootrun.log 2>&1 &
APP_PID=$!

cleanup() {
  if kill -0 "$APP_PID" >/dev/null 2>&1; then
    kill "$APP_PID" >/dev/null 2>&1 || true
    wait "$APP_PID" >/dev/null 2>&1 || true
  fi
}

trap cleanup EXIT

deadline=$((SECONDS + BOOT_WAIT_SECONDS))
while (( SECONDS < deadline )); do
  if psql \
    --host "$DB_HOST" \
    --port "$DB_PORT" \
    --username "$DB_USER" \
    --dbname "$DB_NAME" \
    --tuples-only \
    --no-align \
    --command "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'flyway_schema_history';" \
    2>/dev/null | grep -q '^1$'; then
    break
  fi

  if ! kill -0 "$APP_PID" >/dev/null 2>&1; then
    echo "Application exited before Flyway completed. Check /tmp/lessonring-bootrun.log"
    exit 1
  fi

  sleep 2
done

if ! psql \
  --host "$DB_HOST" \
  --port "$DB_PORT" \
  --username "$DB_USER" \
  --dbname "$DB_NAME" \
  --tuples-only \
  --no-align \
  --command "SELECT COUNT(*) FROM flyway_schema_history WHERE success = true;" \
  2>/dev/null | grep -Eq '^[1-9][0-9]*$'; then
  echo "Flyway migration did not complete successfully within ${BOOT_WAIT_SECONDS}s. Check /tmp/lessonring-bootrun.log"
  exit 1
fi

echo "[4/5] Stopping application after Flyway migration"
cleanup
trap - EXIT

if [[ "$RUN_SEED" == "true" ]]; then
  echo "[5/5] Seeding test data"
  DB_HOST="$DB_HOST" DB_PORT="$DB_PORT" DB_NAME="$DB_NAME" DB_USER="$DB_USER" DB_PASSWORD="$DB_PASSWORD" \
    "$ROOT_DIR/scripts/seed-test-data.sh"
else
  echo "[5/5] Skipping seed data because RUN_SEED=$RUN_SEED"
fi

echo
echo "Reset complete."
echo "Backup: $BACKUP_FILE"
