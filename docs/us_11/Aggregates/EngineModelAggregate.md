# <span style="color:#E67E22">Engine Model Aggregate</span>

## <span style="color:#2E75B6">Scenario</span>

The Backoffice Operator registers a new engine model by providing its designator, engine type, power, thrust, fuel type, and TSFC.  
The system must ensure that the engine model is created with valid performance and identification data.

## <span style="color:#2E75B6">Domain Model</span>

![EngineModel DM](../../global-artifacts/02.analysis/svg/Aggregates/EngineModelAggregate.svg)

## <span style="color:#2E75B6">Sequence Diagram</span>

![EngineModel SD](../../global-artifacts/03.design/svg/EngineModelAggregate-SD.svg)

## <span style="color:#2E75B6">Invariants</span>

- <span style="color:#70AD47">Unique Engine Model Designator</span>

  An `EngineModel` must have a unique `EngineModelDesignator` in the system.


- <span style="color:#70AD47">Valid Performance Values</span>

  An `EngineModel` must always have physically valid performance attributes, namely `thrust > 0` and `TSFC > 0`.


- <span style="color:#70AD47">Complete Technical Definition</span>

  An `EngineModel` must always have a defined `EngineType` and `FuelType`.


## <span style="color:#2E75B6">Justification</span>

- <span style="color:#70AD47">Aggregate Root Responsibility</span>

  `EngineModel` is the Aggregate Root and the single entry point for creating a new engine model. It encapsulates the 
creation of `EngineModelDesignator`, `EngineType`, and `FuelType`, ensuring that these Value Objects cannot exist 
independently outside the aggregate.


- <span style="color:#70AD47">Internal Consistency Enforcement</span>

  The aggregate guarantees that its internal state is always valid. In this scenario, this includes ensuring that the 
engine model is created only with physically valid performance values, namely positive thrust and positive TSFC.


- <span style="color:#70AD47">System-Wide Uniqueness Support</span>

  The uniqueness of `EngineModelDesignator` is a business rule that depends on the global state of the system. Therefore, 
it is verified through the repository before the aggregate is persisted, ensuring that no duplicate engine model 
designators exist.


- <span style="color:#70AD47">Complete Technical Definition</span>

  The aggregate is also responsible for ensuring that an engine model is not created with an incomplete technical 
definition. In this scenario, that means the engine model must always include a defined `EngineType` and `FuelType`.


- <span style="color:#70AD47">Encapsulation of Internal State</span>

  External components do not manipulate `EngineModelDesignator`, `EngineType`, or `FuelType` directly. They interact 
only with `EngineModel`, which preserves the consistency of the aggregate and clearly defines its boundary.