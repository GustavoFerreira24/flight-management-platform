# Domain Analysis – Entities and Value Objects

## 1. Purpose

This document defines the classification of domain concepts into **Entities** and **Value Objects**, according to Domain-Driven Design (DDD) principles.

The classification is based strictly on the system requirements (Section 3 and all User Stories), the forum Q&A with the client, and follows the definitions provided in the course materials.

According to DDD:

- An **Entity** is an object with identity that persists over time.
- A **Value Object** is defined only by its attributes, has no identity, and should be immutable.

This classification precedes aggregate design and focuses exclusively on domain semantics.

---

## 2. Entities

Entities are domain concepts that:

- Have a unique identity
- Exist independently over time
- Are subject to lifecycle changes
- Are referenced by other parts of the system

### Air Control Area
Identified by a unique area code.  
Manages geographic boundaries and is referenced by airports and weather data.  
Weather conditions are not uniform across an air control area — they vary by location and altitude within it (confirmed in forum Q&A).  
Created and managed by Backoffice Operators (US050).

### Air Transport Company
Identified by unique IATA (2 letters) and ICAO (2–3 letters) codes.  
Manages aircraft, flight routes, collaborators, and pilots.  
Owns and is responsible for flight routes — a route's name includes the company's initials (e.g. TP123), confirming ownership (US060, US073, forum Q&A).

### Aircraft
Identified by a unique registration number, globally unique.  
Has an operational status (active/decommissioned) and cabin configuration.  
Registered in a country, which may differ from the company's home country.  
Cannot be used in flight plans if decommissioned (US071).  
Belongs to exactly one Air Transport Company (US070).  
An aircraft is of a specific model variant, which includes engine configuration.

### Aircraft Model
Identified by the combination of model name and manufacturer — this combination must be unique (US055).  
Has technical characteristics required for flight simulation: type, motorization, maximum range, empty weight, MTOW, MZFW, maximum fuel capacity, service ceiling, cruise speed, wing area, drag coefficient, and lift coefficient (Section 3.2).  
Must have at least one certified engine model. A specific combination of aircraft model and engine model constitutes an Aircraft Variant (US057).

### Aircraft Variant
Represents a specific combination of an Aircraft Model and a certified Engine Model configuration.  
Introduced by US057: *"An aircraft model might have several aircraft variants (combinations of model and engine configuration)"*.  
An actual Aircraft is of a specific variant, not just a model.

### Engine Model
Identified by the combination of model name and manufacturer — this combination must be unique (US056).  
Defines engine type, thrust (power), fuel type, and TSFC (efficiency).  
Engine maintenance is explicitly out of scope (confirmed in forum Q&A).

### Airport
Identified globally by unique IATA (3-letter) and ICAO (4-letter) codes (US052).  
Characterized by name, town, country, coordinates, and elevation (altitude above sea level) (Section 3.2).  
Associated with exactly one Air Control Area.

### User
Identified by a unique valid email address.  
Has name and phone number.  
Must have an active security clearance with an expiration date, and a periodic skills assessment (Section 3.1.1).  
Roles in the system include: Admin, Backoffice Operator, Air Transport Company Collaborator, Pilot, Flight Control Operator, and Weather Person.  
At present, a user may only have one role at a time, though this may change in the future (Section 3.1.1).

### Pilot
Represents a User certified to operate one or more aircraft models (US075).  
Belongs to exactly one Air Transport Company at a time, though they may change company over time (confirmed in forum Q&A).  
Cannot be deactivated if they have flight plans currently assigned (US077).  
Has an active/inactive status within the company's roster.

### Collaborator
Represents a User associated with a customer entity — which may be either an Air Transport Company or an Air Control Area (US061).  
Carries a position attribute that belongs to the relationship between the user and the customer, not to either individually.  
Email and phone number may be edited; all other information is immutable after registration (US063).  
Can be disabled to revoke system access (US064).

### Flight Route
Identified by a unique route name composed of the company's 2-letter initials followed by up to 4 digits (e.g. TP123) (US073).  
Connects exactly two airports (origin and destination).  
Owned by an Air Transport Company — the company's identifier is embedded in the route name (confirmed in forum Q&A).  
Can be deactivated from a specific date onwards; no new flights may be created on a deactivated route (US074).  
Cannot be deactivated if there are planned flights after the deactivation date.

### Flight
Identified by a unique flight designator in the format `xxn(n)(n)(n)(a)`, where `xx` is the airline designator and `n(n)(n)(n)(a)` is the numeric flight number with optional operational suffix (Section 3.2).  
Represents a scheduled flight instance associated with a route.  
Has a type (regular or charter), departure day/time, and scheduled arrival.  
The relationship between Route, Flight, and Flight Plan is 1:N at each level — one route may have many flights, and one flight may have many flight plans, though only one may be approved/validated at a time (confirmed in forum Q&A).

