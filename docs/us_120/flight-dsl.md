# US120 Flight DSL Specification and Validation

## Purpose

US120 evolves the Flight DSL used to formally describe and validate flight plans. The implementation uses ANTLR 4 for lexical and syntactic analysis, a listener for semantic validation, and a visitor to build internal domain objects.

## Lexical Specification

Keywords are case-insensitive:

- `flight`
- `leg`
- `departure`
- `arrival`
- `route`
- `segment`
- `fuel`
- `aircraft`
- `pilot`
- `load`
- `speed`
- `at`
- `regular`
- `charter`
- `altitude`
- `width`
- `wind`
- `deg`

Other lexical elements:

- Flight identifiers: airline-style designators such as `TP123` or `TP123A`.
- Aircraft registration metadata: identifiers such as `CS-TXA`.
- Pilot metadata: identifiers such as `pilot01`.
- Airport codes: IATA or ICAO style uppercase codes with 3 or 4 letters.
- Numeric literals: integer or decimal values, optionally signed.
- Date/time literals: `yyyy-MM-ddTHH:mm` or separated date and time tokens.
- Units: `kg`, `l`, `m`, `ft`, `km`, `nm`, `m/s`, and `knots`.
- Symbols: `{`, `}`, `(`, `)`, `[`, `]`, `,`, `;`, `->`.
- Whitespace, line comments and block comments are ignored.

## Syntactic Specification

The DSL is hierarchical and block-based. A file may contain one or more flight blocks:

```antlr
flightPlanFile : flightBlock+ EOF;

flightBlock
    : FLIGHT flightIdentifier flightType LBRACE (flightMetadata | loadStatement)* legBlock+ RBRACE;

flightMetadata
    : aircraftStatement
    | pilotStatement;

aircraftStatement : AIRCRAFT aircraftRegistration SEMI;
pilotStatement    : PILOT pilotIdentifier SEMI;
loadStatement     : LOAD number unit? SEMI;

legBlock
    : LEG LBRACE departureStatement arrivalStatement routeStatement segmentStatement+ fuelStatement RBRACE;

departureStatement : DEPARTURE airportCode AT dateTime SEMI;
arrivalStatement   : ARRIVAL airportCode AT dateTime SEMI;
routeStatement     : ROUTE airportCode ARROW airportCode SEMI;
segmentStatement   : SEGMENT coordinate ARROW coordinate speedClause? altitudeClause? widthClause? windClause? SEMI;
fuelStatement      : FUEL number unit SEMI;
speedClause        : SPEED number speedUnit?;
```

Example:

```text
flight TP123 regular {
  aircraft CS-TXA;
  pilot pilot01;
  load 15000 kg;
  leg {
    departure OPO at 2026-05-10T10:00;
    arrival LIS at 2026-05-10T11:00;
    route OPO -> LIS;
    segment (41.0,-8.0) -> (38.0,-9.0) speed 450 knots altitude [1000,2000] m width 10 km wind 90 deg 12 m/s;
    fuel 5000 kg;
  }
}
```

## Semantic Validation

After lexical and syntactic validation, the listener validates domain coherence:

- Flight identifiers must follow the airline designator format, for example `TP123` or `TP123A`.
- Flight identifiers must be unique within the file.
- Each flight may declare at most one aircraft, one pilot and one load.
- Aircraft registration and pilot identifier metadata must follow their accepted identifier formats.
- Arrival date/time must be after departure date/time within the same leg.
- Consecutive legs must connect: arrival airport of leg `n` must match departure airport of leg `n + 1`.
- Arrival date/time of leg `n` must be before departure date/time of leg `n + 1`.
- Route origin must match the leg departure airport.
- Route destination must match the leg arrival airport.
- The same airport cannot be visited twice in the same flight path.
- Segment start and end coordinates must be different.
- Coordinates must be within valid latitude and longitude ranges.
- Altitude, width, wind speed, speed, load and fuel values must be positive.
- Altitude minimum must be lower than altitude maximum.
- Wind direction must be greater than or equal to `0` and lower than `360` degrees.
- Contextual units are validated: altitude `m`/`ft`, width `m`/`km`/`nm`, wind `m/s`, fuel `kg`/`l`, load `kg`, speed `knots`/`m/s`.
- Date/time values must represent valid calendar values.

All semantic errors are collected and reported in a single validation execution.

## Language Extensions

US120 keeps compatibility with the Core Flight DSL and adds three coherent extensions:

- Multiple flight blocks in the same file, enabling file-level validation such as unique flight identifiers.
- `load`, representing people/cargo load at flight level.
- `speed`, representing segment speed and aligning the DSL with the flight profile concepts present in the LPROG sample `Flight_Plan_v0b.json`.

## Implementation

- Grammar: `aisafe.core/src/main/antlr4/isep/aisafe/flightplan/importer/dsl/FlightPlanDsl.g4`.
- Lexical and syntactic error collection: `CollectingFlightPlanDslErrorListener`.
- Semantic listener: `FlightPlanSemanticValidator`.
- AST/domain visitor: `FlightPlanAstBuilder`.
- Result object: `FlightPlanParseResult`, exposing `flightPlans()` and the compatibility method `flightPlan()`.
- CLI: `FlightPlanDslCli`.

## Tests

Automated tests are implemented in:

- `FlightPlanDslSyntaxValidatorTest`.
- `FlightPlanDslFileImportTest`.

Sprint 3 sample file:

```text
aisafe.core/src/test/resources/flightplan/valid/sprint3-multiple-flights.fpl
```

Run from `aisafe.core`:

```bash
mvn test
```

Run the CLI with the Sprint 3 sample:

```bash
mvn compile exec:java -Pflight-dsl -DflightPlan.dsl.file=src/test/resources/flightplan/valid/sprint3-multiple-flights.fpl
```
