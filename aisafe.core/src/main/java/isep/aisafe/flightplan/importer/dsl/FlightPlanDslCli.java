package isep.aisafe.flightplan.importer.dsl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Small command-line runner for manually validating a Core Flight DSL file.
 */
public final class FlightPlanDslCli {

    private FlightPlanDslCli() {
        // Utility class.
    }

    public static void main(final String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: FlightPlanDslCli <flight-plan-file>");
            return;
        }

        final FlightPlanParseResult result = new FlightPlanDslSyntaxValidator().validate(Path.of(args[0]));

        if (!result.isValid()) {
            System.out.println("Flight plan is invalid:");
            for (FlightPlanDslError error : result.errors()) {
                System.out.printf("%s at %d:%d - %s%n",
                        error.type(),
                        error.line(),
                        error.column(),
                        error.message());
            }
            return;
        }

        final DslFlightPlan flightPlan = result.flightPlan();

        System.out.println("Result with Visitor:");
        System.out.printf("Flight: %s (%s)%n", flightPlan.identifier(), flightPlan.type());
        flightPlan.aircraftRegistration().ifPresent(aircraft -> System.out.printf("Aircraft: %s%n", aircraft));
        flightPlan.pilotIdentifier().ifPresent(pilot -> System.out.printf("Pilot: %s%n", pilot));
        System.out.printf("Legs: %d%n", flightPlan.legs().size());
        printLegs(flightPlan);
        System.out.println("Parse tree:");
        System.out.println(result.parseTree());
    }

    private static void printLegs(final DslFlightPlan flightPlan) {
        for (int i = 0; i < flightPlan.legs().size(); i++) {
            final DslFlightLeg leg = flightPlan.legs().get(i);

            System.out.printf("Leg %d:%n", i + 1);
            System.out.printf("  Departure: %s at %s%n", leg.departure().airportCode(), leg.departure().dateTime());
            System.out.printf("  Arrival: %s at %s%n", leg.arrival().airportCode(), leg.arrival().dateTime());
            System.out.printf("  Route: %s -> %s%n", leg.route().originAirportCode(), leg.route().destinationAirportCode());
            System.out.printf(Locale.ROOT, "  Fuel: %.2f %s%n", leg.fuel().value(), leg.fuel().unit());
            System.out.printf("  Segments: %d%n", leg.segments().size());

            for (int j = 0; j < leg.segments().size(); j++) {
                printSegment(j + 1, leg.segments().get(j));
            }
        }
    }

    private static void printSegment(final int number, final DslSegment segment) {
        System.out.printf(Locale.ROOT,
                "    Segment %d: (%.6f, %.6f) -> (%.6f, %.6f)%n",
                number,
                segment.start().latitude(),
                segment.start().longitude(),
                segment.end().latitude(),
                segment.end().longitude());

        segment.altitudeSlot().ifPresent(altitude -> System.out.printf(Locale.ROOT,
                "      Altitude: %.2f - %.2f %s%n",
                altitude.minimum(),
                altitude.maximum(),
                altitude.unit() == null ? "" : altitude.unit()));
        segment.width().ifPresent(width -> System.out.printf(Locale.ROOT,
                "      Width: %.2f %s%n",
                width.value(),
                width.unit()));
        segment.wind().ifPresent(wind -> System.out.printf(Locale.ROOT,
                "      Wind: %.2f deg %.2f %s%n",
                wind.directionDegrees(),
                wind.speed(),
                wind.unit() == null ? "" : wind.unit()));
    }
}
