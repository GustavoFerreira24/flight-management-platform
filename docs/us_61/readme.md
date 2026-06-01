# US061 - Register Customer Collaborators

## 1. Context

This user story was developed for customer collaborator management. The goal is to allow a Backoffice Operator to register collaborators associated with existing AISafe customers.

The implemented customer types are air transport companies and air control areas. Each collaborator is also registered as a system user, so the collaborator can authenticate and execute the actions allowed by the role assigned to its customer type.

### 1.1 List of issues

- US061.1 – Implement Customer Collaborator Registration
- US061.2 – Associate Collaborator with Customer 
- US061.3 – Create Collaborator as System User 
- US061.4 – Implement Collaborator Bootstrap Process 
- US061.5 – Add Collaborator Registration Tests 
- US061.6 – Document Collaborator Registration 

## 2. Requirements

**US061** As a Backoffice Operator, I want to register customer collaborators, associating each collaborator with exactly one customer and creating the corresponding system user.

**Acceptance Criteria:**

- AC1 - Backoffice Operator can successfully register a collaborator for a customer
- AC2 - Collaborator is associated with exactly one customer (air transport company or air control area)
- AC3 - Each collaborator is created as a distinct system user
- AC4 - Collaborator data (e.g., identity, email, roles) is validated and persisted
- AC5 - No validation is required to match collaborator email with customer domain
- AC6 - Bootstrap process creates collaborators successfully
- AC7 - Unit and/or integration tests validate collaborator registration behaviour

**Dependencies/References:**

This requirement depends on existing customer aggregates: `AirTransportCompany` and `AirControlArea`. It also depends on US030 for user management roles and on the eAPLI framework for system-user creation.

## 3. Analysis

The collaborator belongs to the customer collaboration domain and is represented by the `CustomerCollaborator` aggregate root. The aggregate identity is the collaborator email.

The collaborator must refer to different types of customers. Instead of coupling the aggregate directly to multiple customer aggregate classes, the implementation uses `CustomerReference`, an embedded value object composed of:

- `CustomerType`, identifying the type of customer.
- `customerIdentity`, storing the customer business identity.

For air transport companies, the customer identity is the company IATA code. For air control areas, the customer identity is the air control area code.

The `CustomerCollaborator` aggregate also manages the collaborator's active status through an `active` boolean field. This supports US064 (disable collaborator) and US062 (list active collaborators) without requiring separate aggregates.

Important invariants are enforced in the domain and application layers:

- First name and last name are mandatory and must contain only letters, spaces, hyphens or apostrophes.
- Collaborator email is mandatory, valid, normalized and unique.
- Each collaborator must have exactly one customer reference.
- Each collaborator must have one system username.
- The referenced customer must already exist.
- The system username is the normalized (lowercase) collaborator email.
- System-user role depends on the associated customer type.

## 4. Design

### 4.1. Realization

![US61 class diagram](class-diagram-61.svg "US61 Class Diagram")

The `RegisterCustomerCollaboratorController` exposes separate methods for air transport company collaborators and air control area collaborators. Each method validates the customer identifier, checks that the customer exists, builds a `CustomerReference`, and delegates to the common registration flow.

The common registration flow checks backoffice configuration permission, validates collaborator data, enforces email uniqueness, persists the `CustomerCollaborator`, and creates a matching framework system user with the role required by the customer type.

The `RegisterCustomerCollaboratorUI` is the console entry point for backoffice operators. It asks the operator to choose the customer type, then collects the appropriate customer code (IATA for air transport companies, area code for air control areas), followed by collaborator first name, last name, email and initial password. First and last name validation applies the pattern `[A-Za-z]+(?:[ '-][A-Za-z]+)*`, accepting letters, spaces, hyphens and apostrophes only.

### 4.2. Acceptance Tests

**Test 1:** Valid collaborator registration for an air transport company is accepted and persisted.

**Refers to Acceptance Criteria:** AC1, AC2, AC3, AC4, AC5, AC7

Implemented in `RegisterCustomerCollaboratorControllerTest.ensureCollaboratorIsRegisteredForAirTransportCompany`.

**Test 2:** Valid collaborator registration for an air control area is accepted and persisted.

**Refers to Acceptance Criteria:** AC1, AC2, AC3, AC4, AC5, AC7

Implemented in `RegisterCustomerCollaboratorControllerTest.ensureCollaboratorIsRegisteredForAirControlArea`.

**Test 3:** Duplicate collaborator emails are rejected.

**Refers to Acceptance Criteria:** AC4

Implemented in `RegisterCustomerCollaboratorControllerTest.ensureDuplicateCollaboratorEmailIsRejected` and `InMemoryCustomerCollaboratorRepositoryTest`.

**Test 4:** Unknown customer identifiers are rejected.

**Refers to Acceptance Criteria:** AC2, AC4

