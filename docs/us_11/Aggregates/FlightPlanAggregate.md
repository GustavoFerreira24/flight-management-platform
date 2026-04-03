# <span style="color:#E67E22">FlightPlan Aggregate</span>

## <span style="color:#2E75B6">Scenario</span>

The Pilot creates a new flight plan by selecting the flight, the aircraft, the scheduled arrival, and the fuel quantity.
The system must ensure that the flight plan is created with a valid initial status and a strictly positive fuel quantity.

## <span style="color:#2E75B6">Domain Model</span>

![FlightPlan DM](../../global-artifacts/02.analysis/svg/Aggregates/FlightPlanAggregate.svg)

## <span style="color:#2E75B6">Sequence Diagram</span>

![FlightPlan SD](../../global-artifacts/03.design/svg/FlightPlanAggregate-SD.svg)

## <span style="color:#2E75B6">Invariants</span>

- <span style="color:#70AD47">Positive Fuel Quantity</span>

  A `FlightPlan` must always contain a `FuelQty` with a strictly positive amount.


- <span style="color:#70AD47">Valid Performance Values</span>

  A `FlightPlan` must be created with a status consistent with the beginning of its lifecycle.


## <span style="color:#2E75B6">Justification</span>

- <span style="color:#70AD47">Flight Plan Integrity</span>

  The `FlightPlan` acts as the Aggregate Root and is responsible for creating and maintaining the state of the plan. Since
  `FuelQty` has no independent lifecycle outside the flight plan context, the root ensures that the aggregate is created in 
a valid and consistent state.


- <span style="color:#70AD47">Business Invariant Control</span>

  This scenario demonstrates that the `FlightPlan` is responsible for enforcing the core business rules of the aggregate, 
especially the validity of fuel quantity and the correctness of the initial lifecycle state. By centralizing these checks 
inside the Aggregate Root, invalid flight plans are prevented.


- <span style="color:#70AD47">Encapsulation of Internal State</span>

  External components do not manipulate `FuelQty` directly. They interact only with the `FlightPlan`, which decides whether 
the creation is valid. This preserves the consistency of the aggregate and makes its boundary explicit.