### Flight Execution
Represents the execution of an individual flight during simulation.
According to US101, each flight is executed as a separate process that follows its flight plan and continuously sends position updates.
A Flight Execution executes one Flight Plan, has its own lifecycle and execution state (e.g. running, terminated) and produces a sequence of aircraft positions over time
The existence of a status attribute is justified by US109, which requires reporting individual execution statuses, confirming that each flight execution must be tracked independently.

### Flight Plan
Represents the formal, structured definition of how a flight takes place (Section 3.4).  
Described using the Core Flight DSL and validated through lexical, syntactic, and semantic analysis (US081, US083).  
Includes the aircraft, pilot, one or more flight legs, and fuel information.  
Status progresses from draft to validated.  
A flight plan is fundamentally a document, as stated by the client in the forum.

### Flight Leg
Represents a single, non-stop journey segment between two airports within a flight plan, defined by one take-off and one landing (Section 3.4.1).  
Each leg contains departure information, arrival information, route, one or more segments, and fuel data.  
The arrival airport of leg N must match the departure airport of leg N+1 (Section 3.4.5).  
**Note:** The client expressed doubt about whether Flight Leg needs to be represented in the DDD Domain Model, suggesting it may exist only within the DSL specification. The team has opted to maintain it given its explicit role in Section 3.4.1 and its relevance to weather data association (US082). This decision should be validated with the client.

### Segment
Represents a linear path connecting two coordinate points within a flight leg.  
Characterized by start and end coordinates, allowed altitude slots, and wind conditions (direction and speed) (Section 3.2).  
**Note:** The client suggested that segments and nodes may not need to be stored independently in the system and could exist solely as part of the DSL specification (confirmed in forum Q&A). The team has retained Segment in the model to support semantic validation and weather data association, but this decision should be validated.

### Weather Data (Weather Record)
Represents atmospheric conditions registered for a specific air control area on a specific day.  
Can be registered manually (US041), imported in bulk from external providers (US042), and consulted by Weather Persons, Pilots, and Flight Control Operators (US043).  
Weather conditions vary by location within an air control area and by altitude — they are not uniform across the area (confirmed in forum Q&A).  
May originate from multiple external weather service providers, allowing data aggregation for better accuracy (US042).

### Weather Provider
Represents an external weather data source.  
Weather data may originate from multiple providers to allow aggregation (US042).  
Does not have its own user stories for management — it is a reference entity used to identify the origin of imported weather records.

---

## 3. Value Objects

Value Objects represent descriptive concepts. They:

- Do not have identity
- Are compared by value
- Should be immutable

### Coordinates
Defined by latitude and longitude.  
Used to represent airport locations, segment endpoints, and weather measurement locations.  
Reused across multiple domain concepts.

### IATACode
Three-letter airport or company identifier with strict format constraints.  
Used in Airport and Air Transport Company.

### ICAOCode
Four-letter airport identifier or two-to-three-letter company code with strict format constraints.  
Used in Airport and Air Transport Company.

### AircraftRegistration
Represents the unique registration identifier of an aircraft.  
Globally unique and immutable once assigned.

### AircraftPosition
Represents the state of an aircraft at a specific moment during a simulation.
It includes coordinates, altitude, velocity and timestamp.
Each FlightExecution generates multiple AircraftPosition instances, creating a timeline of the aircraft’s movement.

### RouteName
Represents the unique identifier of a Flight Route.  
Format: two company-initial letters followed by up to four digits (e.g. TP123).  
Its format encodes company ownership.

### FlightDesignator
Represents the formatted identifier of a Flight.  
Format: airline designator (2 letters) + numeric flight number (up to 4 digits) + optional operational suffix (1 letter).

### FuelQuantity
Represents the amount of fuel assigned to a flight leg.  
Must be strictly positive (Section 3.4.5).  
Includes the numeric amount and a unit of measure.

### AltitudeSlot
Represents an allowed altitude band within a segment.  
Defined by minimum altitude, maximum altitude, and width.  
A segment may have multiple altitude slots simultaneously to support aircraft climbing and descending (Section 3.2, confirmed in forum Q&A).

### CabinConfiguration
Represents the number of seats per class (economy, business, first) in a specific aircraft.  
Total seats cannot exceed the aircraft model's maximum capacity (US070).

### SecurityClearance
Represents the security clearance level and expiration date associated with a User.  
Automatically expires at a given date and must be renewed (Section 3.1.1).

### SkillsAssessment
Represents the periodic skills assessment associated with a User.  
Required by regulation every five years (Section 3.1.1).  
Records the date of assessment and the date the next assessment is due.

### WeatherMeasurement
Represents a single atmospheric measurement within a Weather Data record.  
Defined by a parameter type (e.g. wind speed, temperature, pressure), a numeric value, and a unit.  
Has no identity of its own — it is meaningful only as part of a Weather Data record.  
Includes coordinates to reflect that conditions vary by location within an air control area.

