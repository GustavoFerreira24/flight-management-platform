# <span style="color:#E67E22">Air Transport Company Aggregate</span>

## <span style="color:#2E75B6">Scenario</span>

The Backoffice Operator registers a new air transport company by providing its name, country, IATA code, and ICAO code.
The system must ensure that the company codes are unique and that the company is created with one consistent set of 
identifying information.

## <span style="color:#2E75B6">Domain Model</span>

![AirTransportCompany DM](../../global-artifacts/02.analysis/svg/Aggregates/AirTransportCompanyAggregate.svg)

## <span style="color:#2E75B6">Sequence Diagram</span>

![AirTransportCompany SD](../../global-artifacts/03.design/svg/AirTransportCompanyAggregate-SD.svg)

## <span style="color:#2E75B6">Invariants</span>

- <span style="color:#70AD47">Unique Company Codes</span>

  An `AirTransportCompany` must have a unique `IATACode` and a unique `ICAOCode` in the system.


- <span style="color:#70AD47">Consistent Identification</span>

  An `AirTransportCompany` must always have one `Country`, one `IATACode`, and one `ICAOCode`.


## <span style="color:#2E75B6">Justification</span>

- <span style="color:#70AD47">Aggregate Root Responsibility</span>

  `AirTransportCompany` is the Aggregate Root and the single entry point for creating a new air transport company. It 
encapsulates the creation of `Country`, `IATACode`, and `ICAOCode`, ensuring that these Value Objects cannot exist 
independently outside the aggregate.


- <span style="color:#70AD47">Internal Consistency Enforcement</span>

  The aggregate is responsible for keeping together the company’s identifying information as one consistent whole. This 
prevents the creation of incomplete or ambiguous company definitions inside the aggregate boundary.


- <span style="color:#70AD47">System-Wide Uniqueness Support</span>

  The uniqueness of `IATACode` and `ICAOCode` is a business rule that depends on the global state of the system. 
Therefore, it is verified through the repository before the aggregate is persisted, ensuring that no duplicate company 
codes exist.


- <span style="color:#70AD47">Encapsulation of Internal State</span>

  External components do not manipulate `Country`, `IATACode`, or `ICAOCode` directly.  
  They interact only with `AirTransportCompany`, which preserves the consistency of the aggregate and clearly defines its boundary.