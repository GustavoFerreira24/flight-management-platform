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

## Validation Coverage

Automated tests are implemented in:

- `FlightPlanDslSyntaxValidatorTest`, covering valid DSL input, comments, keyword case-insensitivity, lexical errors, syntactic errors, semantic errors, and metadata validation.
- `FlightPlanDslFileImportTest`, covering file-based import through `Path`, invalid file rejection, error classification, and transformation into `DslFlightPlan`, `DslFlightLeg`, `DslSegment`, `DslQuantity`, `DslAltitudeSlot`, and `DslWind`.

## Acceptance Notes

- Valid flight plan files are accepted and expose a parse tree plus internal representation.
- Lexical, syntactic, and semantic errors reject the file.
- Rejected files do not expose partial imported data.
- Date/time values are converted to `LocalDateTime`.
- Flight type is normalized to uppercase in `DslFlightPlan`.
- Numeric quantities, coordinates, route endpoints, altitude slots, width, wind, and fuel are preserved in the internal representation.

## Running Tests

From the repository root:

```bash
mvn -pl aisafe.core test
```
