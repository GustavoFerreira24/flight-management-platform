package isep.aisafe.flightplan.importer.dsl;

import java.io.IOException;
import java.nio.file.Path;

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
        System.out.println("Parse tree:");
        System.out.println(result.parseTree());
    }
}
