# <span style="color:#E67E22">Manufacturer Aggregate</span>

## <span style="color:#2E75B6">Scenario</span>

The Backoffice Operator registers a new manufacturer by providing its name, designator, and country.
The system must ensure that the manufacturer is created with valid identifying information and one consistent country.

## <span style="color:#2E75B6">Domain Model</span>

![Manufacturer DM](../../global-artifacts/02.analysis/svg/Aggregates/ManufacturerAggregate.svg)

## <span style="color:#2E75B6">Sequence Diagram</span>

![Manufacturer SD](../../global-artifacts/03.design/svg/ManufacturerAggregate-SD.svg)

## <span style="color:#2E75B6">Invariants</span>

- <span style="color:#70AD47">Unique Manufacturer Designator</span>

  A `Manufacturer` must have a unique `ManufacturerDesignator` in the system.

- <span style="color:#70AD47">Consistent Identification</span>

  A `Manufacturer` must always have exactly one valid `ManufacturerDesignator` and exactly one `Country`.


## <span style="color:#2E75B6">Justification</span>

- <span style="color:#70AD47">Manufacturer Integrity</span>

  The `Manufacturer` acts as the Aggregate Root and is responsible for creating and keeping together the manufacturer’s 
identifying information. Since `ManufacturerDesignator` and `Country` have no independent lifecycle outside the manufacturer 
context, the root ensures that the aggregate is created in a valid and consistent state.

- <span style="color:#70AD47">Business Invariant Control</span>

  This scenario demonstrates that the `Manufacturer` is responsible for enforcing the core business rules of the aggregate, 
especially the consistency of its identifying information. By centralizing validation inside the Aggregate Root, invalid 
or incomplete manufacturer definitions are prevented.

- <span style="color:#70AD47">Encapsulation of Internal State</span>

  External components do not manipulate `ManufacturerDesignator` or `Country` directly. They interact only with the
`Manufacturer`, which decides whether the registration is valid. This preserves the consistency of the aggregate and makes 
its boundary explicit.
