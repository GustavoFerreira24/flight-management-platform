# US 071 - Decommission an Aircraft

## 1. Context

This user story was developed for the Air Transport Company configuration area. The goal is to allow an authenticated Air Transport Company Collaborator to retire an aircraft from the fleet of the company they belong to.

The implementation follows the existing DDD approach used for US070: `Aircraft` is the aggregate root, registration and operational status are represented by value objects, and cross-aggregate checks are performed by the application service before the aggregate state is changed and persisted.

### 1.1 List of issues

Analysis:

- 1 - Implement Aircraft Decommissioning
- 2 - Validate Aircraft Ownership and Lifecycle State
- 3 - Prevent Decommissioning Aircraft with Pending Flights
- 4 - List Aircraft Available for Decommissioning

Design:

- 8 - Document Aircraft Decommissioning

Implement:

- 1 - Implement Aircraft Decommissioning in the Aggregate
- 2 - Implement Aircraft Decommissioning Controller
- 3 - Implement Pending Flights Validation Seam
- 4 - Implement Aircraft Decommissioning UI

Test:

- 5 - Add Aircraft Domain Tests
- 6 - Add Aircraft Controller Tests
- 7 - Add Aircraft Listing Tests

## 2. Requirements

**US071** As an Air Transport Company Collaborator, I want to retire an aircraft from my company's fleet. It is not possible to create flights for a retired aircraft, nor is it possible to retire an aircraft that has pending flights registered in the system.

**Acceptance Criteria:**

- US071.1 The system must allow an Air Transport Company Collaborator to retire an aircraft from their company's fleet.
- US071.2 The aircraft registration number must identify an existing aircraft.
- US071.3 The aircraft must belong to the authenticated collaborator's air transport company.
- US071.4 The aircraft must not have pending flights registered in the system.
- US071.5 The aircraft must not already be decommissioned.
- US071.6 Retiring an aircraft must change its operational status to `DECOMMISSIONED`.
- US071.7 A decommissioned aircraft must not be available for future flight assignment.
- US071.8 The UI should help the collaborator identify aircraft available for decommissioning.

**Dependencies/References:**

This requirement depends on US070 because only previously registered aircraft can be retired. It also depends on US060 and US061 because the aircraft belongs to an air transport company and the authenticated collaborator determines the company scope. It relates to future flight and flight plan management requirements because the pending-flight rule depends on flight-related data.

## 3. Analysis

The aircraft registration is the business identity of an aircraft and is represented by `AircraftRegistration`. The aircraft is not deleted when it is retired. Instead, its `AircraftOperationalStatus` changes to `DECOMMISSIONED`, preserving fleet history and avoiding broken references from future flight-related aggregates.

The authenticated collaborator determines the company scope. The controller obtains the authenticated username, resolves the matching `CustomerCollaborator`, verifies that the collaborator is associated with an air transport company and then loads that company from `AirTransportCompanyRepository`. The aircraft can only be decommissioned if it belongs to that company.

The domain model and application layer enforce the following rules:

- Aircraft registration is mandatory and normalized by `AircraftRegistration`.
- Aircraft operational status must be one of `ACTIVE`, `IN_MAINTENANCE` or `DECOMMISSIONED`.
- Decommissioning changes the aircraft status to `DECOMMISSIONED`.
- A decommissioned aircraft cannot be decommissioned again.
- A decommissioned aircraft cannot be used for future flight assignment.
- A collaborator can only decommission aircraft from their own air transport company.
- An aircraft with pending flights cannot be decommissioned.

The `Aircraft` aggregate is responsible for the internal lifecycle rule: the status transition to `DECOMMISSIONED` and the rejection of repeated decommissioning. Rules that require other aggregates or repositories, such as company ownership and pending flights, are handled by `DecommissionAircraftController` before invoking the aggregate method.

The pending-flight check is represented by `PendingAircraftFlightsService`. Since the project does not yet have a complete persisted `Flight` or `FlightPlan` repository, the current production implementation is a seam (`NoPendingAircraftFlightsService`) that can later be replaced by a repository-backed implementation.

## 4. Design

### 4.1. Realization

![US71 class diagram](class-diagram-71.svg "US71 Class Diagram")

The `DecommissionAircraftUI` lists aircraft from the authenticated collaborator's company that are still available for decommissioning and asks for the aircraft registration number. The UI delegates the operation to `DecommissionAircraftController`.

The controller authorizes the operation with air company configuration permissions, resolves the authenticated collaborator's company, loads the selected aircraft, validates ownership, checks pending flights through `PendingAircraftFlightsService`, calls `Aircraft.decommission()` and persists the modified aggregate through `AircraftRepository`.

