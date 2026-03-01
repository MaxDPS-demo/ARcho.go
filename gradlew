#!/usr/bin/env sh
set -eu

if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
else
  echo "Gradle is not installed. Install Gradle 8.x or open project in Android Studio." >&2
  exit 1
fi
