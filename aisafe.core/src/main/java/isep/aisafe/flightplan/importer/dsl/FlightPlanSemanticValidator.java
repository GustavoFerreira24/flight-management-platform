package isep.aisafe.flightplan.importer.dsl;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class FlightPlanSemanticValidator extends FlightPlanDslBaseListener {

    private final List<FlightPlanDslError> errors = new ArrayList<>();
    private final List<LegSummary> legs = new ArrayList<>();

    @Override
    public void exitLegBlock(final FlightPlanDslParser.LegBlockContext ctx) {
        final FlightPlanDslParser.DepartureStatementContext departure = ctx.departureStatement();
        final FlightPlanDslParser.ArrivalStatementContext arrival = ctx.arrivalStatement();
        final FlightPlanDslParser.RouteStatementContext route = ctx.routeStatement();

        final LocalDateTime departureDateTime = parseDateTime(departure.dateTime());
        final LocalDateTime arrivalDateTime = parseDateTime(arrival.dateTime());

        if (departureDateTime != null && arrivalDateTime != null && !departureDateTime.isBefore(arrivalDateTime)) {
            addError(arrival.getStart().getLine(), arrival.getStart().getCharPositionInLine(),
                    "Arrival date/time must be after departure date/time in the same leg.");
        }

        final String departureAirport = departure.airportCode().getText();
        final String arrivalAirport = arrival.airportCode().getText();
        final String routeOrigin = route.airportCode(0).getText();
        final String routeDestination = route.airportCode(1).getText();

        if (!departureAirport.equals(routeOrigin)) {
            addError(route.getStart().getLine(), route.getStart().getCharPositionInLine(),
                    "Route origin must match leg departure airport.");
        }

        if (!arrivalAirport.equals(routeDestination)) {
            addError(route.getStart().getLine(), route.getStart().getCharPositionInLine(),
                    "Route destination must match leg arrival airport.");
        }

        legs.add(new LegSummary(departureAirport, arrivalAirport, departureDateTime, arrivalDateTime, ctx));
    }

    @Override
    public void exitFlightBlock(final FlightPlanDslParser.FlightBlockContext ctx) {
        validateLegSequence();
        validateAirportRevisits();
    }

    @Override
    public void exitSegmentStatement(final FlightPlanDslParser.SegmentStatementContext ctx) {
        final double startLatitude = number(ctx.coordinate(0).number(0));
        final double startLongitude = number(ctx.coordinate(0).number(1));
        final double endLatitude = number(ctx.coordinate(1).number(0));
        final double endLongitude = number(ctx.coordinate(1).number(1));

        if (Double.compare(startLatitude, endLatitude) == 0 && Double.compare(startLongitude, endLongitude) == 0) {
            addError(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                    "Segment start and end coordinates must be different.");
        }
    }

    @Override
    public void exitAltitudeClause(final FlightPlanDslParser.AltitudeClauseContext ctx) {
        final double minimum = number(ctx.number(0));
        final double maximum = number(ctx.number(1));

        if (minimum <= 0 || maximum <= 0) {
            addError(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                    "Altitude values must be positive.");
        }

        if (minimum >= maximum) {
            addError(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                    "Altitude minimum must be lower than altitude maximum.");
        }
    }

    @Override
    public void exitWidthClause(final FlightPlanDslParser.WidthClauseContext ctx) {
        if (number(ctx.number()) <= 0) {
            addError(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                    "Segment width must be positive.");
        }
    }

    @Override
    public void exitWindClause(final FlightPlanDslParser.WindClauseContext ctx) {
        final double direction = number(ctx.number(0));
        final double speed = number(ctx.number(1));

        if (direction < 0 || direction >= 360) {
            addError(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                    "Wind direction must be greater than or equal to 0 and lower than 360 degrees.");
        }

        if (speed <= 0) {
            addError(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                    "Wind speed must be positive.");
        }
    }

    @Override
    public void exitFuelStatement(final FlightPlanDslParser.FuelStatementContext ctx) {
        if (number(ctx.number()) <= 0) {
            addError(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                    "Fuel quantity must be positive.");
        }
    }

    boolean hasErrors() {
        return !errors.isEmpty();
    }

    List<FlightPlanDslError> errors() {
        return List.copyOf(errors);
    }

    private void validateLegSequence() {
        for (int i = 0; i < legs.size() - 1; i++) {
            final LegSummary current = legs.get(i);
            final LegSummary next = legs.get(i + 1);

            if (!current.arrivalAirport.equals(next.departureAirport)) {
                addError(next.context.getStart().getLine(), next.context.getStart().getCharPositionInLine(),
                        "The arrival airport of a leg must match the departure airport of the following leg.");
            }

            if (current.arrivalDateTime != null
                    && next.departureDateTime != null
                    && !current.arrivalDateTime.isBefore(next.departureDateTime)) {
                addError(next.context.getStart().getLine(), next.context.getStart().getCharPositionInLine(),
                        "The arrival date/time of a leg must be before the departure date/time of the following leg.");
            }
        }
    }

    private void validateAirportRevisits() {
        final Set<String> visitedAirports = new HashSet<>();

        for (int i = 0; i < legs.size(); i++) {
            final LegSummary leg = legs.get(i);

            if (i == 0 && !visitedAirports.add(leg.departureAirport)) {
                addError(leg.context.getStart().getLine(), leg.context.getStart().getCharPositionInLine(),
                        "The same airport cannot be visited twice in a flight.");
            }

            if (!visitedAirports.add(leg.arrivalAirport)) {
                addError(leg.context.arrivalStatement().getStart().getLine(),
                        leg.context.arrivalStatement().getStart().getCharPositionInLine(),
                        "The same airport cannot be visited twice in a flight.");
            }
        }
    }

    private LocalDateTime parseDateTime(final FlightPlanDslParser.DateTimeContext context) {
        final String value = normalizedDateTime(context);

        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeException e) {
            addError(context.getStart().getLine(), context.getStart().getCharPositionInLine(),
                    "Invalid date/time value: " + value + ".");
            return null;
        }
    }

    private String normalizedDateTime(final FlightPlanDslParser.DateTimeContext context) {
        if (context.DATE_TIME() != null) {
            return context.DATE_TIME().getText();
        }

        return context.DATE().getText() + "T" + context.TIME().getText();
    }

    private double number(final FlightPlanDslParser.NumberContext context) {
        return Double.parseDouble(context.getText());
    }

    private void addError(final int line, final int column, final String message) {
        errors.add(new FlightPlanDslError(FlightPlanDslErrorType.SEMANTIC, line, column, message));
    }

    private static final class LegSummary {

        private final String departureAirport;
        private final String arrivalAirport;
        private final LocalDateTime departureDateTime;
        private final LocalDateTime arrivalDateTime;
        private final FlightPlanDslParser.LegBlockContext context;

        private LegSummary(
                final String departureAirport,
                final String arrivalAirport,
                final LocalDateTime departureDateTime,
                final LocalDateTime arrivalDateTime,
                final FlightPlanDslParser.LegBlockContext context) {

            this.departureAirport = departureAirport;
            this.arrivalAirport = arrivalAirport;
            this.departureDateTime = departureDateTime;
            this.arrivalDateTime = arrivalDateTime;
            this.context = context;
        }
    }
}
