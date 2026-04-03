# <span style="color:#E67E22">Pilot Aggregate</span>

## <span style="color:#2E75B6">Scenario</span>

The Air Transport Company Collaborator activates a pilot by providing the pilot identifier.
The system must ensure that the pilot can only be activated if their certification is still valid.

## <span style="color:#2E75B6">Domain Model</span>

![Pilot DM](../../global-artifacts/02.analysis/svg/Aggregates/PilotAggregate.svg)

## <span style="color:#2E75B6">Sequence Diagram</span>

![Pilot SD](../../global-artifacts/03.design/svg/PilotAggregate-SD.svg)

## <span style="color:#2E75B6">Invariants</span>

- <span style="color:#70AD47">Valid Certification Period</span>

  A `Certification` must always have a valid time interval where `issuedAt < validUntil`.


- <span style="color:#70AD47">Activation Requires Valid Certification</span>

  A `Pilot` can only transition to the active state if the current date is before the certification’s validUntil date.


## <span style="color:#2E75B6">Justification</span>

- <span style="color:#70AD47">Lifecycle Integrity</span>

  The Pilot acts as the Aggregate Root and is responsible for controlling the lifecycle of the pilot. The root ensures 
that the pilot’s operational status is always consistent with their certification validity.


- <span style="color:#70AD47">Business Invariant Control</span>

  This scenario demonstrates that the `Pilot` is responsible for enforcing the core business rules of the aggregate, 
especially the temporal relationship between certification validity and activation. By centralizing this validation 
inside the Aggregate Root, invalid pilot activations are prevented.


- <span style="color:#70AD47">Encapsulation of Internal State</span>

  External components do not manipulate the `Certification` directly when deciding activation. They interact only with the 
`Pilot`, which determines whether the operation is valid. This preserves the consistency of the aggregate and makes its 
boundary explicit.
