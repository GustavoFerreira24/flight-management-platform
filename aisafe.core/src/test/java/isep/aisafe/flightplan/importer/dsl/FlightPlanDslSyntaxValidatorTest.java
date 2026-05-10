package isep.aisafe.flightplan.importer.dsl;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlightPlanDslSyntaxValidatorTest {

    private final FlightPlanDslSyntaxValidator validator = new FlightPlanDslSyntaxValidator();

    @Test
    void ensureValidFlightPlanProducesInternalRepresentation() {
        // Arrange
        final String source = """
                flight TP123 regular {
                  leg {
                    departure OPO at 2026-05-10T10:00;
                    arrival LIS at 2026-05-10T11:00;
                    route OPO -> LIS;
                    segment (41.0,-8.0) -> (38.0,-9.0) altitude [1000,2000] m width 10 km wind 90 deg 12 m/s;
                    fuel 5000 kg;
                  }
                }
                """;

        // Act
        final FlightPlanParseResult result = validator.validate(source);

        // Assert
        assertTrue(result.isValid());
        assertNotNull(result.parseTree());
        assertNotNull(result.flightPlan());
        assertEquals("TP123", result.flightPlan().identifier());
        assertEquals("REGULAR", result.flightPlan().type());
        assertEquals(1, result.flightPlan().legs().size());
        assertEquals("OPO", result.flightPlan().legs().getFirst().departure().airportCode());
        assertEquals("LIS", result.flightPlan().legs().getFirst().arrival().airportCode());
    }

    @Test
    void ensureSyntaxErrorsAreReportedWithLocation() {
        // Arrange
        final String source = """
                flight TP123 regular {
                  leg {
                    departure OPO at 2026-05-10T10:00
                    arrival LIS at 2026-05-10T11:00;
                    route OPO -> LIS;
                    segment (41.0,-8.0) -> (38.0,-9.0);
                    fuel 5000 kg;
                  }
                }
                """;

        // Act
        final FlightPlanParseResult result = validator.validate(source);

        // Assert
        assertFalse(result.isValid());
        assertEquals(FlightPlanDslErrorType.SYNTACTIC, result.errors().getFirst().type());
        assertTrue(result.errors().getFirst().line() > 0);
        assertTrue(result.errors().getFirst().column() >= 0);
    }

    @Test
    void ensureSemanticErrorsAreCollectedInSingleRun() {
        // Arrange
        final String source = """
                flight TP123 regular {
                  leg {
                    departure OPO at 2026-05-10T10:00;
                    arrival LIS at 2026-05-10T09:00;
                    route LIS -> OPO;
                    segment (41.0,-8.0) -> (41.0,-8.0) altitude [-100,50] m width 0 km wind 360 deg -1 m/s;
                    fuel -1 kg;
                  }
                }
                """;

        // Act
        final FlightPlanParseResult result = validator.validate(source);

        // Assert
        assertFalse(result.isValid());
        assertTrue(result.errors().size() >= 6);
        assertEquals(List.of(FlightPlanDslErrorType.SEMANTIC), result.errors().stream().map(FlightPlanDslError::type).distinct().toList());
        assertTrue(result.errors().stream().anyMatch(error -> error.message().contains("Fuel quantity")));
        assertTrue(result.errors().stream().anyMatch(error -> error.message().contains("Segment start and end")));
        assertTrue(result.errors().stream().anyMatch(error -> error.message().contains("Route origin")));
    }

    @Test
    void ensureLegSequenceCoherenceIsValidated() {
        // Arrange
        final String source = """
                flight TP123 regular {
                  leg {
                    departure OPO at 2026-05-10T10:00;
                    arrival LIS at 2026-05-10T11:00;
                    route OPO -> LIS;
                    segment (41.0,-8.0) -> (38.0,-9.0);
                    fuel 5000 kg;
                  }
                  leg {
                    departure MAD at 2026-05-10T10:30;
                    arrival CDG at 2026-05-10T12:30;
                    route MAD -> CDG;
                    segment (40.0,-3.0) -> (49.0,2.0);
                    fuel 5000 kg;
                  }
                }
                """;

        // Act
        final FlightPlanParseResult result = validator.validate(source);

        // Assert
        assertFalse(result.isValid());
        assertTrue(result.errors().stream().anyMatch(error -> error.message().contains("following leg")));
    }
}
