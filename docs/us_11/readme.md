# <span style="color:#E67E22">US11 – Aggregate Justification</span>

## <span style="color:#2E75B6">1. Context</span>

This user story is part of Sprint 1 and focuses on the **design of the Domain Model using Domain-Driven Design (DDD)** principles.

The objective is to justify the defined aggregates by:
- illustrating one representative scenario per aggregate using sequence diagrams
- identifying the invariants enforced by each aggregate

This is the first time this task is being developed.

### <span style="color:#70AD47">1.1 List of issues</span>

**Analysis:**  
Already performed through the definition of the Domain Model and Aggregate boundaries.

**Design:**  
Definition of sequence diagrams and aggregate invariants.


## <span style="color:#2E75B6">2. Requirements</span>

**US11** As Project Manager, I want the team to illustrate one representative scenario per aggregate with a sequence
diagram and a short explanation, so that the aggregate’s responsibility is demonstrated through an invariant it enforces.
This justification will make aggregate design decisions more explicit and easier to review by the project manager.

**Acceptance Criteria:**

- <span style="color:#70AD47">Ac1:</span> Each aggregate must have one representative scenario
- <span style="color:#70AD47">Ac2:</span> Each scenario must be represented using a sequence diagram
- <span style="color:#70AD47">Ac3:</span> Each aggregate must include a justification based on invariants
- <span style="color:#70AD47">Ac4:</span> The justification must clearly explain the responsibility of the aggregate

**Dependencies/References:**

This user story depends on:
- Domain Model definition (US10)
- Aggregate identification and validation according to DDD rules


## <span style="color:#2E75B6">3. Analysis</span>

The analysis activities related to this user story were previously performed in **US10**, where the Domain Model and Aggregate boundaries were defined.

That phase included:
- identification of domain concepts
- classification into Entities and Value Objects
- definition of aggregate boundaries
- validation against DDD rules (e.g., access through aggregate root, consistency boundaries)

Supporting artifacts can be found in:

`../global-artifacts/02.analysis/puml/Aggregates/`

This user story does not introduce new analysis activities, but rather builds upon the results of US10.

**NAO SE ENQUADRA** (no additional analysis required in US11)


## <span style="color:#2E75B6">4. Design</span>

The design phase focuses on demonstrating aggregate responsibilities through behavior.

Each aggregate includes:
- a representative scenario
- a sequence diagram
- a set of enforced invariants

Sequence diagrams are located in:

`../global-artifacts/03.design/`

### <span style="color:#70AD47">4.1 Realization</span>

Each aggregate is documented individually with:
- scenario description
- domain model reference
- sequence diagram
- invariant justification

#### <span style="color:#70AD47">Aggregates:</span>

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


## <span style="color:#2E75B6">6. Integration/Demonstration</span>

The results of this user story integrate with:
- the Domain Model (analysis phase)
- future implementation of aggregates in the system

The sequence diagrams and justifications serve as a foundation for:
- enforcing invariants in code
- guiding aggregate implementation

No execution scripts are required.


## <span style="color:#2E75B6">7. Observations</span>

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