# <span style="color:#E67E22">Manufacturer Aggregate</span>

## <span style="color:#2E75B6">Scenario</span>

The Backoffice Operator registers a new manufacturer by providing its name, designator, country, and manufacturer type.  
The system must ensure that the manufacturer is created with valid identifying information and one consistent technical classification.

## <span style="color:#2E75B6">Domain Model</span>

![Manufacturer DM](../../global-artifacts/02.analysis/svg/Aggregates/ManufacturerAggregate.svg)

## <span style="color:#2E75B6">Sequence Diagram</span>

![Manufacturer SD](../../global-artifacts/03.design/svg/ManufacturerAggregate-SD.svg)

## <span style="color:#2E75B6">Invariants</span>

- <span style="color:#70AD47">Unique Manufacturer Designator</span>

  A `Manufacturer` must have a unique `ManufacturerDesignator` in the system.


- <span style="color:#70AD47">Consistent Identification</span>

  A `Manufacturer` must always have one valid `ManufacturerDesignator`, one `Country`, and one `ManufacturerType`.


## <span style="color:#2E75B6">Justification</span>

- <span style="color:#70AD47">Aggregate Root Responsibility</span>

  `Manufacturer` is the Aggregate Root and the single entry point for creating a new manufacturer. It encapsulates the 
creation of `ManufacturerDesignator`, `Country`, and `ManufacturerType`, ensuring that these Value Objects cannot exist 
independently outside the aggregate.


- <span style="color:#70AD47">Internal Consistency Enforcement</span>

  The aggregate is responsible for keeping together the manufacturer’s identifying and classification information as one 
consistent whole. This prevents incomplete or ambiguous manufacturer definitions inside the aggregate boundary.


- <span style="color:#70AD47">System-Wide Uniqueness Support</span>

  The uniqueness of `ManufacturerDesignator` is a business rule that depends on the global state of the system. 
Therefore, it is verified through the repository before the aggregate is persisted, ensuring that no duplicate 
manufacturer designators exist.


- <span style="color:#70AD47">Encapsulation of Internal State</span>

  External components do not manipulate `ManufacturerDesignator`, `Country`, or `ManufacturerType` directly. They 
interact only with `Manufacturer`, which preserves the consistency of the aggregate and clearly defines its boundary.