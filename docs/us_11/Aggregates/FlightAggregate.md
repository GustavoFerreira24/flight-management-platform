# <span style="color:#E67E22">Flight Aggregate</span>

## <span style="color:#2E75B6">Scenario</span>

The Air Transport Company Collaborator creates a new flight by providing the route, the flight designator, and the departure day and time.  
The system must ensure that the flight is created with a unique designator, a valid departure definition, and an association with an existing flight route.

## <span style="color:#2E75B6">Domain Model</span>

![Flight DM](../../global-artifacts/02.analysis/svg/Aggregates/FlightAggregate.svg)

## <span style="color:#2E75B6">Sequence Diagram</span>

![Flight SD](../../global-artifacts/03.design/svg/FlightAggregate_SD.svg)

## <span style="color:#2E75B6">Invariants</span>

- <span style="color:#70AD47">Unique Flight Designator</span>

  A `Flight` must have a unique `FlightDesignator` in the system.


- <span style="color:#70AD47">Valid Departure Definition</span>

  A `Flight` must always have `Departure`, including both departure day and departure time.


- <span style="color:#70AD47">Association with One Route</span>

  A `Flight` must always be associated with one existing `FlightRoute`.

## <span style="color:#2E75B6">Justification</span>

- <span style="color:#70AD47">Aggregate Root Responsibility</span>

  `Flight` is the Aggregate Root and the single entry point for creating a new flight.  
  It encapsulates the creation of `FlightDesignator` and `Departure`, ensuring that these Value Objects cannot exist 
independently outside the aggregate.


- <span style="color:#70AD47">Internal Consistency Enforcement</span>

  The aggregate guarantees that its internal state is always valid.  
In this scenario, this includes ensuring that the flight is created with a complete departure definition, including both departure day and departure time.


- <span style="color:#70AD47">System-Wide Uniqueness Support</span>

  The uniqueness of `FlightDesignator` is a business rule that depends on the global state of the system.  
Therefore, it is verified through the repository before the aggregate is persisted, ensuring that no duplicate flight designators exist.


- <span style="color:#70AD47">Association with Another Aggregate Root</span>

  A `Flight` exists in the context of a `FlightRoute`, which is another Aggregate Root in the domain model.  
In this scenario, the associated route is obtained through its repository before the flight is created, ensuring that the relationship is established only with an existing aggregate root.


- <span style="color:#70AD47">Encapsulation of Internal State</span>

  External components do not manipulate `FlightDesignator` or `Departure` directly.  
They interact only with `Flight`, which preserves the consistency of the aggregate and clearly defines its boundary.