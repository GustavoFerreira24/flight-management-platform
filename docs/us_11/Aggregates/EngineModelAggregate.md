# <span style="color:#E67E22">Engine Model Aggregate</span>

## <span style="color:#2E75B6">Scenario</span>

The Backoffice Operator registers a new engine model by providing its name, type, thrust, fuel type, TSFC, and designator.
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

  An `EngineModel` must always have a defined engine type and fuel type.

## <span style="color:#2E75B6">Justification</span>

- <span style="color:#70AD47">Engine Model Integrity</span>

  The `EngineModel` acts as the Aggregate Root and is responsible for creating and maintaining the technical identity of 
the engine model. Since `EngineModelDesignator` has no independent lifecycle outside the engine context, the root ensures 
that the aggregate is created in a valid and consistent state.

- <span style="color:#70AD47">Business Invariant Control</span>

  This scenario demonstrates that the `EngineModel` is responsible for enforcing the core business rules of the aggregate, 
especially the validity of its technical and performance attributes. By centralizing these checks inside the Aggregate 
Root, invalid engine definitions are prevented.

- <span style="color:#70AD47">Encapsulation of Internal State</span>

  External components do not manipulate the `EngineModelDesignator` directly. They interact only with the `EngineModel`, 
which decides whether the registration is valid. This preserves the consistency of the aggregate and makes its boundary 
explicit.

