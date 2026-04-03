# <span style="color:#E67E22">User Aggregate</span>

## <span style="color:#2E75B6">Scenario</span>

The Administrator activates a user by providing the user identifier.
The system must ensure that the user can only be activated if their security clearance is still valid and their skills 
assessment is up to date.

## <span style="color:#2E75B6">Domain Model</span>

![User DM](../../global-artifacts/02.analysis/svg/Aggregates/UserAggregate.svg)

## <span style="color:#2E75B6">Sequence Diagram</span>

![User SD](../../global-artifacts/03.design/svg/UserAggregate-SD.svg)

## <span style="color:#2E75B6">Invariants</span>

- <span style="color:#70AD47">Valid Security Clearance</span>

  A `User` can only be active if the current date is on or before the `expiryDate` of the `SecurityClearance`.


- <span style="color:#70AD47">Up-to-Date Skills Assessment</span>

  A `User` can only be active if the current date is on or before the `nextDueDate` of the `SkillsAssessment`.


## <span style="color:#2E75B6">Justification</span>

- <span style="color:#70AD47">Aggregate Root Responsibility</span>

  `User` is the Aggregate Root and is responsible for controlling the lifecycle and operational status of the user.  
  The transition to an active state is only allowed through the root, which ensures that the user satisfies the required 
conditions before activation.


- <span style="color:#70AD47">Business Invariant Control</span>

  This scenario demonstrates that `User` is responsible for enforcing the business rules associated with activation.  
  In particular, it guarantees that a user cannot become active if the `SecurityClearance` is expired or if the 
`SkillsAssessment` is overdue.


- <span style="color:#70AD47">Temporal Consistency</span>

  The aggregate enforces temporal consistency between the current user status and the validity periods of the associated 
qualification information. This ensures that the user status is always coherent with the user’s security and regulatory 
conditions.


- <span style="color:#70AD47">Encapsulation of Internal State</span>

  External components do not manipulate `SecurityClearance`, `SkillsAssessment`, or `UserStatus` directly when deciding 
activation. They interact only with `User`, which preserves the consistency of the aggregate and clearly defines its 
boundary.