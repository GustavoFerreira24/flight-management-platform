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

    An `AirTransportCompany` must always have exactly one Country, exactly one `IATACode`, and exactly one `ICAOCode`.

## <span style="color:#2E75B6">Justification</span>

- <span style="color:#70AD47">Company Integrity</span>

  The `AirTransportCompany` acts as the Aggregate Root and is responsible for creating and keeping together the company’s 
identifying information. Since `Country`, `IATACode`, and `ICAOCode` have no independent lifecycle outside the company 
context, the root ensures that the aggregate is created in a valid and consistent state.

- <span style="color:#70AD47">Business Invariant Control</span>

  This scenario demonstrates that the `AirTransportCompany` is responsible for enforcing the core business rules of the 
aggregate, especially the requirement that the company has one coherent set of identification codes. By centralizing 
validation inside the Aggregate Root, invalid or ambiguous company definitions are prevented.

- <span style="color:#70AD47">Encapsulation of Internal State</span>

  External components do not manipulate Country, `IATACode`, or `ICAOCode` directly. They interact only with the
`AirTransportCompany`, which decides whether the registration is valid. This preserves the consistency of the aggregate 
and makes its boundary explicit.
