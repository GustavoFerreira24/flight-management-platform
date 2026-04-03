# <span style="color:#E67E22">Flight Route Aggregate</span>

## <span style="color:#2E75B6">Scenario</span>

The Air Transport Company Collaborator deactivates an existing flight route by providing its identifier and the date 
from which it should become inactive.
The system must ensure that the route can only be deactivated from a valid future date and that the state transition is 
consistent.

## <span style="color:#2E75B6">Domain Model</span>

![FlightRoute DM](../../global-artifacts/02.analysis/svg/Aggregates/FlightRouteAggregate.svg)

## <span style="color:#2E75B6">Sequence Diagram</span>

![FlightRoute SD](../../global-artifacts/03.design/svg/FlightRouteAggregate-SD.svg)

## <span style="color:#2E75B6">Invariants</span>

- <span style="color:#70AD47">Valid Deactivation Date</span>

  A FlightRoute can only be deactivated with a date that is valid and in the future.


- <span style="color:#70AD47">Consistent Lifecycle State</span>

  A FlightRoute can only transition to the inactive state if it is currently active.


- <span style="color:#70AD47">Consistent Deactivation Information</span>

  When a FlightRoute becomes inactive, the deactivatedFrom date must be defined consistently with that state.

## <span style="color:#2E75B6">Justification</span>

- <span style="color:#70AD47">Lifecycle Integrity</span>

  The FlightRoute acts as the Aggregate Root and is responsible for controlling the lifecycle of the route. Since the 
route’s status and deactivation date must remain coherent, the root ensures that the aggregate only transitions to an 
inactive state under valid conditions.


- <span style="color:#70AD47">Business Invariant Control</span>

  This scenario demonstrates that the FlightRoute is responsible for enforcing the core business rules of the aggregate, 
especially those related to state transition and deactivation timing. By centralizing this validation inside the 
Aggregate Root, inconsistent route states are prevented.


- <span style="color:#70AD47">Encapsulation of Internal State</span>

  External components do not change the route’s status or deactivation date directly. They interact only with the 
FlightRoute, which decides whether the deactivation operation is valid. This preserves the consistency of the aggregate 
and makes its boundary explicit.

