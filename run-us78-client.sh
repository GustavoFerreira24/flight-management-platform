#!/bin/bash
set -e

cd "$(dirname "$0")"

TCP_HOST=${1:-localhost}
TCP_PORT=${2:-9000}
SERVICE="US78"
CP_FILE="rcomp-cp.txt"

case "$(uname -s)" in
  MINGW*|MSYS*|CYGWIN*) CP_SEP=";" ;;
  *) CP_SEP=":" ;;
esac

if ! command -v java >/dev/null 2>&1; then
  echo "Error: java not found. Install/configure JDK 21 and try again."
  exit 1
fi

if [ ! -f "aisafe.app.rcomp/target/classes/isep/aisafe/app/rcomp/remoteaccess/RemoteAccessTcpClient.class" ]; then
  echo "Compiling project..."
  mvn -q -pl aisafe.app.rcomp -am clean install -DskipTests
fi

if [ ! -f "$CP_FILE" ]; then
  echo "Generating classpath..."
  mvn -q -pl aisafe.app.rcomp dependency:build-classpath \
    -Dmdep.outputFile="$PWD/$CP_FILE" \
    -DincludeScope=runtime
fi

APP_CP="aisafe.app.rcomp/target/classes${CP_SEP}aisafe.infrastructure.application/target/classes${CP_SEP}aisafe.core/target/classes${CP_SEP}persistence.impl.jpa/target/classes${CP_SEP}$(cat "$CP_FILE")"

echo "Starting US78 client..."
echo "TCP server: $TCP_HOST:$TCP_PORT"
echo

java -cp "$APP_CP" isep.aisafe.app.rcomp.remoteaccess.RemoteAccessTcpClient "$TCP_HOST" "$TCP_PORT" "$SERVICE" || {
  echo
  echo "Could not connect to the TCP server."
  echo "If you are outside the DEI network, open this SSH tunnel in another terminal:"
  echo
  echo "ssh -p 10578 -L 9000:localhost:9000 root@vsgate-ssh.dei.isep.ipp.pt"
  echo ""
  echo "For logs:"
  echo  "ssh -p 10596 -L 8081:localhost:8080 root@vsgate-ssh.dei.isep.ipp.pt"
  echo
  echo "Then run this script again."
  exit 1
}