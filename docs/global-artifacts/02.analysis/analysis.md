# Domain Analysis – Entities and Value Objects

## 1. Purpose

This document defines the classification of domain concepts into **Entities** and **Value Objects**, according to Domain-Driven Design (DDD) principles.

The classification is based strictly on the system requirements (Section 3 and all User Stories) and follows the definitions provided in the course materials.

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

### Air Transport Company
Identified by unique IATA and ICAO codes.  
Manages aircraft, flight routes, collaborators, and pilots.

### Aircraft
Identified by a unique registration.  
Has operational status and configuration.  
Cannot be used in flights if decommissioned.

### Aircraft Model
Identified by model name and manufacturer combination.  
Has technical characteristics and a set of certified engine models.

### Engine Model
Identified by model name and manufacturer combination.  
Defines engine type, power, efficiency, and fuel characteristics.

### Airport
Identified globally by unique IATA and ICAO codes.  
Has geographic location and elevation.  
Associated with exactly one Air Control Area.

### User
Identified by a unique valid email.  
Has name, phone number, security clearance, and skills assessment data.

### Pilot
Represents a system user certified to operate one or more aircraft models.  
Cannot be removed if assigned to existing flight plans.

### Collaborator
Represents a user associated with an Air Transport Company or Air Control Area.  
Used to manage operational data.

### Flight Route
Identified by a unique route name.  
Connects two airports and may be activated or deactivated.

### Flight
Identified by a unique flight designator.  
Represents a scheduled flight instance between airports.

### Flight Plan
Represents the structured definition of a flight.  
Includes route, legs, segments, fuel data, and validation status.

### Flight Leg
Represents one take-off and landing within a flight plan.

### Segment
Represents a linear path between two coordinates with altitude constraints.

### Weather Record
Represents atmospheric conditions associated with a specific air control area and date.  
Can be registered, imported, and queried.

---

## 3. Value Objects

Value Objects represent descriptive concepts. They:

- Do not have identity
- Are compared by value
- Should be immutable

The following domain concepts are classified as Value Objects:

### Coordinates
Defined by latitude and longitude.  
Used to represent airport locations and segment endpoints.

### IATACode
Three-letter airport or company identifier with format constraints.

### ICAOCode
Four-letter airport identifier or company code with format constraints.

### AircraftRegistration
Represents the registration identifier of an aircraft.

### RouteName
Represents the unique identifier of a flight route.

### FlightDesignator
Represents the formatted identifier of a flight.

### FuelQuantity
Represents the amount of fuel assigned to a flight or leg.  
Must be strictly positive.

### DateTime
Represents departure and arrival times.  
Must correspond to valid calendar values.

### GeographicBoundary
Represents the spatial limits of an Air Control Area.

### SecurityClearance
Represents clearance level and expiration date associated with a user.

### CabinConfiguration
Represents the number of seats per class in an aircraft.

---

## 4. Observations

### 4.1 Identity is Central to the Domain

Most core domain objects require persistent identity:

- Aircraft → registration
- Airport → IATA/ICAO
- User → email
- Route → name
- Flight → designator

This indicates a domain strongly centered on Entities.

---

### 4.2 Descriptive Concepts Should Not Be Modeled as Primitives

Several concepts initially appear as primitive types (e.g., String, double), but represent domain concepts and therefore must be modeled as Value Objects:

- Email
- Codes (IATA/ICAO)
- Fuel quantity
- Coordinates
- Registration identifiers

This aligns with the DDD principle that primitive types are often insufficient to model domain concepts.

---

### 4.3 Structural vs Independent Concepts

Some entities exist independently (e.g., Airport, Aircraft), while others are structural parts of larger domain concepts (e.g., Flight Leg within a Flight Plan).

This distinction will be considered during aggregate design.

---