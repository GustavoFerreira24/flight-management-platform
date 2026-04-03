# <span style="color:#E67E22">Aircraft Aggregate</span>

## <span style="color:#2E75B6">Scenario</span>

The Air Transport Company Collaborator registers a new aircraft in the company fleet by providing its registration, 
country, crew size, cabin configuration, and associated aircraft model.
The system must ensure that the aircraft registration is unique and that the cabin configuration does not exceed the 
passenger capacity of the selected aircraft model.


## <span style="color:#2E75B6">Domain Model</span>

![Aircraft DM](../../global-artifacts/02.analysis/svg/Aggregates/AircraftAggregate.svg)

## <span style="color:#2E75B6">Sequence Diagram</span>

![Aircraft SD](../../global-artifacts/03.design/svg/AircraftAggregate-SD.svg)

## <span style="color:#2E75B6">Invariants</span>

- <span style="color:#70AD47">Unique Aircraft Registration</span>

  An Aircraft must have a unique AircraftRegistration in the system.


- <span style="color:#70AD47">Valid Cabin Configuration</span>

    The total number of seats in the CabinConfiguration must not exceed the passenger capacity of the associated 
AircraftModel.


- <span style="color:#70AD47">Valid Initial State</span>

  An Aircraft must always be created with valid registration, country, crew size, and cabin configuration.


## <span style="color:#2E75B6">Justification</span>

- <span style="color:#70AD47">Aggregate Root Responsibility</span>

  `Aircraft` is the Aggregate Root and the single entry point for creating a new aircraft. It encapsulates the creation 
of `AircraftRegistration`, `Country`, `CabinConfiguration`, and `AircraftOperationalStatus`, ensuring that these Value 
Objects cannot exist independently outside the aggregate.


- <span style="color:#70AD47">Internal Consistency Enforcement</span>

  The aggregate guarantees that its internal state is always valid. In this scenario, this includes ensuring that the 
`CabinConfiguration` is consistent with the aircraft’s operational constraints.


- <span style="color:#70AD47">System-Wide Uniqueness Support</span>

  The uniqueness of `AircraftRegistration` is a business rule that depends on the global state of the system. Therefore, 
it is verified through the repository before the aggregate is persisted.


- <span style="color:#70AD47">Association with Another Aggregate Root</span>

  An `Aircraft` must be associated with an existing `AircraftModel`, which is another Aggregate Root in the domain model.  
The model is retrieved through its repository before the aircraft is created, ensuring that the relationship is 
established only with a valid aggregate.


- <span style="color:#70AD47">Business Invariant Control</span>

  The aggregate is responsible for enforcing the rule that the total number of seats in the `CabinConfiguration` must 
not exceed the passenger capacity defined by the associated `AircraftModel`. This ensures that invalid aircraft 
configurations are prevented.


- <span style="color:#70AD47">Encapsulation of Internal State</span>

  External components do not manipulate `AircraftRegistration`, `Country`, `CabinConfiguration`, or 
`AircraftOperationalStatus` directly. They interact only with `Aircraft`, which preserves the consistency of the 
aggregate and clearly defines its boundary.
