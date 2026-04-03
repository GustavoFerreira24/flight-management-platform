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

- <span style="color:#70AD47">Aggregate Root Responsibility</span>

  `AirControlArea` is the Aggregate Root and the single access point for creating a new air control area. It 
encapsulates the creation of `AirControlAreaCode` and `GeographicBoundary`, ensuring that these Value Objects cannot be 
created or changed independently outside the aggregate.


- <span style="color:#70AD47">Internal Consistency Enforcement</span>

  The aggregate is responsible for guaranteeing that its internal state is always valid. In this scenario, that means 
the `GeographicBoundary` can only be created with consistent coordinate limits, namely `minLatitude < maxLatitude` and 
`minLongitude < maxLongitude`.


- <span style="color:#70AD47">System-Wide Uniqueness Support</span>

  The uniqueness of `AirControlAreaCode` is a business rule that depends on the existing state of the system. Therefore, 
it is verified through the repository before the aggregate is persisted. The registration only succeeds if no other 
`AirControlArea` already uses the same code.


- <span style="color:#70AD47">Encapsulation of Internal State</span>

  External components do not manipulate `AirControlAreaCode` or `GeographicBoundary` directly. They interact only with 
`AirControlArea`, which preserves the consistency of the aggregate and makes its boundary explicit.