# <span style="color:#E67E22">FlightPlan Aggregate</span>

## <span style="color:#2E75B6">Scenario</span>

The Pilot creates a new flight plan by selecting the flight, the aircraft, the pilot, the planned departure, the scheduled arrival, and the fuel quantity.  
The system must ensure that the flight plan is created with a valid initial status, a strictly positive fuel quantity, and valid associations with the required aggregates.

## <span style="color:#2E75B6">Domain Model</span>

![FlightPlan DM](../../global-artifacts/02.analysis/svg/Aggregates/FlightPlanAggregate.svg)

## <span style="color:#2E75B6">Sequence Diagram</span>

![FlightPlan SD](../../global-artifacts/03.design/svg/FlightPlanAggregate-SD.svg)

## <span style="color:#2E75B6">Invariants</span>

- <span style="color:#70AD47">Positive Fuel Quantity</span>

  A `FlightPlan` must always contain a `FuelQty` with a strictly positive amount.


- <span style="color:#70AD47">Valid Initial Status</span>

  A `FlightPlan` must be created with a status consistent with the beginning of its lifecycle.


- <span style="color:#70AD47">Association with Required Aggregates</span>

  A `FlightPlan` must always be associated with one existing `Flight`, one existing `Aircraft`, and one existing `Pilot`.

## <span style="color:#2E75B6">Justification</span>

- <span style="color:#70AD47">Aggregate Root Responsibility</span>

  `FlightPlan` is the Aggregate Root and the single entry point for creating a new flight plan.  
  It encapsulates the creation of `FlightPlanStatus` and `FuelQty`, ensuring that these internal objects cannot exist independently outside the aggregate.


- <span style="color:#70AD47">Internal Consistency Enforcement</span>

  The aggregate guarantees that its internal state is always valid.  
  In this scenario, this includes ensuring that the fuel quantity is strictly positive and that the flight plan starts with a valid initial status.


- <span style="color:#70AD47">Association with Other Aggregate Roots</span>

  A `FlightPlan` exists in relation to other aggregates in the domain model, namely `Flight`, `Aircraft`, and `Pilot`.  
  In this scenario, these aggregates are obtained through their repositories before the flight plan is created, ensuring that the plan is only created with valid existing references.


- <span style="color:#70AD47">Business Invariant Control</span>

  This scenario demonstrates that `FlightPlan` is responsible for enforcing the core business rules associated with creation.  
  In particular, it prevents invalid flight plans by centralizing fuel and lifecycle-state validation inside the Aggregate Root.


- <span style="color:#70AD47">Encapsulation of Internal State</span>

  External components do not manipulate `FuelQty` or `FlightPlanStatus` directly.  
  They interact only with `FlightPlan`, which preserves the consistency of the aggregate and clearly defines its boundary.