Implemented in `RegisterCustomerCollaboratorControllerTest.ensureUnknownAirTransportCompanyIsRejected` and `ensureUnknownAirControlAreaIsRejected`.

**Test 5:** Invalid collaborator data and blank initial passwords are rejected.

**Refers to Acceptance Criteria:** AC4

Implemented in `RegisterCustomerCollaboratorControllerTest.ensureInvalidCollaboratorDataIsRejected` and `ensureBlankInitialPasswordIsRejected`.

**Test 6:** Customer collaborator aggregate invariants are enforced.

**Refers to Acceptance Criteria:** AC2, AC4

Implemented in `CustomerCollaboratorTest`, `CollaboratorEmailTest`, `CustomerReferenceTest` and `SystemUsernameTest`.

**Test 7:** Customer collaborator bootstrap helper creates missing collaborators and avoids duplicate creation.

**Refers to Acceptance Criteria:** AC6, AC7

Implemented in `AisafeBootstrapperTest.ensureCustomerCollaboratorIsBootstrappedWhenMissing` and `AisafeBootstrapperTest.ensureCustomerCollaboratorBootstrapIsIdempotent`.

## 5. Implementation

Main implementation artifacts:

- `CustomerCollaborator`: aggregate root representing a collaborator associated with one customer. Includes `active` flag with `disable()`, `enable()` and `isActive()` methods to support collaborator lifecycle management (US062, US064).
- `CollaboratorEmail`: value object for collaborator identity and email normalization (lowercased).
- `SystemUsername`: value object for the linked system username (lowercased).
- `CustomerReference`: value object identifying the associated customer by type and identity string.
- `CustomerType`: enum for supported customer types (`AIR_TRANSPORT_COMPANY`, `AIR_CONTROL_AREA`).
- `CustomerCollaboratorRepository`: repository abstraction with three lookups: by email, by system username, and by customer reference.
- `InMemoryCustomerCollaboratorRepository`: in-memory repository implementation.
- `JpaCustomerCollaboratorRepository`: JPA repository implementation.
- `RegisterCustomerCollaboratorController`: application controller for registering collaborators.
- `RegisterCustomerCollaboratorUI`: console UI for backoffice operators to register collaborators interactively. Validates first and last name format (`[A-Za-z]+(?:[ '-][A-Za-z]+)*`), email format, and customer codes before calling the controller.
- `AisafeBootstrapper`: creates initial customer collaborators and matching system users.

## 6. Integration/Demonstration

The registration flow is exposed both through the console UI and the bootstrap process.

### Console UI

Run the application with:

```bash
mvn -f aisafe.app.backoffice.console/pom.xml exec:java
```

Login with:

```text
Username/Email: backoffice
Password: Password1
```

Then select the option to register a customer collaborator. The UI will ask:

1. Customer type (1 - Air Transport Company / 2 - Air Control Area)
2. Customer code (IATA code for companies, area code for air control areas)
3. First name, last name, email and initial password

### Bootstrap

The bootstrap creates two initial collaborators:

- `tap.collaborator@aisafe.com`, first name `TAP`, last name `Collaborator`, associated with air transport company `TP`, role `AIR_TRANSPORT_COMPANY_COLLABORATOR`.
- `lisacc.collaborator@aisafe.com`, first name `Lisbon`, last name `Controller`, associated with air control area `LISACC`, role `FLIGHT_CONTROL_OPERATOR`.

The collaborators are created during `AisafeBootstrapper.bootstrap()` if they do not already exist.

A `WEATHER_PERSON` user (`ipma@aisafe.com`) is also created during bootstrap directly via the framework user service. This user is associated with the `WEATHER_PERSON` role and supports US041 (Register Weather Data) and US042 (Import Bulk Weather Data).

## 7. Observations

**Active status:** The `CustomerCollaborator` aggregate includes an `active` boolean field (`true` by default on creation). The `disable()` and `enable()` methods support US064 (disable collaborator) without requiring a separate domain concept.

**Name validation:** The UI enforces a name pattern `[A-Za-z]+(?:[ '-][A-Za-z]+)*`, accepting compound names with spaces, hyphens and apostrophes (e.g. `O'Brien`, `Van der Berg`). The controller and domain do not duplicate this validation — it is applied at the UI boundary only.

**Weather Person and air control areas:** The `registerCollaboratorForAirControlArea` controller method currently assigns the `FLIGHT_CONTROL_OPERATOR` role. A `WEATHER_PERSON` collaborator associated with an air control area (required for US041) is created during bootstrap directly via the framework user service, not through the collaborator controller. If the UI needs to support interactive registration of Weather Person collaborators in the future, the controller and UI should be extended with a dedicated method or a role-selection step for air control area collaborators.

**Repository findByCustomerReference:** The `CustomerCollaboratorRepository` exposes a `findByCustomerReference()` method in addition to `findByEmail()` and `findBySystemUsername()`. This method supports listing collaborators by customer (US062) and is used internally by the bootstrap idempotency checks.
