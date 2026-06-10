#!/bin/bash
set -euo pipefail

cd "$(dirname "$0")"

TCP_HOST=${1:-vs578.dei.isep.ipp.pt}
TCP_PORT=${2:-9000}
SERVICE="US78"
CP_FILE=${CP_FILE:-rcomp-cp.txt}

if [ ! -f "$CP_FILE" ]; then
  echo "Erro: classpath file não encontrado: $CP_FILE"
  echo "Corre primeiro:"
  echo "mvn -pl aisafe.app.rcomp dependency:build-classpath -Dmdep.outputFile=rcomp-cp.txt -DincludeScope=runtime"
  exit 1
fi

echo "Starting US78 client..."
echo "TCP server: $TCP_HOST:$TCP_PORT"
echo "Service: $SERVICE"

java -cp "aisafe.app.rcomp/target/classes:aisafe.infrastructure.application/target/classes:aisafe.core/target/classes:persistence.impl.jpa/target/classes:$(cat "$CP_FILE")" \
isep.aisafe.app.rcomp.remoteaccess.RemoteAccessTcpClient "$TCP_HOST" "$TCP_PORT" "$SERVICE"