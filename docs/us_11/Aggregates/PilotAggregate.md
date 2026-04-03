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

  A `Pilot` can only transition to the active state if the current date is on or before the certification’s `validUntil` date.


## <span style="color:#2E75B6">Justification</span>

- <span style="color:#70AD47">Aggregate Root Responsibility</span>

  `Pilot` is the Aggregate Root and is responsible for controlling the lifecycle and operational status of the pilot.  
  The transition to an active state is only allowed through the root, which ensures that certification requirements are satisfied before activation.


- <span style="color:#70AD47">Business Invariant Control</span>

  This scenario demonstrates that `Pilot` is responsible for enforcing the core business rules associated with 
activation. In particular, it guarantees that a pilot cannot become active if the associated `Certification` is no 
longer valid.


- <span style="color:#70AD47">Temporal Consistency</span>

  The aggregate enforces temporal consistency between the pilot’s operational status and the validity period of the 
associated certification. This ensures that the status of the pilot is always coherent with the pilot’s qualification to 
operate.


- <span style="color:#70AD47">Encapsulation of Internal State</span>

  External components do not manipulate `Certification` or `PilotStatus` directly when deciding activation. They 
interact only with `Pilot`, which preserves the consistency of the aggregate and clearly defines its boundary.