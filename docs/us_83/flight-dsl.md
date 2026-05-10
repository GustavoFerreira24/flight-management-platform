# US083 Flight DSL Specification and Validation

## Purpose

US083 defines the Core Flight DSL used to formally describe flight plans. The DSL is implemented with ANTLR 4 and supports lexical validation, syntactic validation, semantic validation, and construction of an internal representation.

## Lexical Specification

Keywords are case-insensitive:

- `flight`
- `leg`
- `departure`
- `arrival`
- `route`
- `segment`
- `fuel`
- `at`
- `regular`
- `charter`
- `altitude`
- `width`
- `wind`
- `deg`

Other lexical elements:

- Flight identifiers: airline-style designators such as `TP123` or generic identifiers.
- Airport codes: IATA or ICAO style uppercase codes with 3 or 4 letters.
- Numeric literals: integer or decimal values, optionally signed.
- Date/time literals: `yyyy-MM-ddTHH:mm` or separated date and time tokens.
- Units: examples include `kg`, `l`, `m`, `ft`, `km`, `nm`, and `m/s`.
- Symbols: `{`, `}`, `(`, `)`, `[`, `]`, `,`, `;`, `->`.
- Whitespace and comments are ignored.

## Syntactic Specification

The DSL is hierarchical and block-based:

```antlr
flightPlanFile : flightBlock EOF;

flightBlock
    : FLIGHT flightIdentifier flightType LBRACE legBlock+ RBRACE;

legBlock
    : LEG LBRACE departureStatement arrivalStatement routeStatement segmentStatement+ fuelStatement RBRACE;

departureStatement : DEPARTURE airportCode AT dateTime SEMI;
arrivalStatement   : ARRIVAL airportCode AT dateTime SEMI;
routeStatement     : ROUTE airportCode ARROW airportCode SEMI;
segmentStatement   : SEGMENT coordinate ARROW coordinate altitudeClause? widthClause? windClause? SEMI;
fuelStatement      : FUEL number unit SEMI;
```

Example:

```text
flight TP123 regular {
  leg {
    departure OPO at 2026-05-10T10:00;
    arrival LIS at 2026-05-10T11:00;
    route OPO -> LIS;
    segment (41.0,-8.0) -> (38.0,-9.0) altitude [1000,2000] m width 10 km wind 90 deg 12 m/s;
    fuel 5000 kg;
  }
}
```

## Semantic Validation

After lexical and syntactic validation, the implementation validates domain coherence:

- Arrival date/time must be after departure date/time within the same leg.
- Route origin must match the leg departure airport.
- Route destination must match the leg arrival airport.
- Consecutive legs must connect: arrival airport of leg `n` must match departure airport of leg `n + 1`.
- Arrival date/time of leg `n` must be before departure date/time of leg `n + 1`.
- The same airport cannot be visited twice in the same flight path.
- Segment start and end coordinates must be different.
- Altitude, width, wind speed, and fuel values must be positive.
- Altitude minimum must be lower than altitude maximum.
- Wind direction must be greater than or equal to `0` and lower than `360` degrees.
- Date/time values must represent valid calendar values.

All semantic errors are collected and returned in a single validation execution.

## ANTLR Processing

The grammar is located at:

```text
aisafe.core/src/main/antlr4/isep/aisafe/flightplan/importer/dsl/FlightPlanDsl.g4
```

Processing pipeline:

- `FlightPlanDslLexer` performs lexical analysis.
- `FlightPlanDslParser` performs syntactic analysis.
- `CollectingFlightPlanDslErrorListener` collects lexical and syntactic errors with line/column information.
- `FlightPlanSemanticValidator` is an ANTLR listener used for semantic validation.
- `FlightPlanAstBuilder` is an ANTLR visitor used to build the internal representation.

The internal representation is exposed through `FlightPlanParseResult.flightPlan()` as `DslFlightPlan` and related `Dsl*` classes.
