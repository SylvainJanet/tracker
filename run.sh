#!/usr/bin/env bash

set -euo pipefail

readonly PROJECT_DIRECTORY="$(
    cd -- "$(dirname -- "${BASH_SOURCE[0]}")"
    pwd
)"
readonly GRADLEW="$PROJECT_DIRECTORY/gradlew"

mode="normal"
database="local"

usage() {
    printf '%s\n' \
        "Usage: ./run.sh [--mode=normal|debug] [--database=local|empty|real]" \
        "" \
        "Defaults:" \
        "  --mode=normal" \
        "  --database=local"
}

fail() {
    printf 'Error: %s\n\n' "$1" >&2
    usage >&2
    exit 2
}

while (($# > 0)); do
    case "$1" in
        --mode)
            [[ $# -ge 2 ]] || fail "Missing value for --mode."
            mode="$2"
            shift 2
            ;;
        --mode=*)
            mode="${1#*=}"
            shift
            ;;
        --database)
            [[ $# -ge 2 ]] || fail "Missing value for --database."
            database="$2"
            shift 2
            ;;
        --database=*)
            database="${1#*=}"
            shift
            ;;
        -h | --help)
            usage
            exit 0
            ;;
        *)
            fail "Unknown option: $1"
            ;;
    esac
done

case "$mode" in
    normal)
        mode_description="NORMAL"
        debug_argument=""
        ;;
    debug)
        mode_description="DEBUG"
        debug_argument=" --debug-jvm"
        ;;
    *)
        fail "Unsupported mode: $mode"
        ;;
esac

case "$database" in
    local)
        backend_task="bootRun"
        database_description="LOCAL SNAPSHOT"
        database_location="$PROJECT_DIRECTORY/backend/build/dev-database/tracker.db"
        database_safety="Disposable snapshot of the real database"
        ;;
    empty)
        backend_task="bootRunEmpty"
        database_description="EMPTY"
        database_location="$PROJECT_DIRECTORY/backend/build/dev-database/tracker.db"
        database_safety="Fresh disposable empty database"
        ;;
    real)
        backend_task="bootRunReal"
        database_description="REAL"
        database_location="$PROJECT_DIRECTORY/data/tracker.db"
        database_safety="REAL PERSISTENT PERSONAL DATABASE"
        ;;
    *)
        fail "Unsupported database: $database"
        ;;
esac

backend_arguments=(
    "--console=plain"
    ":backend:$backend_task"
)

if [[ "$mode" == "debug" ]]; then
    backend_arguments+=("--debug-jvm")
fi

backend_pid=""
frontend_pid=""

stop_processes() {
    local pid

    for pid in "$backend_pid" "$frontend_pid"; do
        if [[ -n "$pid" ]] && kill -0 "$pid" 2>/dev/null; then
            kill "$pid" 2>/dev/null || true
        fi
    done
}

trap stop_processes EXIT INT TERM

printf '\nLaunch configuration:\n'
printf '  Application mode:  %s\n' "$mode_description"
printf '  Backend launch:    :backend:%s%s\n' "$backend_task" "$debug_argument"
printf '  Database mode:     %s\n' "$database_description"
printf '  Database location: %s\n' "$database_location"
printf '  Database safety:   %s\n\n' "$database_safety"

"$GRADLEW" \
    -p "$PROJECT_DIRECTORY" \
    "${backend_arguments[@]}" &
backend_pid=$!

"$GRADLEW" \
    -p "$PROJECT_DIRECTORY" \
    --console=plain \
    :frontend:start &
frontend_pid=$!

exit_status=0
wait -n "$backend_pid" "$frontend_pid" || exit_status=$?

stop_processes
wait "$backend_pid" 2>/dev/null || true
wait "$frontend_pid" 2>/dev/null || true

trap - EXIT INT TERM
exit "$exit_status"
