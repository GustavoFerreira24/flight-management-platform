# <span style="color:#E67E22">Aircraft Model Aggregate</span>

## <span style="color:#2E75B6">Scenario</span>

The Backoffice Operator adds a certified engine model to an existing aircraft model (e.g., Boeing 737), creating a new 
`AircraftVariant`.  
The system must ensure that the same engine model is not added more than once and that the engine model is technically 
compatible with the aircraft model.

## <span style="color:#2E75B6">Domain Model</span>

![AircraftModel DM](../../global-artifacts/02.analysis/svg/Aggregates/AircraftModelAggregate.svg)

## <span style="color:#2E75B6">Sequence Diagram</span>

![AircraftModel SD](../../global-artifacts/03.design/svg/AircraftModelAggregate-SD.svg)

## <span style="color:#2E75B6">Invariants</span>

- <span style="color:#70AD47">Minimum Certified Configuration</span>
  
    An `AircraftModel` must always contain at least one certified `AircraftVariant`.


- <span style="color:#70AD47">No Duplication</span>
  
    The same `EngineModel` cannot be associated more than once with the same `AircraftModel`.


- <span style="color:#70AD47">Engine Compatibility</span>
  
    The `EngineModel` associated with an `AircraftVariant` must be compatible with the motorization type defined in the `AircraftModel`.


## <span style="color:#2E75B6">Justification</span>

- <span style="color:#70AD47">Aggregate Root Responsibility</span>

  `AircraftModel` is the Aggregate Root and is responsible for managing the lifecycle of its internal `AircraftVariant` 
entities. Since variants represent certified configurations of the aircraft model, they cannot exist independently 
outside this aggregate.


- <span style="color:#70AD47">Business Invariant Control</span>

  This scenario demonstrates that `AircraftModel` is responsible for enforcing the main business rules associated with 
certified variants. In particular, it prevents duplicate associations with the same `EngineModel` and ensures that only 
technically compatible engine models can be added.


- <span style="color:#70AD47">Association with Another Aggregate Root</span>

  The `EngineModel` is another Aggregate Root in the domain model. In this scenario, it is obtained through its 
repository before being associated with the `AircraftModel`. The `AircraftVariant` is then created based on this 
existing `EngineModel`, ensuring that no variant exists without a valid engine configuration.


- <span style="color:#70AD47">Encapsulation of Internal State</span>

  External components do not manipulate `AircraftVariant` objects directly. All modifications are performed through 
`AircraftModel`, which preserves the consistency of the aggregate and clearly defines its boundary.


- <span style="color:#70AD47">Aggregate Consistency Over Time</span>

  The aggregate must always retain at least one certified `AircraftVariant`. Although this invariant is especially 
relevant in removal scenarios, it justifies why variants are managed within the `AircraftModel` aggregate boundary.