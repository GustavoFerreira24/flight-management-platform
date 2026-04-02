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

- <span style="color:#70AD47">Aircraft Consistency</span>

  The Aircraft acts as the Aggregate Root and is responsible for creating and keeping together its identifying and 
operational information. Since AircraftRegistration, Country, and CabinConfiguration have no independent meaning 
outside the aircraft context, the root ensures that the aggregate is always created in a valid state.

- <span style="color:#70AD47">Business Invariant Control</span>

  This scenario demonstrates that the Aircraft is responsible for enforcing the main business rules of the aggregate, 
especially the relation between cabin configuration and the capacity of the selected aircraft model. By centralizing 
validation inside the Aggregate Root, invalid aircraft configurations are prevented.

- <span style="color:#70AD47">Encapsulation of Internal State</span>

  External components do not manipulate AircraftRegistration, Country, or CabinConfiguration directly. They interact 
only with the Aircraft, which decides whether the operation is valid. This preserves the internal consistency of the 
aggregate and makes its boundary explicit.
