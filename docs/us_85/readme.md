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
- When a `DRAFT` flight plan starts testing, the system marks it as `UNDER_VALIDATION` before running the checks.
- The console presents a step-by-step validation progress so the Pilot can see the transition into validation and the checks being performed.
- The system validates operational domain rules using persisted data.
- On success, the `FlightPlan` becomes `VALIDATED` and is saved.
- On failure, the `FlightPlan` becomes `REJECTED` and is saved.
- On success or operational failure, the system persists a `FlightPlanTestReport` with the tested flight plan, timestamp, authenticated pilot, initial status, validation status, final status, outcome and validation messages.
- The Pilot can consult previous persisted validation reports from the Flight Plan Management Console.
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
- `ConsultFlightPlanTestReportsUI`
- `TestFlightPlanController`
- `ConsultFlightPlanTestReportsController`
- `TestFlightPlanResult`
- `FlightPlan`
- `FlightPlanTestReport`
- `FlightPlanRepository`
- `FlightPlanTestReportRepository`

## 6. Tests

Automated coverage is provided by `TestFlightPlanControllerTest`.

The tests cover:

- listing only owned and testable flight plans;
- successful validation and transition to `VALIDATED`;
- transition through `UNDER_VALIDATION`;
- persisted validation report creation;
- listing previous validation reports only for the authenticated Pilot;
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
mvn -pl aisafe.infrastructure.application -am -Dtest=TestFlightPlanControllerTest,ConsultFlightPlanTestReportsControllerTest -Dsurefire.failIfNoSpecifiedTests=false test
```

## 7. Console Demonstration

During validation, the console shows the validation lifecycle and the operational checks being performed:

```text
Status changed: DRAFT -> UNDER_VALIDATION
[1/8] Checking route status...
[2/8] Checking aircraft operational status...
[3/8] Checking aircraft company ownership...
[4/8] Checking pilot status...
[5/8] Checking pilot company assignment...
[6/8] Checking pilot aircraft certification...
[7/8] Checking engine/model certification...
[8/8] Checking fuel quantity...
Generating validation report...
Status changed: UNDER_VALIDATION -> VALIDATED
Validation report: FPT-FPFR902-20260612022126522
```

The persisted reports can then be consulted from:

```text
Flight Plan Management Console -> Consult Flight Plan Test Reports (US85)
```

The report detail shows the stored lifecycle:

```text
Initial status: DRAFT
Validation status: UNDER_VALIDATION
Final status: VALIDATED
Result: PASSED
```

## 8. Database Migration Notes

When using an existing database, make sure the old `TEST_STATUS` column no longer blocks inserts and create the report tables used by this US.

```sql
ALTER TABLE FLIGHTPLAN DROP COLUMN IF EXISTS TEST_STATUS;

CREATE TABLE IF NOT EXISTS FLIGHTPLANTESTREPORT (
    PK BIGINT NOT NULL PRIMARY KEY,
    VERSION BIGINT,
    REPORT_ID CHARACTER VARYING(255) NOT NULL UNIQUE,
    TESTED_AT TIMESTAMP NOT NULL,
    TESTED_BY CHARACTER VARYING(255) NOT NULL,
    SUCCESSFUL BOOLEAN NOT NULL,
    INITIAL_STATUS CHARACTER VARYING(255) NOT NULL,
    VALIDATION_STATUS CHARACTER VARYING(255) NOT NULL,
    FINAL_STATUS CHARACTER VARYING(255) NOT NULL,
    FLIGHTPLAN_PK BIGINT NOT NULL,
    CONSTRAINT FK_FPTR_FLIGHTPLAN FOREIGN KEY (FLIGHTPLAN_PK) REFERENCES FLIGHTPLAN(PK)
);

CREATE TABLE IF NOT EXISTS FLIGHT_PLAN_TEST_REPORT_MESSAGES (
    REPORT_PK BIGINT NOT NULL,
    MESSAGE CHARACTER VARYING(500) NOT NULL,
    CONSTRAINT FK_FPTR_MESSAGES FOREIGN KEY (REPORT_PK) REFERENCES FLIGHTPLANTESTREPORT(PK)
);

CREATE SEQUENCE IF NOT EXISTS SEQ_GEN_SEQUENCE START WITH 1 INCREMENT BY 50;
```
