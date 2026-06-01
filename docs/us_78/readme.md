# US 078 - Air Transport Company Collaborator Remote Access

## 1. Context

This user story belongs to Sprint 3 and integrates EAPLI with RCOMP. The goal is to allow an Air Transport Company Collaborator to access the system remotely through a dedicated Air Transport Company App.

The remote client must not access the database directly. It communicates with the system through a TCP connection, and the server side invokes the existing EAPLI application controllers.

## 2. Requirements

**US078** As an Air Transport Company Collaborator, I want to remotely access the system using the Air Transport Company App.

**Acceptance Criteria:**

- US078.1 A specific TCP-based network client application must communicate with a server application embedded in the system.
- US078.2 The client application must interact with the system only through the TCP connection.
- US078.3 The client application must not access the database directly.
- US078.4 Air Transport Company Collaborator user stories must be remotely available through this client application.
- US078.5 Authentication and authorization must be enforced.

## 3. Analysis

US078 is split into two responsibilities:

- EAPLI responsibility: expose the Air Transport Company Collaborator use cases through an application-level remote access controller.
- RCOMP responsibility: provide the TCP client and TCP server that transport requests and responses.

The first implementation step focuses on the EAPLI side. A remote request is parsed into a command, authenticated when necessary, authorized for `AIR_COMPANY_CONFIGURATION`, and delegated to the existing application controllers.

The current remote command set is prepared for the main Air Transport Company Collaborator use cases already implemented in the project:

- `LIST_FLEET` for US072.
- `REGISTER_AIRCRAFT` for US070.
- `DECOMMISSION_AIRCRAFT` for US071.
- `CREATE_ROUTE` for US073.

## 4. Protocol Draft

The protocol is line-based and uses `|` as separator:

```text
COMMAND|arg1|arg2|...
```

Responses use the same style:

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
LIST_FLEET
REGISTER_AIRCRAFT|registration|country|status|crewSize|economySeats|businessSeats|firstClassSeats|aircraftModelName|aircraftModelManufacturer|engineModelName|engineModelManufacturer
DECOMMISSION_AIRCRAFT|registration
CREATE_ROUTE|routeDesignator|departureAirportIcao|destinationAirportIcao
```

The TCP server and TCP client will use this protocol in the RCOMP implementation step.

## 5. Design

The EAPLI side introduces a small remote access controller and protocol objects:

- `RemoteAccessRequest`: parses a protocol line into a command and arguments.
- `RemoteAccessResponse`: represents an `OK`, `ERROR` or `DATA` response.
- `AirCompanyRemoteAccessController`: handles authentication state and dispatches commands.
- `AirCompanyRemoteOperations`: abstraction for the Air Transport Company Collaborator use cases exposed remotely.
- `ControllerAirCompanyRemoteOperations`: production implementation that delegates to existing EAPLI controllers.

The remote controller does not contain domain rules. It only coordinates remote command handling and delegates business behavior to the existing controllers.

## 6. Authentication and Authorization

The `LOGIN` command authenticates credentials through `CredentialHandler`.

After authentication, the remote controller enforces `AIR_COMPANY_CONFIGURATION` using `AisafeAuthorizationService`. This ensures that only Air Transport Company Collaborators can use the remote access channel.

Each protected command also checks that a remote session was opened and that the authenticated user still has the required permission.

## 7. Tests

The first implementation step includes tests for:

- protocol parsing;
- response encoding;
- login success and failure;
- command rejection before login;
- authorization enforcement;
- delegation to remote operations.

Socket-level tests are left for the RCOMP implementation step.
