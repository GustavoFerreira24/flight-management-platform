# <span style="color:#E67E22">Domain Analysis – Entities and Value Objects</span>

---

## <span style="color:#2E75B6">1. Purpose</span>

This document defines the classification of domain concepts into **Entities** and **Value Objects**, according to Domain-Driven Design (DDD) principles.

The classification is based strictly on the system requirements (Section 3 and all User Stories), the forum Q&A with the client, and follows the definitions provided in the course materials.

According to DDD:

- An **Entity** is an object with identity that persists over time.
- A **Value Object** is defined only by its attributes, has no identity, and should be immutable.

This classification precedes aggregate design and focuses exclusively on domain semantics.

---

## <span style="color:#2E75B6">2. Entities</span>

Entities are domain concepts that:

- Have a unique identity
- Exist independently over time
- Are subject to lifecycle changes
- Are referenced by other parts of the system

### <span style="color:#70AD47">Air Control Area</span>
Identified by a unique area code.  
Manages geographic boundaries and is referenced by airports and weather data.  
Weather conditions are not uniform across an air control area — they vary by location and altitude within it (confirmed in forum Q&A).  
Created and managed by Backoffice Operators (US050).

### <span style="color:#70AD47">Air Transport Company</span>
Identified by unique IATA (2 letters) and ICAO (2–3 letters) codes.  
Manages aircraft, flight routes, collaborators, and pilots.  
Owns and is responsible for flight routes — a route's name includes the company's initials (e.g. TP123), confirming ownership (US060, US073, forum Q&A).

### <span style="color:#70AD47">Aircraft</span>
Identified by a unique registration number, globally unique.  
Has an operational status (active/decommissioned) and cabin configuration.  
Registered in a country, which may differ from the company's home country.  
Cannot be used in flight plans if decommissioned (US071).  
Belongs to exactly one Air Transport Company (US070).  
An aircraft is of a specific model variant, which includes engine configuration.

### <span style="color:#70AD47">Aircraft Model</span>
Identified by the combination of model name and manufacturer — this combination must be unique (US055).  
Has technical characteristics required for flight simulation: type, motorization, maximum range, empty weight, MTOW, MZFW, maximum fuel capacity, service ceiling, cruise speed, wing area, drag coefficient, and lift coefficient (Section 3.2).  
Must have at least one certified engine model. A specific combination of aircraft model and engine model constitutes an Aircraft Variant (US057).

### <span style="color:#70AD47">Aircraft Variant</span>
Represents a specific combination of an Aircraft Model and a certified Engine Model configuration.  
Introduced by US057: *"An aircraft model might have several aircraft variants (combinations of model and engine configuration)"*.  
An actual Aircraft is of a specific variant, not just a model.

### <span style="color:#70AD47">Engine Model</span>
Identified by the combination of model name and manufacturer — this combination must be unique (US056).  
Defines engine type, thrust (power), fuel type, and TSFC (efficiency).  
Engine maintenance is explicitly out of scope (confirmed in forum Q&A).

### <span style="color:#70AD47">Airport</span>
Identified globally by unique IATA (3-letter) and ICAO (4-letter) codes (US052).  
Characterized by name, town, country, coordinates, and elevation (altitude above sea level) (Section 3.2).  
Associated with exactly one Air Control Area.

### <span style="color:#70AD47">User</span>
Identified by a unique valid email address.  
Has name and phone number.  
Must have an active security clearance with an expiration date, and a periodic skills assessment (Section 3.1.1).  
Roles in the system include: Admin, Backoffice Operator, Air Transport Company Collaborator, Pilot, Flight Control Operator, and Weather Person.  
At present, a user may only have one role at a time, though this may change in the future (Section 3.1.1).

### <span style="color:#70AD47">Pilot</span>
Represents a User certified to operate one or more aircraft models (US075).  
Belongs to exactly one Air Transport Company at a time, though they may change company over time (confirmed in forum Q&A).  
Cannot be deactivated if they have flight plans currently assigned (US077).  
Has an active/inactive status within the company's roster.

### <span style="color:#70AD47">Flight Route</span>
Identified by a unique route name composed of the company's 2-letter initials followed by up to 4 digits (e.g. TP123) (US073).  
Connects exactly two airports (origin and destination).  
Owned by an Air Transport Company — the company's identifier is embedded in the route name (confirmed in forum Q&A).  
Can be deactivated from a specific date onwards; no new flights may be created on a deactivated route (US074).  
Cannot be deactivated if there are planned flights after the deactivation date.

### <span style="color:#70AD47">Flight</span>
Identified by a unique flight designator in the format `xxn(n)(n)(n)(a)`, where `xx` is the airline designator and `n(n)(n)(n)(a)` is the numeric flight number with optional operational suffix (Section 3.2).  
Represents a scheduled flight instance associated with a route.  
Has a type (regular or charter), departure day/time, and scheduled arrival.  
The relationship between Route, Flight, and Flight Plan is 1:N at each level — one route may have many flights, and one flight may have many flight plans, though only one may be approved/validated at a time (confirmed in forum Q&A).

