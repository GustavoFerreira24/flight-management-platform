# US121 Create a Flight Plan from a File

## Scope

US121 imports flight plans from files written in the Flight DSL specified and validated by US120.

The use case does not define a new grammar. It uses the US120 validation pipeline and only accepts files that pass lexical, syntactic and semantic validation.

## Implementation

- Validation entry point: `FlightPlanDslSyntaxValidator.validate(Path)`.
- Application use case: `ImportFlightPlanFromFileController.importFromFile(Path)`.
- Import result: `ImportFlightPlanResult`.
- Imported representation: `ImportedFlightPlan`.
- Temporary persistence: `ImportedFlightPlanRepository` with `InMemoryImportedFlightPlanRepository`.

## Behaviour

- The file must exist and be readable.
- The file is validated according to the Flight DSL pipeline from US120.
- Lexical, syntactic and semantic errors are returned as `FlightPlanDslError` values with type, line, column and message.
- If the file is invalid, no flight plan from that file is stored.
- If the file is valid, every `flight` block parsed from the file is stored as an `ImportedFlightPlan`.
- Multi-flight files are supported because US120 accepts `flightBlock+` and exposes all parsed plans through `FlightPlanParseResult.flightPlans()`.

## Acceptance Notes

- Valid single-flight files are imported and stored.
- Valid multi-flight files are imported and all contained flight plans are stored.
- Invalid files are rejected and do not produce partial imports.
- The first imported plan remains available through `ImportFlightPlanResult.importedFlightPlan()` for simple single-flight use cases.
- All imported plans are available through `ImportFlightPlanResult.importedFlightPlans()`.

## Tests

Automated coverage is provided by:

- `FlightPlanDslFileImportTest`, validating file-level DSL acceptance and rejection.
- `ImportFlightPlanFromFileControllerTest`, validating the US121 application flow, including single-flight import, multi-flight import, invalid-file rejection, missing-file rejection and authorization.

## Current Integration Boundary

US121 imports and stores accepted DSL flight plans as `ImportedFlightPlan` records. It does not execute flight simulation, export data to SCOMP, or create a final persisted domain `FlightPlan` aggregate because those concerns belong to other user stories.

## Running Tests

From the repository root:

```bash
mvn -pl aisafe.infrastructure.application -am test
```

Manual DSL validation can be executed with:

```bash
mvn -pl aisafe.core -Pflight-dsl -DflightPlan.dsl.file="docs/us_83/valid-flight-plan.fpl"
```
