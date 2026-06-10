#!/bin/bash
set -euo pipefail

cd "$(dirname "$0")"

TCP_PORT=${1:-9000}
LOG_HOST=${2:-vs596.dei.isep.ipp.pt}
LOG_PORT=${3:-9001}
CP_FILE=${CP_FILE:-/root/rcomp-cp.txt}

if [ ! -f "$CP_FILE" ]; then
  echo "Erro: classpath file não encontrado: $CP_FILE"
  echo "Corre primeiro:"
  echo "mvn -pl aisafe.app.rcomp dependency:build-classpath -Dmdep.outputFile=/root/rcomp-cp.txt -DincludeScope=runtime"
  exit 1
fi

echo "Starting TCP Server..."
echo "TCP port: $TCP_PORT"
echo "Logging server: $LOG_HOST:$LOG_PORT"

java -cp "aisafe.app.rcomp/target/classes:aisafe.infrastructure.application/target/classes:aisafe.core/target/classes:persistence.impl.jpa/target/classes:$(cat "$CP_FILE")" \
isep.aisafe.app.rcomp.remoteaccess.RemoteAccessTcpServer "$TCP_PORT" "$LOG_HOST" "$LOG_PORT"