### Manufacturer
Represents the maker of an aircraft model or engine model.  
Defined by name and country.  
Has no independent user stories for lifecycle management — it is descriptive information stored within the aircraft or engine model it belongs to (Section 3.1.2: *"only basic information (name and country) needs to be stored"*).  
A single entity may be both an aircraft and an engine manufacturer.

### GeographicBoundary
Represents the spatial limits of an Air Control Area.  
Must be valid upon registration (US050).

---

## 4. Observations

### 4.1 Identity is Central to the Domain

Most core domain objects require persistent identity:

- Aircraft → registration number
- Airport → IATA/ICAO codes
-  User → email
- Flight Route → route name (encoding company identity)
- Flight → flight designator
- Air Transport Company → IATA and ICAO codes

This indicates a domain strongly centered on Entities with well-defined, domain-meaningful identifiers — not surrogate keys.

---

### 4.2 Descriptive Concepts Should Not Be Modelled as Primitives

Several concepts initially appear as primitive types (e.g., String, double), but represent domain concepts with format constraints and business meaning. These must be modelled as Value Objects:

- Email addresses
- Airport and company codes (IATA/ICAO)
- Fuel quantity (positive, with unit)
- Coordinates (validated latitude/longitude pairs)
- Registration identifiers (formatted, globally unique)
- Route and flight designators (with format rules)

This aligns with the DDD principle that primitive types are often insufficient to model domain concepts.

---

### 4.3 Structural vs Independent Concepts

Some entities exist independently and have their own lifecycle (e.g. Airport, Aircraft, Flight Route), while others are structural parts of larger domain concepts (e.g. Flight Leg within a Flight Plan, Segment within a Flight Leg).

This distinction is critical during aggregate design: structural concepts that cannot exist outside their parent should be contained within the parent's aggregate boundary and accessed exclusively through it.

---

### 4.4 The Route–Flight–FlightPlan Hierarchy

A clarification from the client established the following 1:N hierarchy:

**Route → 1:N → Flight → 1:N → Flight Plan**

This means:
- A route may have many flights (e.g. TO3910 has flights on Tuesday, Wednesday, Thursday, Friday).
- A flight may have multiple flight plans, but only one may be approved/validated at a time.
- The flight designator and type (regular/charter) belong to **Flight**, not to Flight Plan.
- Flight Plan is the operational document describing how a specific flight takes place.

This hierarchy was absent from early versions of the domain model and is a critical structural clarification.

---

### 4.5 Debate on Flight Legs and Segments in the Domain Model

The client expressed doubt about whether Flight Legs need explicit representation in the DDD Domain Model, noting that they may exist solely within the DSL specification processed by ANTLR. Similarly, Segments and Nodes/Junctions were suggested to potentially reside only in the flight plan file specification rather than as stored domain objects.

The team has retained both Flight Leg and Segment in the domain model for the following reasons:

1. Section 3.4.1 explicitly defines the hierarchical structure: Flight Plan → Flight Leg → Segment.
2. US082 requires associating weather data with a flight plan — without segment-level granularity, it is not possible to associate weather conditions accurately, since conditions vary by location within an air control area.
3. Semantic validation rules in Section 3.4.5 (e.g. arrival airport of leg N must match departure of leg N+1) imply that legs have domain-meaningful structure.

This decision should be formally validated with the client before implementation.

---

### 4.6 Weather Data Granularity

The client confirmed that weather conditions are not uniform across an air control area — they vary by location and altitude. This has two implications for the domain model:

1. `WeatherMeasurement` (a Value Object within `WeatherData`) must carry `Coordinates` to express where within the area the measurement applies.
2. `AltitudeSlot` supports multiple simultaneous altitude bands per segment, which is consistent with this requirement.

---

### 4.7 Simulation Information Must Be in the System

The client confirmed that while the simulation itself runs as a C module, the information sent to the simulator and the results received back must be stored in the core Java system. This means `Simulation`, `Report`, and `SafetyViolation` are legitimate domain entities, not implementation artefacts.

Simulation performance settings (e.g. time step, precision) were deferred to Sprint 2 and need not be detailed in the current domain model.

---


### 4.8 Simulation Execution and Temporal Behaviour

The introduction of Flight Execution and AircraftPosition allows the domain model to represent runtime behaviour, not just static flight planning.

While Flight Plan defines how a flight should occur, Flight Execution captures how it actually unfolds during simulation.

AircraftPosition introduces a temporal dimension to the model, representing the aircraft’s state at specific points in time. This enables:

- Tracking aircraft movement over time
- Comparing positions of different aircraft at the same instant
- Supporting the detection of potential safety violations

This design aligns with the simulation requirements, where flights are executed step-by-step and their positions are continuously monitored.

### 4.9 Node/Junction Exclusion from the Domain Model

Although nodes/junctions appear in the specification, the client said in the forum that there are no user stories that require managing them, that we can avoid that.

For that reason, Node/Junction was not included in the Domain Model.



