package isep.aisafe.flightplan.importer.dsl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlightPlanDslFileImportTest {

    private final FlightPlanDslSyntaxValidator validator = new FlightPlanDslSyntaxValidator();

    @TempDir
    private Path temporaryDirectory;

    @Test
    void ensureValidFlightPlanFileImportProducesInternalRepresentation() throws IOException {
        // Arrange
        final Path file = writeFlightPlan("valid-flight-plan.fpl", """
                flight TP123 charter {
                  aircraft CS-TXA;
                  pilot pilot01;
                  leg {
                    departure OPO at 2026-05-10 10:00;
                    arrival LIS at 2026-05-10T11:00;
                    route OPO -> LIS;
                    segment (41.1579,-8.6291) -> (38.7742,-9.1342) altitude [1000,2000] m width 10 km wind 90 deg 12 m/s;
                    fuel 5000 kg;
                  }
                }
                """);

        // Act
        final FlightPlanParseResult result = validator.validate(file);

        // Assert
        assertTrue(result.isValid());
        assertNotNull(result.parseTree());
        assertNotNull(result.flightPlan());

        final DslFlightPlan flightPlan = result.flightPlan();
        assertEquals("TP123", flightPlan.identifier());
        assertEquals("CHARTER", flightPlan.type());
        assertEquals("CS-TXA", flightPlan.aircraftRegistration().orElseThrow());
        assertEquals("pilot01", flightPlan.pilotIdentifier().orElseThrow());
        assertEquals(1, flightPlan.legs().size());

        final DslFlightLeg leg = flightPlan.legs().getFirst();
        assertEquals("OPO", leg.departure().airportCode());
        assertEquals(LocalDateTime.of(2026, 5, 10, 10, 0), leg.departure().dateTime());
        assertEquals("LIS", leg.arrival().airportCode());
        assertEquals(LocalDateTime.of(2026, 5, 10, 11, 0), leg.arrival().dateTime());
        assertEquals("OPO", leg.route().originAirportCode());
        assertEquals("LIS", leg.route().destinationAirportCode());
        assertEquals(5000.0, leg.fuel().value());
        assertEquals("kg", leg.fuel().unit());

        final DslSegment segment = leg.segments().getFirst();
        assertEquals(41.1579, segment.start().latitude());
        assertEquals(-8.6291, segment.start().longitude());
        assertEquals(38.7742, segment.end().latitude());
        assertEquals(-9.1342, segment.end().longitude());
        assertEquals(1000.0, segment.altitudeSlot().orElseThrow().minimum());
        assertEquals(2000.0, segment.altitudeSlot().orElseThrow().maximum());
        assertEquals("m", segment.altitudeSlot().orElseThrow().unit());
        assertEquals(10.0, segment.width().orElseThrow().value());
        assertEquals("km", segment.width().orElseThrow().unit());
        assertEquals(90.0, segment.wind().orElseThrow().directionDegrees());
        assertEquals(12.0, segment.wind().orElseThrow().speed());
        assertEquals("m/s", segment.wind().orElseThrow().unit());
    }

    @Test
    void ensureInvalidFlightPlanFilesAreNotAccepted() throws IOException {
        // Arrange
        final List<Path> invalidFiles = List.of(
                writeFlightPlan("lexical-error.fpl", """
                        flight TP123 regular {
                          leg {
                            departure OPO at 2026-05-10T10:00;
                            arrival LIS at 2026-05-10T11:00;
                            route OPO -> LIS;
                            segment (41.0,-8.0) -> (38.0,-9.0) @;
                            fuel 5000 kg;
                          }
                        }
                        """),
                writeFlightPlan("syntactic-error.fpl", """
                        flight TP123 regular {
                          leg {
                            departure OPO at 2026-05-10T10:00
                            arrival LIS at 2026-05-10T11:00;
                            route OPO -> LIS;
                            segment (41.0,-8.0) -> (38.0,-9.0);
                            fuel 5000 kg;
                          }
                        }
                        """),
                writeFlightPlan("semantic-error.fpl", """
                        flight TP123 regular {
                          leg {
                            departure OPO at 2026-05-10T10:00;
                            arrival LIS at 2026-05-10T09:00;
                            route OPO -> LIS;
                            segment (41.0,-8.0) -> (38.0,-9.0);
                            fuel 5000 kg;
                          }
                        }
                        """)
        );

        // Act and assert
        for (Path invalidFile : invalidFiles) {
            final FlightPlanParseResult result = validator.validate(invalidFile);

            assertFalse(result.isValid());
            assertFalse(result.errors().isEmpty());
            assertNull(result.parseTree());
            assertNull(result.flightPlan());
        }
    }

    @Test
    void ensureFileImportReportsLexicalSyntacticAndSemanticErrors() throws IOException {
        // Arrange
        final Path lexicalErrorFile = writeFlightPlan("unexpected-character.fpl", """
                flight TP123 regular {
                  leg {
                    departure OPO at 2026-05-10T10:00;
                    arrival LIS at 2026-05-10T11:00;
                    route OPO -> LIS;
                    segment (41.0,-8.0) -> (38.0,-9.0) #;
                    fuel 5000 kg;
                  }
                }
                """);
        final Path syntacticErrorFile = writeFlightPlan("missing-fuel-unit.fpl", """
                flight TP123 regular {
                  leg {
                    departure OPO at 2026-05-10T10:00;
                    arrival LIS at 2026-05-10T11:00;
                    route OPO -> LIS;
                    segment (41.0,-8.0) -> (38.0,-9.0);
                    fuel 5000;
                  }
                }
                """);
        final Path semanticErrorFile = writeFlightPlan("route-mismatch.fpl", """
                flight TP123 regular {
                  leg {
                    departure OPO at 2026-05-10T10:00;
                    arrival LIS at 2026-05-10T11:00;
                    route LIS -> OPO;
                    segment (41.0,-8.0) -> (38.0,-9.0);
                    fuel 5000 kg;
                  }
                }
                """);

        // Act
        final FlightPlanParseResult lexicalResult = validator.validate(lexicalErrorFile);
        final FlightPlanParseResult syntacticResult = validator.validate(syntacticErrorFile);
        final FlightPlanParseResult semanticResult = validator.validate(semanticErrorFile);

        // Assert
        assertEquals(FlightPlanDslErrorType.LEXICAL, lexicalResult.errors().getFirst().type());
        assertEquals(FlightPlanDslErrorType.SYNTACTIC, syntacticResult.errors().getFirst().type());
        assertEquals(List.of(FlightPlanDslErrorType.SEMANTIC), semanticResult.errors().stream().map(FlightPlanDslError::type).distinct().toList());
        assertTrue(semanticResult.errors().stream().anyMatch(error -> error.message().contains("Route origin")));
    }

    private Path writeFlightPlan(final String fileName, final String source) throws IOException {
        final Path file = temporaryDirectory.resolve(fileName);
        Files.writeString(file, source);
        return file;
    }
}
