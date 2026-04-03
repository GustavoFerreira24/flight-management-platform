# <span style="color:#E67E22">User Aggregate</span>

## <span style="color:#2E75B6">Scenario</span>

The Administrator activates a user by providing the user identifier.
The system must ensure that the user can only be activated if their security clearance is still valid and their skills 
assessment is up to date.

## <span style="color:#2E75B6">Domain Model</span>

![Simulation DM](../../global-artifacts/02.analysis/svg/Aggregates/UserAggregate.svg)

## <span style="color:#2E75B6">Sequence Diagram</span>

![Simulation SD](../../global-artifacts/03.design/svg/UserAggregate-SD.svg)

## <span style="color:#2E75B6">Invariants</span>

- <span style="color:#70AD47">Valid Security Clearance</span>

  A User can only be active if the current date is on or before the expiryDate of the SecurityClearance.


- <span style="color:#70AD47">Up-to-Date Skills Assessment</span>

  A User can only be active if the current date is on or before the nextDueDate of the SkillsAssessment.

## <span style="color:#2E75B6">Justification</span>

- <span style="color:#70AD47">Lifecycle Integrity</span>

  The User acts as the Aggregate Root and is responsible for controlling the operational status of the user. The root 
ensures that activation is only possible when the user meets the required security and qualification conditions.


- <span style="color:#70AD47">Business Invariant Control</span>

  This scenario demonstrates that the User is responsible for enforcing the core business rules of the aggregate, 
especially the temporal relationship between activation, security clearance validity, and skills assessment validity. 
By centralizing this validation inside the Aggregate Root, invalid user activations are prevented.


- <span style="color:#70AD47">Encapsulation of Internal State</span>

  External components do not manipulate SecurityClearance or SkillsAssessment directly when deciding activation. They 
interact only with the User, which determines whether the operation is valid. This preserves the consistency of the 
aggregate and makes its boundary explicit.

