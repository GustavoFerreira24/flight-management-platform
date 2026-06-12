# US085 - Test / Validate Flight Plan

## 1. Context

US085 allows a Pilot to test and validate an existing flight plan.

The use case works on the persisted `FlightPlan` aggregate. The Pilot does not provide a Flight DSL file during this operation. Flight DSL validation belongs to US120/US121, where flight plans described through the DSL are validated before being accepted by the system.

For US085, the test component receives the data it needs from the domain model: the selected `FlightPlan`, its `Flight`, `FlightRoute`, assigned `Aircraft`, assigned `Pilot`, fuel quantity and any weather data already associated with the flight plan.

The original acceptance criteria mention a test component in C. For this implementation, the test component is implemented in Java because the teacher accepted this clarification for the project delivery.

## 2. Behaviour

- The authenticated user must have flight plan management permissions.
- The UI lists only flight plans owned by the authenticated Pilot and still available for testing.
- The Pilot selects one flight plan from the list.
- The selected `FlightPlan` must exist.
- The selected `FlightPlan` must belong to the authenticated Pilot.
- The selected `FlightPlan` must be testable, currently `DRAFT` or `UNDER_VALIDATION`.
- The system validates operational domain rules using persisted data.
- On success, the `FlightPlan` becomes `VALIDATED` and is saved.
- On failure, the `FlightPlan` becomes `REJECTED` and is saved.
- Already `VALIDATED` or `REJECTED` flight plans are not tested again.

## 3. Operational Checks

The current test validates:

- flight route is active;
- aircraft can be used for flights;
- aircraft belongs to the route's air transport company;
- pilot is active;
- pilot belongs to the route's air transport company;
- pilot is certified for the aircraft model;
- aircraft engine model is certified for the aircraft model;
- fuel quantity is greater than zero.

If no persisted weather data is associated with the flight plan, the result report states that standard no-weather assumptions were used.

## 4. Design

### 4.1 Sequence Diagram

![US085 sequence diagram](svg/sequence-diagram-85.svg "US085 Sequence Diagram")

### 4.2 Class Diagram

![US085 class diagram](svg/class-diagram-85.svg "US085 Class Diagram")

## 5. Main Artifacts

- `TestFlightPlanUI`
- `TestFlightPlanController`
- `TestFlightPlanResult`
- `FlightPlan`
- `FlightPlanRepository`

## 6. Tests

Automated coverage is provided by `TestFlightPlanControllerTest`.

The tests cover:

- listing only owned and testable flight plans;
- successful validation and transition to `VALIDATED`;
- operational validation failure and transition to `REJECTED`;
- route validation failure;
- pilot certification failure;
- already validated flight plan rejection;
- already rejected flight plan rejection;
- ownership validation;
- unknown flight plan rejection;
- invalid input validation;
- authorization failure propagation.

Run from the repository root:

```bash
mvn -pl aisafe.infrastructure.application -am -Dtest=TestFlightPlanControllerTest -Dsurefire.failIfNoSpecifiedTests=false test
```
