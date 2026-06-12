# US90 and US91 - Remote Access Logging

## 1. Context

US90 and US91 implement external logging for remote accesses. The TCP server sends login, logout and disconnect events to a dedicated logging server using UDP datagrams.

The logging server also exposes HTTP pages so an Administrator can monitor the current state in a browser.

## 2. Requirements

Covered by the current implementation:

- Remote access events are sent to a remote application through UDP.
- Successful logins, failed logins, logouts and disconnects are logged.
- Each event includes timestamp, username, client IP address, client port and service.
- The logging server can run on a dedicated network node.
- An HTTP server provides pages for recent events and active remote users.
- AJAX updates both pages without browser reloads.

## 3. Implementation

Main classes:

- `RemoteAccessLogClient` sends UDP datagrams from the TCP server.
- `RemoteAccessLoggingServer` starts the UDP receiver and HTTP server.
- `RemoteAccessUdpLogServer` receives datagrams and records valid events.
- `RemoteAccessHttpLogServer` serves the browser pages and AJAX fragments.
- `RemoteAccessEventStore` stores recent events and active users in memory.

## 4. Ports

Default ports:

- TCP remote access server: `9000`.
- UDP logging server: `9001`.
- HTTP logging dashboard: `8080`.

## 5. Execution

Start the logging server first:

```bash
mvn -pl aisafe.app.rcomp -am exec:java -Dexec.mainClass=isep.aisafe.app.rcomp.remoteaccess.RemoteAccessLoggingServer -Dexec.args="9001 8080"
```

Start the TCP server and point it to the logging server host:

```bash
mvn -pl aisafe.app.rcomp -am exec:java -Dexec.mainClass=isep.aisafe.app.rcomp.remoteaccess.RemoteAccessTcpServer -Dexec.args="9000 localhost 9001"
```

If the logging server is running on another machine, replace `localhost` with that machine's hostname or IP address.

Open the dashboards in a browser:

```text
http://localhost:8080/events
http://localhost:8080/active-users
```

If the logging server is in the cloud, replace `localhost` with the public hostname/IP and ensure port `8080` is reachable.
