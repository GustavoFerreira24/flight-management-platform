# <span style="color:#E67E22">Airport Aggregate</span>

## <span style="color:#2E75B6">Scenario</span>

The Backoffice Operator registers a new airport by providing its name, town, country, coordinates, altitude, IATA code, ICAO code, and associated air control area.  
The system must ensure that the airport codes are unique, that the coordinates are valid, and that the airport is associated with exactly one existing air control area.

## <span style="color:#2E75B6">Domain Model</span>

![Airport DM](../../global-artifacts/02.analysis/svg/Aggregates/AirportAggregate.svg)

## <span style="color:#2E75B6">Sequence Diagram</span>

![Airport SD](../../global-artifacts/03.design/svg/AirportAggregate-SD.svg)

## <span style="color:#2E75B6">Invariants</span>

- <span style="color:#70AD47">Unique Airport Codes</span>

  An `Airport` must have a unique `IATACode` and a unique `ICAOCode` in the system.


- <span style="color:#70AD47">Valid Coordinates</span>

  The `Coordinates` associated with an `Airport` must always represent a valid geographic location.


- <span style="color:#70AD47">Single Air Control Area Association</span>

  An `Airport` must always be associated with exactly one `AirControlArea`.

## <span style="color:#2E75B6">Justification</span>

- <span style="color:#70AD47">Aggregate Root Responsibility</span>

  `Airport` is the Aggregate Root and the single entry point for creating a new airport. It encapsulates the creation of 
`Coordinates`, `Country`, `IATACode`, and `ICAOCode`, ensuring that these Value Objects cannot exist independently 
outside the aggregate.


- <span style="color:#70AD47">Internal Consistency Enforcement</span>

  The aggregate guarantees that its internal state is always valid. In this scenario, this includes ensuring that the 
`Coordinates` represent a valid geographic location.


- <span style="color:#70AD47">System-Wide Uniqueness Support</span>

  The uniqueness of `IATACode` and `ICAOCode` is a business rule that depends on the global state of the system. 
Therefore, it is verified through the repository before the aggregate is persisted, ensuring that no duplicate airport 
codes exist.


- <span style="color:#70AD47">Association with Another Aggregate Root</span>

  An `Airport` must always be associated with exactly one `AirControlArea`, which is another Aggregate Root in the 
domain model. In this scenario, the associated area is obtained through its repository before the airport is created, 
ensuring that the relation is established only with an existing aggregate root.


- <span style="color:#70AD47">Encapsulation of Internal State</span>

  External components do not manipulate `Coordinates`, `Country`, `IATACode`, or `ICAOCode` directly. They interact only 
with `Airport`, which preserves the consistency of the aggregate and clearly defines its boundary.