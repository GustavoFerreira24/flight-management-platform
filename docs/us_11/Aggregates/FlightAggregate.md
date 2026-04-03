# <span style="color:#E67E22">Flight Aggregate</span>

## <span style="color:#2E75B6">Scenario</span>

The Air Transport Company Collaborator creates a new flight by providing the route, the flight designator, and the
departure day and time.  
The system must ensure that the flight is created with a unique designator and a valid departure definition.

## <span style="color:#2E75B6">Domain Model</span>

![Flight DM](../../global-artifacts/02.analysis/svg/Aggregates/FlightAggregate.svg)

## <span style="color:#2E75B6">Sequence Diagram</span>

![Flight SD](../../global-artifacts/03.design/svg/FlightAggregate-SD.svg)

## <span style="color:#2E75B6">Invariants</span>

- <span style="color:#70AD47">Unique Flight Designator</span>

    A `Flight` must have a unique `FlightDesignator` in the system.



- <span style="color:#70AD47">Valid Departure Definition</span>

  A `Flight` must always have one defined `Departure`, including both departure day and departure time.

## <span style="color:#2E75B6">Justification</span>

- <span style="color:#70AD47">Flight Integrity</span>

  The `Flight` acts as the Aggregate Root and is responsible for creating and keeping together the identifying and
  scheduling information of the flight. Since `FlightDesignator` and `Departure` have no independent lifecycle outside the
  flight context, the root ensures that the aggregate is created in a valid and consistent state.


- <span style="color:#70AD47">Business Invariant Control</span>

  This scenario demonstrates that the `Flight` is responsible for enforcing the core business rules of the aggregate,
  especially the uniqueness of the flight designator and the existence of one coherent departure definition. By
  centralizing validation inside the Aggregate Root, invalid or incomplete flight definitions are prevented.


- <span style="color:#70AD47">Encapsulation of Internal State</span>

  External components do not manipulate `FlightDesignator` or `Departure` directly. They interact only with the `Flight`,
  which decides whether the creation operation is valid. This preserves the consistency of the aggregate and makes its
  boundary explicit.