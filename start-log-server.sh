#!/bin/bash
set -euo pipefail

cd "$(dirname "$0")"

UDP_PORT=${1:-9001}
HTTP_PORT=${2:-8080}
CP_FILE=${CP_FILE:-/root/rcomp-cp.txt}

if [ ! -f "$CP_FILE" ]; then
  echo "Erro: classpath file não encontrado: $CP_FILE"
  echo "Corre primeiro:"
  echo "mvn -pl aisafe.app.rcomp dependency:build-classpath -Dmdep.outputFile=/root/rcomp-cp.txt -DincludeScope=runtime"
  exit 1
fi

echo "Starting Logging Server..."
echo "UDP port: $UDP_PORT"
echo "HTTP port: $HTTP_PORT"

java -cp "aisafe.app.rcomp/target/classes:aisafe.infrastructure.application/target/classes:aisafe.core/target/classes:persistence.impl.jpa/target/classes:$(cat "$CP_FILE")" \
isep.aisafe.app.rcomp.remoteaccess.RemoteAccessLoggingServer "$UDP_PORT" "$HTTP_PORT"