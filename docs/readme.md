# Documentation
This folder contains all documentation artifacts produced during development of the project.

---

## Navigation

### Requirements Engineering
- [Glossary](./global-artifacts/01.requirements-engineering/glossary.md)
- [Use Case Diagram](./global-artifacts/01.requirements-engineering/use-case-diagram.md)

---

### Domain Analysis
- [Main analysis made](./global-artifacts/02.analysis/analysis.md)
- [Full Domain Model (SVG)](./global-artifacts/02.analysis/svg/DM.svg)
- [Reduced Domain Model (SVG)](./global-artifacts/02.analysis/svg/DM-reduced.svg)

#### Aggregates (Analysis)
- [Aggregates Diagrams](./global-artifacts/02.analysis/svg/Aggregates/)

---

### System Design
- [Design Overview](./global-artifacts/03.design/design.md)

#### Sequence Diagrams
- [All Sequence Diagrams (SVG)](./global-artifacts/03.design/svg/)

---

### User Stories

#### US11 — Domain Model (DDD)
- [Overview](./us_11/readme.md)

#### US55 — Create an Aircraft Model
- [Aircraft Model Registration](./us_55/readme.md)

#### US56 — Create an Aircraft Engine Model
- [Engine Model Registration](./us_56/readme.md)

#### US70 — Add an Aircraft to an Air Transport Company
- [Aircraft Registration](./us_70/readme.md)

#### US71 — Decommission an Aircraft
- [Aircraft Decommissioning](./us_71/readme.md)

#### US72 — List Aircraft Fleet
- [Aircraft Fleet Listing](./us_72/readme.md)

#### US73 — Create a Flight Route
- [Flight Route Creation](./us_73/readme.md)

#### US78 — Air Transport Company Collaborator Remote Access
- [Remote Access](./us_78/readme.md)

#### US86 — Pilot Remote Access
- [Pilot Remote Access](./us_86/readme.md)

#### US81 — Create a Flight Plan from a File
- [Flight Plan File Import](./us_81/implementation-notes.md)

#### US83 — Flight DSL Specification and Validation
- [Flight DSL Specification](./us_83/flight-dsl.md)

#### US121 — Create a Flight Plan from a File
- [Flight Plan File Import](./us_121/implementation-notes.md)

#### US112 — Monthly Report Generation
- [Monthly Report Generation](./us_112/readme.md)

##### Aggregates Documentation
- [Flight Aggregate](./us_11/Aggregates/FlightAggregate.md)
- [Flight Route Aggregate](./us_11/Aggregates/FlightRouteAggregate.md)
- [Flight Plan Aggregate](./us_11/Aggregates/FlightPlanAggregate.md)
- [Aircraft Aggregate](./us_11/Aggregates/AircraftAggregate.md)
- [Aircraft Model Aggregate](./us_11/Aggregates/AircraftModelAggregate.md)
- [Engine Model Aggregate](./us_11/Aggregates/EngineModelAggregate.md)
- [Manufacturer Aggregate](./us_11/Aggregates/ManufacturerAggregate.md)
- [Airport Aggregate](./us_11/Aggregates/AirportAggregate.md)
- [Air Control Area Aggregate](./us_11/Aggregates/AirControlAreaAggregate.md)
- [Weather Data Aggregate](./us_11/Aggregates/WeatherDataAggregate.md)
- [Simulation Aggregate](./us_11/Aggregates/SimulationAggregate.md)
- [User Aggregate](./us_11/Aggregates/UserAggregate.md)
- [Pilot Aggregate](./us_11/Aggregates/PilotAggregate.md)
- [Air Transport Company Aggregate](./us_11/Aggregates/AirTransportCompanyAggregate.md)

---

## Structure

```
docs/
 ├── global-artifacts/
 │    ├── 01.requirements-engineering/
 │    ├── 02.analysis/
 │    └── 03.design/
 │
 └── us_11/
      └── Aggregates/

```

---

## Notes

- All artifacts follow Domain-Driven Design principles

- Diagrams are available in:
    - SVG (visualization)
    - PlantUML (source)
