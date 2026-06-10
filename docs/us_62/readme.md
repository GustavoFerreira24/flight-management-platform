# US062 - List Customer Collaborators

## 1. Context

This user story supports customer collaborator management. The goal is to allow a Backoffice Operator to list active collaborators associated with a specific AISafe customer.

The implemented customer types are Air Transport Companies and Air Control Areas. Customer collaborators are registered in US061 and can later be listed by customer through this use case.

## 2. Requirements

**US062** As a Backoffice Operator, I want to list customer collaborators associated with a customer.

**Acceptance Criteria:**

- AC1 - The Backoffice Operator can list collaborators associated with an Air Transport Company.
- AC2 - The Backoffice Operator can list collaborators associated with an Air Control Area.
- AC3 - The customer identifier is validated before listing collaborators.
- AC4 - Unknown customers are rejected with a clear error.
- AC5 - Only active collaborators are returned.
- AC6 - Authorization is enforced before collaborator data is accessed.

**Dependencies/References:**

This use case depends on US061 because collaborators must already exist and be associated with either an `AirTransportCompany` or an `AirControlArea`.

## 3. Analysis

Customer collaborators are represented by the `CustomerCollaborator` aggregate root. Each collaborator is associated with exactly one customer type: either an Air Transport Company or an Air Control Area.

The listing operation is customer-scoped. The operator chooses the customer type and provides the corresponding identifier:

- Air Transport Company: IATA code.
- Air Control Area: area code.

The application controller validates authorization, resolves the selected customer, queries the collaborator repository and filters inactive collaborators. The use case does not create or modify collaborators.

## 4. Design

### 4.1. Realization

Class diagram:

- [PUML](class-diagram-62.puml)
- [SVG](class-diagram-62.svg)

Sequence diagram:

- [PUML](sequence-diagram-62.puml)
- [SVG](sequence-diagram-62.svg)

The `ListCustomerCollaboratorsUI` is the console entry point. It asks whether the operator wants to list collaborators for an Air Transport Company or an Air Control Area, collects the appropriate customer code and delegates to the controller.

The `ListCustomerCollaboratorsController` exposes two application operations:

- `listForAirTransportCompany(iataCode)`.
- `listForAirControlArea(areaCode)`.

Both operations enforce authorization, load the selected customer and return only active collaborators.

### 4.2. Acceptance Tests

**Test 1:** Listing collaborators for an Air Transport Company returns only active collaborators.

**Refers to Acceptance Criteria:** AC1, AC5, AC6

Implemented in `ListCustomerCollaboratorsControllerTest.ensureListForAirTransportCompanyReturnsOnlyActiveCollaborators`.

**Test 2:** Unknown Air Transport Company identifiers are rejected.

**Refers to Acceptance Criteria:** AC3, AC4

Implemented in `ListCustomerCollaboratorsControllerTest.ensureUnknownAirTransportCompanyIsRejected`.

**Test 3:** Listing collaborators for an Air Control Area returns only active collaborators.

**Refers to Acceptance Criteria:** AC2, AC5, AC6

Implemented in `ListCustomerCollaboratorsControllerTest.ensureListForAirControlAreaReturnsOnlyActiveCollaborators`.

**Test 4:** Unknown Air Control Area identifiers are rejected.

**Refers to Acceptance Criteria:** AC3, AC4

Implemented in `ListCustomerCollaboratorsControllerTest.ensureUnknownAirControlAreaIsRejected`.

## 5. Implementation

Main implementation artifacts:

- `ListCustomerCollaboratorsUI`: console UI that collects the listing option and customer identifier.
- `ListCustomerCollaboratorsController`: application controller that coordinates authorization, customer lookup and collaborator listing.
- `CustomerCollaboratorRepository`: repository abstraction with customer-scoped listing methods.
- `AirTransportCompanyRepository`: repository used to resolve Air Transport Companies by IATA code.
- `AirControlAreaRepository`: repository used to resolve Air Control Areas by area code.
- `CustomerCollaborator`: aggregate root returned by the listing operation.

The controller uses `AisafeAuthorizationService` before accessing collaborator data. For Air Transport Company collaborators it checks `USER_MANAGEMENT`; for Air Control Area collaborators it checks `BACKOFFICE_CONFIGURATION`.

## 6. Integration/Demonstration

Run the backoffice console application and log in with a user that has the required backoffice permissions.

From the backoffice configuration menu, select:

```text
List Collaborators (US62)
```

Then choose one of the listing options:

```text
1 - Air Transport Company Customer Collaborators
2 - Air Control Area Customer Collaborators
```

For Air Transport Companies, provide the IATA code. For Air Control Areas, provide the area code. The UI prints each active collaborator with full name and email.

## 7. Observations

Inactive collaborators are intentionally filtered out by the controller. This keeps the listing aligned with collaborator lifecycle management and prevents disabled collaborators from appearing in operational lists.
