#!/bin/bash
set -e

cd "$(dirname "$0")"

TCP_HOST=${1:-localhost}
TCP_PORT=${2:-9000}
SERVICE="US78"
CP_FILE="rcomp-cp.txt"

if [ ! -d "aisafe.app.rcomp/target/classes" ]; then
  echo "Compiling project..."
  mvn -q -pl aisafe.app.rcomp -am clean install -DskipTests
fi

if [ ! -f "$CP_FILE" ]; then
  echo "Generating classpath..."
  mvn -q -pl aisafe.app.rcomp dependency:build-classpath \
    -Dmdep.outputFile="$PWD/$CP_FILE" \
    -DincludeScope=runtime
fi

echo "Starting US78 client..."
echo "TCP server: $TCP_HOST:$TCP_PORT"
echo

java -cp "aisafe.app.rcomp/target/classes:aisafe.infrastructure.application/target/classes:aisafe.core/target/classes:persistence.impl.jpa/target/classes:$(cat "$CP_FILE")" \
isep.aisafe.app.rcomp.remoteaccess.RemoteAccessTcpClient "$TCP_HOST" "$TCP_PORT" "$SERVICE" || {
  echo
  echo "Could not connect to the TCP server."
  echo "Check if the server is running and if the host/port are reachable."
  echo "If using DEI Virtual Servers from outside, open the SSH tunnel and use localhost."
  echo "            ssh -p 10578 -L 9000:localhost:9000 root@vsgate-ssh.dei.isep.ipp.pt"
  echo " or"
  echo "ssh -p 10578 \
            -L 9000:localhost:9000 \
            -L 8081:vs596.dei.isep.ipp.pt:8080 \
            root@vsgate-ssh.dei.isep.ipp.pt"
  exit 1
}