### <span style="color:#70AD47">Flight Execution</span>
Represents the execution of an individual flight during simulation.  
According to US101, each flight is executed as a separate process that follows its flight plan and continuously sends position updates.  
A Flight Execution executes one Flight Plan, has its own lifecycle and execution state (e.g. running, terminated) and produces a sequence of aircraft positions over time.  
The existence of a status attribute is justified by US109, which requires reporting individual execution statuses, confirming that each flight execution must be tracked independently.

### <span style="color:#70AD47">Flight Plan</span>
Represents the formal, structured definition of how a flight takes place (Section 3.4).  
Described using the Core Flight DSL and validated through lexical, syntactic, and semantic analysis (US081, US083).  
Includes the aircraft, pilot, one or more flight legs, and fuel information.  
Status progresses from draft to validated.  
A flight plan is fundamentally a document, as stated by the client in the forum.

### <span style="color:#70AD47">Weather Data (Weather Record)</span>
Represents atmospheric conditions registered for a specific air control area on a specific day.
Can be registered manually (US041), imported in bulk from external providers (US042), and consulted by Weather Persons, Pilots, and Flight Control Operators (US043).  
Weather conditions vary by location within an air control area and by altitude — they are not uniform across the area (confirmed in forum Q&A).  
May originate from multiple external weather service providers, allowing data aggregation for better accuracy (US042).

### <span style="color:#70AD47">Weather Provider</span>
Represents an external weather data source.
Weather data may originate from multiple providers to allow aggregation (US042).  
Does not have its own user stories for management — it is a reference entity used to identify the origin of imported weather records.

### <span style="color:#70AD47">Manufacturer</span>
Identified by name and country.  
Represents the maker of aircraft models or engine models.  
Is referenced by AircraftModel and EngineModel.  
Has its own identity and exists independently of those models.

---

## <span style="color:#2E75B6">3. Value Objects</span>

Value Objects represent descriptive concepts. They:

- Do not have identity
- Are compared by value
- Should be immutable

### <span style="color:#70AD47">Coordinates</span>
Defined by latitude and longitude.  
Used to represent airport locations, segment endpoints, and weather measurement locations.  
Reused across multiple domain concepts.

### <span style="color:#70AD47">IATACode</span>
Three-letter airport or company identifier with strict format constraints.  
Used in Airport and Air Transport Company.

### <span style="color:#70AD47">ICAOCode</span>
Four-letter airport identifier or two-to-three-letter company code with strict format constraints.  
Used in Airport and Air Transport Company.

### <span style="color:#70AD47">AircraftRegistration</span>
Represents the unique registration identifier of an aircraft.  
Globally unique and immutable once assigned.

### <span style="color:#70AD47">AircraftPosition</span>
Represents the state of an aircraft at a specific moment during a simulation.
It includes coordinates, altitude, velocity and timestamp.
Each FlightExecution generates multiple AircraftPosition instances, creating a timeline of the aircraft’s movement.
AircraftPosition belongs to a FlightExecution and does not exist independently.

### <span style="color:#70AD47">RouteName</span>
Represents the unique identifier of a Flight Route.  
Format: two company-initial letters followed by up to four digits (e.g. TP123).  
Its format encodes company ownership.

### <span style="color:#70AD47">FlightDesignator</span>
Represents the formatted identifier of a Flight.  
Format: airline designator (2 letters) + numeric flight number (up to 4 digits) + optional operational suffix (1 letter).

### <span style="color:#70AD47">FuelQuantity</span>
Represents the amount of fuel assigned to a flight leg.  
Must be strictly positive (Section 3.4.5).  
Includes the numeric amount and a unit of measure.

### <span style="color:#70AD47">CabinConfiguration</span>
Represents the number of seats per class (economy, business, first) in a specific aircraft.  
Total seats cannot exceed the aircraft model's maximum capacity (US070).

### <span style="color:#70AD47">SecurityClearance</span>
Represents the security clearance level and expiration date associated with a User.  
Automatically expires at a given date and must be renewed (Section 3.1.1).

### <span style="color:#70AD47">SkillsAssessment</span>
Represents the periodic skills assessment associated with a User.  
Required by regulation every five years (Section 3.1.1).  
Records the date of assessment and the date the next assessment is due.

### <span style="color:#70AD47">WeatherMeasurement</span>
Represents a single atmospheric measurement within a Weather Data record.  
Defined by a parameter type (e.g. wind speed, temperature, pressure), a numeric value, and a unit.  
Has no identity of its own — it is meaningful only as part of a Weather Data record.  
Includes coordinates to reflect that conditions vary by location within an air control area.

### <span style="color:#70AD47">GeographicBoundary</span>
Represents the spatial limits of an Air Control Area.  
Must be valid upon registration (US050).

---

## <span style="color:#2E75B6">4. Observations</span>

### <span style="color:#70AD47">4.1 Identity is Central to the Domain</span>

Most core domain objects require persistent identity:

