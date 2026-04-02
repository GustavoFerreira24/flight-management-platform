# <span style="color:#E67E22">Airport Aggregate</span>

## <span style="color:#2E75B6">Scenario</span>

The Backoffice Operator registers a new airport by providing its name, town, country, coordinates, altitude, IATA code, 
ICAO code, and associated air control area.
The system must ensure that the airport codes are unique and that the coordinates are valid.

## <span style="color:#2E75B6">Domain Model</span>

![Airport DM](../../global-artifacts/02.analysis/svg/Aggregates/AirportAggregate.svg)

## <span style="color:#2E75B6">Sequence Diagram</span>

![Airport SD](../../global-artifacts/03.design/svg/AirportAggregate-SD.svg)

## <span style="color:#2E75B6">Invariants</span>

- <span style="color:#70AD47">Unique Airport Codes</span>

  An `Airport` must have a unique `IATACode` and a unique `ICAOCode` in the system.

- <span style="color:#70AD47">Valid Coordinates</span>

  The `Coordinates` associated with an `Airport` must always represent a valid geographic location.

- <span style="color:#70AD47">Consistent Identification</span>

  An `Airport` must always have exactly one `IATACode` and exactly one `ICAOCode`.


## <span style="color:#2E75B6">Justification</span>

- <span style="color:#70AD47">Airport Integrity</span>

  The `Airport` acts as the Aggregate Root and is responsible for creating and keeping together all information that 
identifies and locates the airport. Since `Coordinates`, Country, `IATACode`, and `ICAOCode` have no independent lifecycle 
outside the airport context, the root ensures that the aggregate is created in a consistent state.

- <span style="color:#70AD47">Business Invariant Control</span>

  This scenario demonstrates that the `Airport` is responsible for enforcing the core business rules of the aggregate, 
especially the validity of coordinates and the existence of one consistent set of airport codes. By centralizing 
validation inside the Aggregate Root, invalid or ambiguous airport definitions are prevented.

- <span style="color:#70AD47">Encapsulation of Internal State</span>

  External components do not manipulate `Coordinates`, `Country`, `IATACode`, or `ICAOCode` directly. They interact only with
the `Airport`, which determines whether the registration is valid. This preserves the consistency of the aggregate and 
makes its boundary explicit.
