# US11 – Aggregate Justification

## 1. Context

This user story is part of Sprint 1 and focuses on the **design of the Domain Model using Domain-Driven Design (DDD)** principles.

The objective is to justify the defined aggregates by:
- illustrating one representative scenario per aggregate using sequence diagrams
- identifying the invariants enforced by each aggregate

This is the first time this task is being developed.

### 1.1 List of issues

Analysis:  
Already performed through the definition of the Domain Model and Aggregate boundaries.

Design:  
Definition of sequence diagrams and aggregate invariants.


## 2. Requirements

**US11** As Project Manager, I want the team to illustrate one representative scenario per aggregate with a sequence 
diagram and a short explana@on, so that the aggregate’s responsibility is demonstrated through an invariant it enforces. 
This justification will make aggregate design decisions more explicit and easier to review by the project manager.

**Acceptance Criteria:**

- Ac1: Each aggregate must have one representative scenario
- Ac2: Each scenario must be represented using a sequence diagram
- Ac3: Each aggregate must include a justification based on invariants
- Ac4: The justification must clearly explain the responsibility of the aggregate

**Dependencies/References:**

This user story depends on:
- Domain Model definition (US10)
- Aggregate identification and validation according to DDD rules


## 3. Analysis

The analysis activities related to this user story were previously performed in **US10**, where the Domain Model and Aggregate boundaries were defined.

That phase included:
- identification of domain concepts
- classification into Entities and Value Objects
- definition of aggregate boundaries
- validation against DDD rules (e.g., access through aggregate root, consistency boundaries)

Supporting artifacts can be found in:

`../global-artifacts/02.analysis/puml/Aggregates/`

This user story does not introduce new analysis activities, but rather builds upon the results of US10.

NAO SE ENQUADRA (no additional analysis required in US11)



## 4. Design

The design phase focuses on demonstrating aggregate responsibilities through behavior.

Each aggregate includes:
- a representative scenario
- a sequence diagram
- a set of enforced invariants

Sequence diagrams are located in:

`../global-artifacts/03.design/`

### 4.1 Realization

Each aggregate is documented individually with:
- scenario description
- domain model reference
- sequence diagram
- invariant justification

#### Aggregates:

- [AirControlAreaAggregate](./Aggregates/AirControlAreaAggregate.md)
- [AircraftAggregate](./Aggregates/AircraftAggregate.md)
- [AircraftModelAggregate](./Aggregates/AircraftModelAggregate.md)
- [AirportAggregate](./Aggregates/AirportAggregate.md)
- [AirTransportCompanyAggregate](./Aggregates/AirTransportCompanyAggregate.md)
- [EngineModelAggregate](./Aggregates/EngineModelAggregate.md)
- [FlightPlanAggregate](./Aggregates/FlightPlanAggregate.md)
- [FlightRouteAggregate](./Aggregates/FlightRouteAggregate.md)
- [ManufacturerAggregate](./Aggregates/ManufacturerAggregate.md)
- [PilotAggregate](./Aggregates/PilotAggregate.md)
- [SimulationAggregate](./Aggregates/SimulationAggregate.md)
- [UserAggregate](./Aggregates/UserAggregate.md)
- [WeatherDataAggregate](./Aggregates/WeatherDataAggregate.md)


## 6. Integration/Demonstration

The results of this user story integrate with:
- the Domain Model (analysis phase)
- future implementation of aggregates in the system

The sequence diagrams and justifications serve as a foundation for:
- enforcing invariants in code
- guiding aggregate implementation

No execution scripts are required.


## 7. Observations

This user story reinforces key DDD concepts:
- Aggregates as consistency boundaries
- Enforcement of invariants through aggregate roots
- Separation between analysis (structure) and design (behavior)

An alternative approach could involve:
- merging smaller aggregates
- redefining boundaries based on transactional consistency

However, the current design prioritizes:
- high cohesion
- low coupling
- clear responsibility per aggregate

---