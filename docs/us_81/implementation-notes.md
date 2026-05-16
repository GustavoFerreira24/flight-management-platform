# US081 Flight Plan File Import Implementation Notes

## Scope

US081 validates flight plan files written in the Core Flight DSL and transforms accepted files into the internal `DslFlightPlan` representation.

## Implementation

- Grammar: `aisafe.core/src/main/antlr4/isep/aisafe/flightplan/importer/dsl/FlightPlanDsl.g4`.
- Import entry point: `FlightPlanDslSyntaxValidator.validate(Path)` reads a flight plan file and runs the same validation pipeline as string input.
- Lexical and syntactic errors are collected by `CollectingFlightPlanDslErrorListener` with line and column information.
- Semantic validation is performed by `FlightPlanSemanticValidator` only when lexical and syntactic validation succeeds.
- Internal representation is built by `FlightPlanAstBuilder` only after all validation stages pass.
- Invalid files return `FlightPlanParseResult.failure(...)`, with `parseTree()` and `flightPlan()` set to `null`.
- Application use case: `ImportFlightPlanFromFileController.importFromFile(Path)` authorizes flight plan management, validates the file, rejects invalid input and stores only accepted imports.
- Imported representation: `ImportedFlightPlan` keeps the accepted `DslFlightPlan`, source file path, import timestamp and summary fields used by the application layer.
- Temporary persistence: `ImportedFlightPlanRepository` and `InMemoryImportedFlightPlanRepository` are used until the complete persisted `FlightPlan` aggregate/repository is introduced.

## Validation Coverage

Automated tests are implemented in:

- `FlightPlanDslSyntaxValidatorTest`, covering valid DSL input, comments, keyword case-insensitivity, lexical errors, syntactic errors, semantic errors, and metadata validation.
- `FlightPlanDslFileImportTest`, covering file-based import through `Path`, invalid file rejection, error classification, and transformation into `DslFlightPlan`, `DslFlightLeg`, `DslSegment`, `DslQuantity`, `DslAltitudeSlot`, and `DslWind`.
- `ImportFlightPlanFromFileControllerTest`, covering the US081 application flow: valid files are imported and stored, invalid files are rejected and not stored, missing files are rejected, and authorization is enforced.

## Acceptance Notes

- Valid flight plan files are accepted and expose a parse tree plus internal representation.
- Lexical, syntactic, and semantic errors reject the file.
- Rejected files do not expose partial imported data.
- Rejected files are not stored by the application use case.
- Accepted files are stored as `ImportedFlightPlan` records, preserving the parsed DSL representation for later conversion into the full flight-plan domain model.
- Date/time values are converted to `LocalDateTime`.
- Flight type is normalized to uppercase in `DslFlightPlan`.
- Numeric quantities, coordinates, route endpoints, altitude slots, width, wind, and fuel are preserved in the internal representation.

## Current Integration Boundary

US081 is integrated up to the application layer. The controller imports and stores accepted DSL flight plans, but it does not yet create a final persisted domain `FlightPlan` aggregate because the complete flight plan persistence model is not available in the Java application.

The SCOMP simulator is not directly integrated with this use case. If needed in a later user story, the accepted `DslFlightPlan` can be exported to the CSV format consumed by the C simulator.

## Running Tests

From the repository root:

```bash
mvn -pl aisafe.core test
```
