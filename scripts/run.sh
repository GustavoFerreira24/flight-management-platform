#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

echo "Packaging project..."
mvn -q -DskipTests package

JAR_FILE="$(find target -maxdepth 1 -type f -name '*.jar' ! -name '*sources.jar' ! -name '*javadoc.jar' | head -n 1)"

if [[ -z "${JAR_FILE:-}" ]]; then
  echo "Error: no runnable JAR found in target/."
  exit 1
fi

echo "Running $JAR_FILE ..."
java -jar "$JAR_FILE"