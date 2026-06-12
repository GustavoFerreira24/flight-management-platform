# US44 - Weather Person Remote Access

## 1. Context

US44 integrates EAPLI with RCOMP. The Weather Person uses a dedicated TCP client to access weather data functionality without direct database access.

The TCP server runs inside the AISafe system and delegates remote commands to the EAPLI application controller `WeatherRemoteAccessController`.

## 2. Requirements

**US44** As a Weather Person, I want to remotely access the system in order to upload weather data.

Acceptance criteria covered by the current implementation:

- A TCP client communicates with a TCP server embedded in the system.
- The client only interacts through the TCP connection.
- The client does not access the database directly.
- Weather Person commands are exposed remotely.
- Authentication and authorization are enforced before protected commands run.

## 3. Protocol

The first TCP line selects the service:

```text
SERVICE|US44
```

After that, commands use a line-based protocol with `|` as separator:

```text
COMMAND|arg1|arg2|...
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

Responses use:

```text
OK|message
ERROR|message
DATA|payload
```

## 4. Implementation

Main classes:

- `RemoteAccessTcpServer` accepts TCP clients and selects `US44`, `US78` or `US86`.
- `RemoteAccessTcpClient` provides a simple console TCP client.
- `WeatherRemoteAccessController` authenticates, authorizes and dispatches Weather Person commands.
- `ControllerWeatherRemoteOperations` delegates to the existing EAPLI weather controllers.

## 5. Demo Credentials

The bootstrap process creates the Weather Person user:

```text
Username: ipma
Password: Password1
Role: WEATHER_PERSON
```

## 6. Execution

From the repository root, start the TCP server with:

```bash
mvn -pl aisafe.app.rcomp -am exec:java -Dexec.mainClass=isep.aisafe.app.rcomp.remoteaccess.RemoteAccessTcpServer -Dexec.args="9000 localhost 9001"
```

Start a client for US44 with:

```bash
mvn -pl aisafe.app.rcomp -am exec:java -Dexec.mainClass=isep.aisafe.app.rcomp.remoteaccess.RemoteAccessTcpClient -Dexec.args="localhost 9000 US44"
```

Example client session:

```text
LOGIN|ipma|Password1
HELP
CONSULT_WEATHER|2027-01-10|LISACC
LOGOUT
```
