#!/bin/sh
set -e

if [ "$#" -lt 1 ]; then
  echo "Usage: $0 URL [URL...]" >&2
  exit 1
fi

for URL in "$@"; do
  echo "⏳ Waiting for $URL ..."
  until curl -sf "$URL"; do
    echo "  …still waiting for $URL"
    sleep 5
  done
done

echo "✅ All dependencies are up!"
# finally hand off to the original CMD
exec "$@"
