# <span style="color:#E67E22">Air Control Area Aggregate</span>

## <span style="color:#2E75B6">Scenario</span>

The Backoffice Operator registers a new air control area by providing its name, unique area code, and geographic boundary.
The system must ensure that the area code is unique in the system and that the geographic boundary is valid.

## <span style="color:#2E75B6">Domain Model</span>

![AirControlArea DM](../../global-artifacts/02.analysis/svg/Aggregates/AirControlAreaAggregate.svg)

## <span style="color:#2E75B6">Sequence Diagram</span>

![AirControlArea SD](../../global-artifacts/03.design/svg/AirControlAreaAggregate-SD.svg)

## <span style="color:#2E75B6">Invariants</span>

- <span style="color:#70AD47">Unique Area Code</span>

  An `AirControlArea` must have a unique `AirControlAreaCode` in the system.

- <span style="color:#70AD47">Valid Geographic Boundary</span>

  A `GeographicBoundary` must always be valid, meaning `minLatitude < maxLatitude` and `minLongitude < maxLongitude`.


## <span style="color:#2E75B6">Justification</span>

- <span style="color:#70AD47">Boundary Integrity</span>

  The `AirControlArea` acts as the Aggregate Root and is responsible for creating and keeping together its identifying and 
spatial information. Since `AirControlAreaCode` and `GeographicBoundary` have no independent lifecycle outside the context 
of the area, the root ensures that the aggregate is always created in a valid state.

- <span style="color:#70AD47">Business Invariant Control</span>

  This scenario demonstrates that the `AirControlArea` is responsible for enforcing the business rules associated with its 
identity and boundary. By centralizing validation inside the Aggregate Root, invalid coordinate ranges and inconsistent 
area definitions are prevented.

- <span style="color:#70AD47">Encapsulation of Internal State</span>

  External components do not manipulate `AirControlAreaCode` or `GeographicBoundary` directly. They interact only with the
  `AirControlArea`, which decides whether the creation operation is valid. This preserves the consistency of the aggregate 
and makes its boundary explicit.

