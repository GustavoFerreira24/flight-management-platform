# US86 - Pilot Remote Access

## 1. Context

US86 integrates EAPLI with RCOMP. The goal is to allow a Pilot to remotely access the system through the Air Transport Company App.

This implementation covers the EAPLI side only. It does not implement TCP sockets, a TCP client or a TCP server. Instead, it exposes an application-level remote access controller that can be invoked by the future RCOMP server.

## 2. Requirements

**US86** As a Pilot, I want to remotely access the system using the Air Transport Company App.

Acceptance criteria addressed by the EAPLI layer:

- Pilot remote commands are represented through a line-based request/response protocol.
- The client does not need direct database access; all behavior is delegated to EAPLI application controllers.
- Pilot user stories already implemented in EAPLI are available through remote commands.
- Authentication and authorization are enforced before protected commands are executed.
- Request parsing, response generation and command delegation are covered by automated tests.

RCOMP remains responsible for the TCP client, TCP server and transport of the protocol lines.

## 3. Analysis

US86 follows the same split used in US78:

- EAPLI responsibility: expose Pilot use cases through an application-level remote access controller.
- RCOMP responsibility: transport requests and responses through TCP.

The Pilot user stories currently documented and implemented in EAPLI are:

- US43 - Consult weather data.
- US82 - Insert weather data in a flight plan.

For that reason, the EAPLI command set is intentionally limited to those operations.

## 4. Protocol Draft

The protocol reuses the same line-based style as US78:

```text
COMMAND|arg1|arg2|...
```

Responses use:

```text
OK|message
ERROR|message
DATA|payload
```

Supported commands in the EAPLI layer:

```text
LOGIN|username|password
LOGOUT
HELP
CONSULT_WEATHER|day|airControlAreaCode
INSERT_WEATHER_DATA|flightPlanIdentifier|weatherDataDay|airControlAreaCode
```

## 5. Design

Main classes:

- `PilotRemoteAccessController`: handles Pilot remote login state and command dispatch.
- `PilotRemoteOperations`: abstraction for Pilot use cases exposed remotely.
- `ControllerPilotRemoteOperations`: production adapter that delegates to existing EAPLI controllers.
- `RemoteAccessRequest`: parses protocol lines.
- `RemoteAccessResponse`: represents `OK`, `ERROR` and `DATA` responses.

Diagrams:

- US86 class diagram: [PUML](class-diagram-86.puml) | [SVG](svg/class-diagram-86.svg)
- US86 sequence diagram: [PUML](sequence-diagram-86.puml) | [SVG](svg/sequence-diagram-86.svg)
- US86 global design SD: [PUML](../global-artifacts/03.design/puml/US86PilotRemoteAccess-SD.puml) | [SVG](../global-artifacts/03.design/svg/US86PilotRemoteAccess-SD.svg)

The remote controller does not implement domain rules. It authenticates, checks remote session state, verifies Pilot authorization and delegates business behavior to existing controllers.

## 6. Authentication and Authorization

The `LOGIN` command authenticates credentials through `CredentialHandler` and then checks the `PILOT` role.

Protected commands require:

- a successful remote login;
- a current authorization check for `PILOT`;
- valid command argument count.

The delegated EAPLI controllers still enforce their own permissions:

- `ConsultWeatherDataController` checks `WEATHER_DATA_CONSULTATION`.
- `InsertWeatherDataInFlightPlanController` checks `FLIGHT_PLAN_WEATHER_DATA_INSERTION`.

## 7. Tests

Automated tests cover:

- `HELP` without login;
- successful and failed login;
- protected command rejection before login;
- authorization checks before command delegation;
- `CONSULT_WEATHER` delegation;
- `INSERT_WEATHER_DATA` delegation;
- invalid argument count rejection;
- logout.

Relevant test class:

- `PilotRemoteAccessControllerTest`

## 8. Execution

Run the relevant tests with:

```bash
mvn -pl aisafe.infrastructure.application -am '-Dtest=PilotRemoteAccessControllerTest' '-Dsurefire.failIfNoSpecifiedTests=false' test
```
