# US 072 - List an Air Transport Company's Aircraft Fleet

## 1. Context

This user story allows an authenticated Air Transport Company Collaborator to list the aircraft fleet of the company they belong to. The listing can be shown without filters or restricted by aircraft model, maker, seat capacity or aircraft age.

The owning company is not provided by the user. It is resolved from the authenticated collaborator, following the same security rule used by US070 and US071.

## 2. Requirements

**US072** As an Air Transport Company Collaborator, I want to list my company's aircraft fleet, optionally filtered by model, maker, capacity, or age.

**Acceptance Criteria:**

- US072.1 The system must list only aircraft belonging to the authenticated collaborator's company.
- US072.2 The listing can be shown without filters.
- US072.3 The listing can be filtered by aircraft model.
- US072.4 The listing can be filtered by maker/manufacturer.
- US072.5 The listing can be filtered by seat capacity.
- US072.6 The listing can be filtered by aircraft age.
- US072.7 The listing must show relevant aircraft data for each result.

**Dependencies/References:**

This requirement depends on US070 because the fleet is composed of aircraft registered in the company. It also depends on the customer collaborator association used to resolve the authenticated air transport company.

## 3. Analysis

Fleet ownership is represented by the association between `Aircraft` and `AirTransportCompany`. The repository already exposes `findByCompany`, so the application layer can retrieve the authenticated company's aircraft and apply the optional filters.

Aircraft age is calculated from `Aircraft.manufactureYear`. This year is collected when registering new aircraft and validated in the domain. Capacity filtering uses the aircraft's actual configured seat count, i.e. `CabinConfiguration.totalSeats()`.

## 4. Design

The main flow is:

- The user logs in as an Air Transport Company Collaborator.
- The user selects `List Aircraft Fleet (US72)` from the Air Company menu.
- The UI asks whether filters should be applied.
- Optional filters are collected: model text, maker text, minimum/maximum total seats, minimum/maximum age.
- `ListAircraftFleetController` authorizes the operation with `AIR_COMPANY_CONFIGURATION`.
- The controller resolves the authenticated collaborator's company.
- The controller loads aircraft with `AircraftRepository.findByCompany(company)`.
- The controller maps each aircraft to `AircraftFleetEntry` and applies the filters.
- The UI prints the matching aircraft.

## 5. Implementation

Main implementation artifacts:

- `ListAircraftFleetUI`: console UI for collecting filters and printing results.
- `ListAircraftFleetController`: application controller for the fleet listing.
- `AircraftFleetFilter`: optional filter object.
- `AircraftFleetEntry`: read model returned to the UI.
- `Aircraft.manufactureYear`: manufacture year used to calculate aircraft age.
- `Aircraft.ageInYears()`: derived aircraft age.

The listing displays:

- registration;
- aircraft model;
- maker/manufacturer;
- configured total seats;
- model passenger capacity;
- manufacture year;
- age in years;
- operational status;
- engine model.

## 6. Tests

Implemented in `ListAircraftFleetControllerTest`:

- list only the authenticated company's aircraft;
- exclude aircraft from other companies;
- filter by model, maker, capacity and age;
- accept a null/empty filter as no filtering;
- reject invalid filter ranges;
- enforce authorization;
- reject users that are not customer collaborators.

Domain support for age is covered in `AircraftTest`.

## 7. Integration/Demonstration

Run the console application and login as an air company collaborator, for example:

```text
Username/Email: tap.collaborator@aisafe.com
Password: Password1
```

Then select:

```text
3 - List Aircraft Fleet (US72)
```

from the Air Company Management Console.
