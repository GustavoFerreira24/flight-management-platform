# US44 - Weather Person Remote Access

## 1. Context

US44 integrates the EAPLI weather data use cases with the RCOMP remote access mechanism. A Weather Person can use the remote TCP client to authenticate and execute weather data operations without direct database access.

The implementation follows the same architectural split used by US78 and US86:

- RCOMP transports command lines through TCP and sends access logs through UDP.
- EAPLI parses commands, authenticates users, checks authorization and delegates business work to existing application controllers.
- Only the server-side application accesses repositories and persistence.

## 2. Requirements

**US44** As a Weather Person, I want to remotely access weather data operations through the remote access application.

Acceptance criteria addressed:

- Weather Person remote commands use a line-based protocol.
- Remote clients do not access the database directly.
- Authentication is required before protected commands.
- Authorization is checked using `AisafePermissions.WEATHER_DATA_MANAGEMENT`.
- Weather operations are delegated to existing EAPLI controllers.
- The RCOMP TCP server exposes this use case through `SERVICE|US44`.
- Remote log events are sent by the TCP server to the logging server using UDP.

## 3. Protocol

The TCP client first selects the service:

```text
SERVICE|US44
```

After that, the command format is:

```text
COMMAND|arg1|arg2|...
```

Responses use:

```text
OK|message
ERROR|message
DATA|payload
```

Supported commands:

```text
LOGIN|username|password
LOGOUT
HELP
CONSULT_WEATHER|day|airControlAreaCode
REGISTER_WEATHER_DATA|day|airControlAreaCode|providerName|providerType|parameter|value|unit|altitude|latitude|longitude
IMPORT_BULK_WEATHER_DATA|serverFilePath
```

The bulk import command receives a file path that exists on the server side. The client does not upload file contents through the protocol.

## 4. Design

Main EAPLI artifacts:

- `WeatherRemoteAccessController`: handles remote login state, request parsing and command dispatch.
- `WeatherRemoteOperations`: interface for weather operations exposed remotely.
- `ControllerWeatherRemoteOperations`: adapter that delegates to existing EAPLI weather controllers.
- `RemoteAccessRequest`: parses protocol lines.
- `RemoteAccessResponse`: returns `OK`, `ERROR` or `DATA` responses.

Delegated EAPLI controllers:

- `RegisterWeatherDataController`
- `ImportBulkWeatherDataController`
- `ConsultWeatherDataController`

RCOMP artifacts involved:

- `RemoteAccessTcpClient`
- `RemoteAccessTcpServer`
- `RemoteAccessService.US44`
- `RemoteAccessLogClient`
- `RemoteAccessLoggingServer`

Diagrams:

- US44 class diagram: [PUML](class-diagram-44.puml) | [SVG](svg/class-diagram-44.svg)
- US44 sequence diagram: [PUML](sequence-diagram-44.puml) | [SVG](svg/sequence-diagram-44.svg)
- US44 global design SD: [PUML](../global-artifacts/03.design/puml/US44WeatherRemoteAccess-SD.puml) | [SVG](../global-artifacts/03.design/svg/US44WeatherRemoteAccess-SD.svg)

## 5. Authentication and Authorization

The `LOGIN` command authenticates credentials through `CredentialHandler`.

If credentials are valid, the controller checks:

```java
AisafePermissions.WEATHER_DATA_MANAGEMENT
```

Protected commands require:

- a successful remote login;
- a current authorization check for weather data management;
- the expected number of command arguments;
- valid numeric values where required.

The delegated controllers still enforce their own EAPLI permissions and domain validations.

## 6. RCOMP Compliance

The US44 remote access flow complies with the RCOMP constraints used in the project:

- TCP transport is implemented with `java.net.ServerSocket` and `java.net.Socket`.
- UDP logging is implemented with `java.net.DatagramSocket` and `java.net.DatagramPacket`.
- Log visualization is exposed with `com.sun.net.httpserver.HttpServer`.
- The remote TCP client only exchanges protocol lines with the TCP server.
- Persistence remains server-side through EAPLI controllers and repositories.
- Access events are logged externally for `LOGIN_SUCCESS`, `LOGIN_FAILED`, `LOGOUT` and `DISCONNECT`.

## 7. Tests

Automated tests cover:

- `HELP` without login;
- successful and failed login;
- protected command rejection before login;
- authorization checks before command delegation;
- `CONSULT_WEATHER` delegation;
- `REGISTER_WEATHER_DATA` delegation;
- `IMPORT_BULK_WEATHER_DATA` delegation;
- invalid numeric argument rejection;
- invalid argument count rejection;
- logout.

Relevant test class:

- `WeatherRemoteAccessControllerTest`

Run the relevant tests with:

```bash
mvn -pl aisafe.infrastructure.application -am '-Dtest=WeatherRemoteAccessControllerTest' '-Dsurefire.failIfNoSpecifiedTests=false' test
```

RCOMP compilation can be checked with:

```bash
mvn -pl aisafe.app.rcomp -am compile
```

## 8. Demo Commands

Start the logging server:

```text
RemoteAccessLoggingServer 9001 8080
```

Start the TCP server:

```text
RemoteAccessTcpServer 9000 localhost 9001
```

Start the TCP client for US44:

```text
RemoteAccessTcpClient localhost 9000 US44
```

Example session:

```text
HELP
LOGIN|ipma|Password1
CONSULT_WEATHER|2027-01-10|LISACC
REGISTER_WEATHER_DATA|2027-01-10|LISACC|IPMA|STATION|TEMPERATURE|17.5|C|120|38.7|-9.1
IMPORT_BULK_WEATHER_DATA|C:/path/on/server/weather.csv
LOGOUT
```

Logs can be viewed at:

```text
http://localhost:8080/events
http://localhost:8080/active-users
```
