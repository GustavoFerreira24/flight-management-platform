# US080 - Create a Flight Plan

## 1. Context

US080 allows an authenticated Pilot to register a draft flight plan for an existing flight. The implementation follows the same DDD layering used by the rest of the project: the UI gathers input, `CreateFlightPlanController` coordinates the use case, repositories resolve existing aggregate roots, and `FlightPlan` protects the aggregate invariants.

## 2. Requirements

**US080** As Pilot, I want to register a flight plan for a route. I must add the aircraft, departure date/time, fuel quantity and pilot. The pilot must be of the route's company. Flight plan status is set to `DRAFT` when created and must undergo a multi-step validation process.

**Acceptance Criteria:**

- US080.1 Only a Pilot may create a flight plan.
- US080.2 The flight plan is created for an existing flight, which provides the route and departure date/time.
- US080.3 The selected aircraft must exist and be usable for flights.
- US080.4 The assigned pilot is the authenticated pilot.
- US080.5 The pilot must belong to the route's air transport company.
- US080.6 The aircraft must belong to the route's air transport company.
- US080.7 The fuel quantity is mandatory and positive.
- US080.8 A newly created flight plan starts with status `DRAFT`.
- US080.9 The flight plan identifier must be unique.

## 3. Analysis

`FlightPlan` is the aggregate root for the flight plan lifecycle. It references existing aggregate roots: `Flight`, `Aircraft` and `Pilot`. The flight already contains the route and departure date/time, so the application does not duplicate those concepts in the flight plan.

The authenticated user determines the assigned pilot. `CreateFlightPlanController` resolves the authenticated pilot by email, falling back to username/pilot id for compatibility with existing authentication data. If an external client still supplies a pilot id, the controller validates that it matches the authenticated pilot instead of trusting that input.

The domain aggregate enforces local consistency rules:

- flight, aircraft, pilot and fuel quantity are mandatory;
- aircraft must be usable for flights;
- aircraft must belong to the route's company;
- pilot must belong to the route's company;
- pilot must be active/valid;
- status is initialized as `DRAFT`.

The application controller enforces use-case rules that require repositories or authentication:

- authorization as `PILOT` through `FLIGHT_PLAN_MANAGEMENT`;
- flight plan identifier uniqueness;
- existence of the referenced `Flight` and `Aircraft`;
- resolution and ownership validation of the authenticated `Pilot`.

## 4. Design

### 4.1 Realization

![US080 class diagram](svg/class-diagram-80.svg "US080 Class Diagram")

![US080 sequence diagram](svg/sequence-diagram-80.svg "US080 Sequence Diagram")

The main flow is:

- The user logs in as a Pilot.
- The user selects the flight plan creation option.
- `CreateFlightPlanUI` asks for flight plan identifier, flight designator, aircraft registration, fuel amount and fuel unit.
- `CreateFlightPlanController` authorizes the use case with `FLIGHT_PLAN_MANAGEMENT`.
- The controller checks flight plan identifier uniqueness.
- The controller resolves the existing `Flight` and `Aircraft` aggregate roots.
- The controller resolves the authenticated `Pilot` and rejects a supplied pilot id if it belongs to another pilot.
- The controller creates the `FlightPlan` aggregate with status `DRAFT` and persists it through `FlightPlanRepository`.

### 4.2 Acceptance Tests

**Test 1:** Flight plan creation requires the flight plan management permission.

**Refers to Acceptance Criteria:** US080.1

Implemented in `CreateFlightPlanControllerTest.ensureExecuteRequiresFlightPlanManagementPermission` and `AisafePermissionsTest.ensureFlightPlanManagementPermissionsAreCorrect`.

**Test 2:** A valid flight plan can be created for the authenticated pilot.

**Refers to Acceptance Criteria:** US080.2, US080.3, US080.4, US080.5, US080.6, US080.7, US080.8

Implemented in `CreateFlightPlanControllerTest.ensureFlightPlanCanBeCreated` and `FlightPlanTest`.

**Test 3:** Duplicate flight plan identifiers are rejected.

**Refers to Acceptance Criteria:** US080.9

Implemented in `CreateFlightPlanControllerTest.ensureDuplicateFlightPlanIdentifierIsRejected`.

**Test 4:** Unknown flights and aircraft are rejected.

**Refers to Acceptance Criteria:** US080.2, US080.3

Implemented in `CreateFlightPlanControllerTest.ensureUnknownFlightIsRejected` and `ensureUnknownAircraftIsRejected`.

**Test 5:** The authenticated pilot must exist and match any supplied pilot identifier.

**Refers to Acceptance Criteria:** US080.1, US080.4

Implemented in `CreateFlightPlanControllerTest.ensureUnknownPilotIsRejected` and `ensureSuppliedPilotIdentifierMustMatchAuthenticatedPilot`.

## 5. Implementation

Main implementation artifacts:

- `FlightPlan`: aggregate root for flight plan data and lifecycle state.
- `FlightPlanIdentifier`: value object for the flight plan identity.
- `FuelQuantity`: value object for the assigned fuel.
- `FlightPlanStatus`: lifecycle status, initialized as `DRAFT`.
- `CreateFlightPlanController`: application service/controller for the use case.
- `CreateFlightPlanUI`: console UI for flight plan creation.
- `FlightPlanRepository`, `FlightRepository`, `AircraftRepository`, `PilotRepository`: repository abstractions used to resolve aggregate roots and persist the new flight plan.

## 6. Integration/Demonstration

Run the console application with:

```bash
mvn -f aisafe.app.backoffice.console/pom.xml exec:java
```

Login as a Pilot and select the flight plan creation option. The UI asks for:

- flight plan identifier;
- flight designator;
- aircraft registration;
- fuel amount;
- fuel unit.

The pilot is derived from the authenticated user and is not entered manually in the UI.