The `Aircraft` aggregate controls the lifecycle transition. The operation updates `AircraftOperationalStatus` to `DECOMMISSIONED`; if the aircraft is already decommissioned, the aggregate rejects the operation.

### 4.2. Acceptance Tests

**Test 1:** Valid aircraft decommissioning is accepted and persisted.

**Refers to Acceptance Criteria:** US071.1, US071.3, US071.4, US071.5, US071.6, US071.7

Implemented in `DecommissionAircraftControllerTest.ensureValidAircraftCanBeDecommissioned` and `AircraftTest.ensureAircraftCanBeDecommissioned`.

**Test 2:** Unknown aircraft registrations are rejected.

**Refers to Acceptance Criteria:** US071.2

Implemented in `DecommissionAircraftControllerTest.ensureUnknownAircraftIsRejected`.

**Test 3:** Aircraft from another company cannot be decommissioned.

**Refers to Acceptance Criteria:** US071.3

Implemented in `DecommissionAircraftControllerTest.ensureAircraftMustBelongToAuthenticatedCollaboratorCompany`.

**Test 4:** Aircraft with pending flights cannot be decommissioned.

**Refers to Acceptance Criteria:** US071.4

Implemented in `DecommissionAircraftControllerTest.ensureAircraftWithPendingFlightsCannotBeDecommissioned`.

**Test 5:** Already decommissioned aircraft cannot be decommissioned again.

**Refers to Acceptance Criteria:** US071.5

Implemented in `DecommissionAircraftControllerTest.ensureAlreadyDecommissionedAircraftIsRejected` and `AircraftTest.ensureAlreadyDecommissionedAircraftCannotBeDecommissionedAgain`.

**Test 6:** Collaborator and company association rules are enforced.

**Refers to Acceptance Criteria:** US071.1, US071.3

Implemented in `DecommissionAircraftControllerTest.ensureAuthenticatedUserMustBeCustomerCollaborator`, `ensureCollaboratorMustBeAssociatedWithAirTransportCompany` and `ensureAssociatedAirTransportCompanyMustExist`.

**Test 7:** Decommissionable aircraft can be listed for the authenticated collaborator's company.

**Refers to Acceptance Criteria:** US071.3, US071.4, US071.5, US071.8

Implemented in `DecommissionAircraftControllerTest.ensureDecommissionableAircraftListsOnlyActiveCompanyAircraftWithoutPendingFlights`.

**Test 8:** Operational status identifies decommissioned aircraft.

**Refers to Acceptance Criteria:** US071.6, US071.7

Implemented in `AircraftOperationalStatusTest.ensureDecommissionedFactoryCreatesDecommissionedStatus` and `ensureActiveStatusIsNotDecommissioned`.

## 5. Implementation

Main implementation artifacts:

- `Aircraft`: aggregate root for fleet aircraft and lifecycle transitions.
- `AircraftRegistration`: value object for the unique registration number.
- `AircraftOperationalStatus`: value object for aircraft status.
- `AircraftRepository`: repository abstraction used to find aircraft by registration and by company.
- `InMemoryAircraftRepository`: in-memory repository used by tests and memory persistence mode.
- `JpaAircraftRepository`: JPA repository implementation.
- `DecommissionAircraftController`: application service/controller for aircraft decommissioning.
- `PendingAircraftFlightsService`: service seam used to validate pending flight assignments.
- `NoPendingAircraftFlightsService`: temporary implementation used until flight persistence is available.
- `DecommissionAircraftUI`: console UI for listing decommissionable aircraft and retiring one by registration number.

## 6. Integration/Demonstration

The functionality is available after login as an Air Transport Company Collaborator.

Run the console application with:

```bash
mvn -f aisafe.app.backoffice.console/pom.xml exec:java
```

Login with the bootstrap collaborator:

```text
Username/Email: tap.collaborator@aisafe.com
Password: Password1
```

Then select the aircraft decommissioning option in the Backoffice console. The UI lists active aircraft available for decommissioning and asks for the registration number of the aircraft to retire.

The bootstrap process creates `TP`, the TAP collaborator, the `A320neo` aircraft model and the `LEAP-1A` engine model used for demonstration. An aircraft must first be registered through US070 before it can be decommissioned.

## 7. Observations

The UI derives the company from the authenticated collaborator instead of asking for a company code. This prevents a collaborator from decommissioning aircraft from another company's fleet.

Pending-flight validation is currently represented by a service seam because the Java persistence model for flights and flight plans is not yet implemented. The tests prove that the controller blocks decommissioning when this seam reports pending flights. When a real `Flight` or `FlightPlan` repository becomes available, `NoPendingAircraftFlightsService` should be replaced by an implementation that queries pending assignments.

The system marks aircraft as `DECOMMISSIONED` instead of deleting them. This preserves fleet history while preventing future use in flight assignment.
