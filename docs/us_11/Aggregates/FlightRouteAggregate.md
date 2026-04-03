# <span style="color:#E67E22">Flight Route Aggregate</span>

## <span style="color:#2E75B6">Scenario</span>

The Air Transport Company Collaborator deactivates an existing flight route by providing its identifier and the date from which it should become inactive.  
The system must ensure that the route can only be deactivated from a valid future date and that the state transition is consistent.

## <span style="color:#2E75B6">Domain Model</span>

![FlightRoute DM](../../global-artifacts/02.analysis/svg/Aggregates/FlightRouteAggregate.svg)

## <span style="color:#2E75B6">Sequence Diagram</span>

![FlightRoute SD](../../global-artifacts/03.design/svg/FlightRouteAggregate-SD.svg)

## <span style="color:#2E75B6">Invariants</span>

- <span style="color:#70AD47">Valid Deactivation Date</span>

  A `FlightRoute` can only be deactivated with a date that is valid and in the future.


- <span style="color:#70AD47">Consistent Lifecycle State</span>

  A `FlightRoute` can only transition to the inactive state if it is currently active.


- <span style="color:#70AD47">Consistent Deactivation Information</span>

  When a `FlightRoute` becomes inactive, the `deactivatedFrom` date must be defined consistently with that state.

## <span style="color:#2E75B6">Justification</span>

- <span style="color:#70AD47">Aggregate Root Responsibility</span>

  `FlightRoute` is the Aggregate Root and is responsible for controlling the lifecycle of the route.  
  The transition to an inactive state is only allowed through the root, which ensures that the route status and deactivation date remain coherent.


- <span style="color:#70AD47">Business Invariant Control</span>

  This scenario demonstrates that `FlightRoute` is responsible for enforcing the core business rules associated with deactivation.  
  In particular, it guarantees that a route cannot be deactivated from an invalid date and cannot transition to an inactive state if it is not currently active.


- <span style="color:#70AD47">Temporal Consistency</span>

  The aggregate enforces temporal consistency between the route status and the deactivation date.  
  This ensures that deactivation information is always coherent with the lifecycle state of the route.


- <span style="color:#70AD47">Encapsulation of Internal State</span>

  External components do not change the route’s status or deactivation date directly.  
  They interact only with `FlightRoute`, which preserves the consistency of the aggregate and clearly defines its boundary.