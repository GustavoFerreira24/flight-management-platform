# <span style="color:#E67E22">Aircraft Model Aggregate</span>

## <span style="color:#2E75B6">Scenario</span>

The Backoffice Operator adds a certified engine to an existing aircraft model (e.g., Boeing 737), creating a new 
`AircraftVariant`.
The system must ensure that the same engine model is not added more than once and that the engine is technically 
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

- <span style="color:#70AD47">Certified Variant Integrity</span>

   The `AircraftModel` acts as the Aggregate Root and is responsible for managing the lifecycle of its `AircraftVariants`. 
Since variants have no independent meaning outside the context of the aircraft model, the aggregate ensures that all 
changes preserve a valid certified configuration.


- <span style="color:#70AD47">Business Invariant Control</span>

    This scenario demonstrates that the `AircraftModel` is the Information Expert regarding certified engines. By centralizing 
validation logic such as duplicate prevention and engine compatibility inside the Aggregate Root, invalid or inconsistent 
aircraft–engine combinations are avoided.

- <span style="color:#70AD47">Encapsulation of Internal State</span>

  External components, such as controllers, do not manipulate `AircraftVariant` objects directly. All modifications are 
performed through the `AircraftModel`, which determines whether the operation is valid. This protects the internal state 
of the aggregate and ensures consistency within its boundary.