- Aircraft → registration number
- Airport → IATA/ICAO codes
-  User → email
- Flight Route → route name (encoding company identity)
- Flight → flight designator
- Air Transport Company → IATA and ICAO codes

This indicates a domain strongly centered on Entities with well-defined, domain-meaningful identifiers — not surrogate keys.

---

### <span style="color:#70AD47">4.2 Descriptive Concepts Should Not Be Modelled as Primitives</span>

Several concepts initially appear as primitive types (e.g., String, double), but represent domain concepts with format constraints and business meaning. These must be modelled as Value Objects:

- Email addresses
- Airport and company codes (IATA/ICAO)
- Fuel quantity (positive, with unit)
- Coordinates (validated latitude/longitude pairs)
- Registration identifiers (formatted, globally unique)
- Route and flight designators (with format rules)

This aligns with the DDD principle that primitive types are often insufficient to model domain concepts.

---

### <span style="color:#70AD47">4.3 Structural vs Independent Concepts</span>

Some entities exist independently and have their own lifecycle (e.g. Airport, Aircraft, Flight Route), while others are structural or descriptive parts of larger domain concepts (e.g. AircraftPosition within a FlightExecution, WeatherMeasurement within WeatherData).
This distinction is critical during aggregate design: structural concepts that cannot exist outside their parent should be contained within the parent's aggregate boundary and accessed exclusively through it.

---

### <span style="color:#70AD47">4.4 The Route–Flight–FlightPlan Hierarchy</span>

A clarification from the client established the following 1:N hierarchy:

**Route → 1:N → Flight → 1:N → Flight Plan**

This means:
- A route may have many flights (e.g. TO3910 has flights on Tuesday, Wednesday, Thursday, Friday).
- A flight may have multiple flight plans, but only one may be approved/validated at a time.
- The flight designator and type (regular/charter) belong to **Flight**, not to Flight Plan.
- Flight Plan is the operational document describing how a specific flight takes place.

This hierarchy was absent from early versions of the domain model and is a critical structural clarification.

---

### <span style="color:#70AD47">4.5 Flight Legs and Segments Outside the Domain Model</span>

Although Section 3.4.1 defines the structure Flight Plan → Flight Leg → Segment, the client later suggested that Flight Legs, Segments, and Nodes/Junctions may exist only as part of the flight plan specification processed by ANTLR, rather than as explicit concepts in the DDD Domain Model.

Following that clarification, the team decided not to include Flight Leg and Segment in the final Domain Model.

This decision is justified because there are no user stories requiring their independent management, and their main role is within DSL specification, validation, and processing.

---

### <span style="color:#70AD47">4.6 Weather Data Granularity</span>

The client confirmed that weather conditions are not uniform across an air control area — they vary by location and altitude. This has two implications for the domain model:

1. `WeatherMeasurement` (a Value Object within `WeatherData`) must carry `Coordinates` to express where within the area the measurement applies.
2. `AltitudeSlot` supports multiple simultaneous altitude bands per segment, which is consistent with this requirement.

---

### <span style="color:#70AD47">4.7 Simulation Information Must Be in the System</span>

The client confirmed that while the simulation itself runs as a C module, the information sent to the simulator and the results received back must be stored in the core Java system.  
This justifies the presence of the Simulation aggregate.

Within the simulation:
- FlightExecution represents execution state and is modelled as an Entity
- AircraftPosition represents temporal state as a Value Object
- Report and SafetyViolation represent descriptive results of the simulation

Simulation performance settings (e.g. time step, precision) were deferred to Sprint 2 and need not be detailed in the current domain model.

---

### <span style="color:#70AD47">4.8 Simulation Execution and Temporal Behaviour</span>

The introduction of Flight Execution and AircraftPosition allows the domain model to represent runtime behaviour, not just static flight planning.

While Flight Plan defines how a flight should occur, Flight Execution captures how it actually unfolds during simulation.

AircraftPosition introduces a temporal dimension to the model, representing the aircraft’s state at specific points in time. This enables:

- Tracking aircraft movement over time
- Comparing positions of different aircraft at the same instant
- Supporting the detection of potential safety violations

This design aligns with the simulation requirements, where flights are executed step-by-step and their positions are continuously monitored.

### <span style="color:#70AD47">4.9 Node/Junction Exclusion from the Domain Model</span>

Although nodes/junctions appear in the specification, the client said in the forum that there are no user stories that require managing them, that we can avoid that.

For that reason, Node/Junction was not included in the Domain Model.

### <span style="color:#70AD47">4.10 The Role of Aircraft Variant</span>

The introduction of `AircraftVariant` clarifies how the domain connects aircraft models, certified engine models, and actual aircraft.

Although an Aircraft Model defines the general characteristics of an aircraft, the requirements make it clear that engine configuration is also domain-relevant. An aircraft model may support multiple certified engine models, and each valid combination of aircraft model and engine model constitutes a distinct aircraft variant.

This distinction is important because an actual `Aircraft` is not only of a given model, but of a specific variant, including its engine configuration. Without `AircraftVariant`, the model would not express which certified engine configuration is actually being used by a given aircraft.
