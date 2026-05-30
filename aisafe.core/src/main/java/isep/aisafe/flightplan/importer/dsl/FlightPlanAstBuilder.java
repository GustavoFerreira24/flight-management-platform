package isep.aisafe.flightplan.importer.dsl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

final class FlightPlanAstBuilder extends FlightPlanDslBaseVisitor<Object> {

    List<DslFlightPlan> build(final FlightPlanDslParser.FlightPlanFileContext context) {
        final List<DslFlightPlan> flightPlans = new ArrayList<>();

        for (FlightPlanDslParser.FlightBlockContext flightBlockContext : context.flightBlock()) {
            flightPlans.add((DslFlightPlan) visit(flightBlockContext));
        }

        return flightPlans;
    }

    @Override
    public DslFlightPlan visitFlightBlock(final FlightPlanDslParser.FlightBlockContext ctx) {
        final List<DslFlightLeg> legs = new ArrayList<>();

        for (FlightPlanDslParser.LegBlockContext legContext : ctx.legBlock()) {
            legs.add((DslFlightLeg) visit(legContext));
        }

        return new DslFlightPlan(
                ctx.flightIdentifier().getText(),
                ctx.flightType().getText(),
                aircraftRegistration(ctx),
                pilotIdentifier(ctx),
                load(ctx),
                legs,
                location(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine())
        );
    }

    @Override
    public DslFlightLeg visitLegBlock(final FlightPlanDslParser.LegBlockContext ctx) {
        final List<DslSegment> segments = new ArrayList<>();

        for (FlightPlanDslParser.SegmentStatementContext segmentContext : ctx.segmentStatement()) {
            segments.add((DslSegment) visit(segmentContext));
        }

        return new DslFlightLeg(
                (DslFlightEndpoint) visit(ctx.departureStatement()),
                (DslFlightEndpoint) visit(ctx.arrivalStatement()),
                (DslRoute) visit(ctx.routeStatement()),
                segments,
                (DslQuantity) visit(ctx.fuelStatement()),
                location(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine())
        );
    }

    @Override
    public DslFlightEndpoint visitDepartureStatement(final FlightPlanDslParser.DepartureStatementContext ctx) {
        return endpoint(ctx.airportCode(), ctx.dateTime());
    }

    @Override
    public DslFlightEndpoint visitArrivalStatement(final FlightPlanDslParser.ArrivalStatementContext ctx) {
        return endpoint(ctx.airportCode(), ctx.dateTime());
    }

    @Override
    public DslRoute visitRouteStatement(final FlightPlanDslParser.RouteStatementContext ctx) {
        return new DslRoute(
                ctx.airportCode(0).getText(),
                ctx.airportCode(1).getText(),
                location(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine())
        );
    }

    @Override
    public DslSegment visitSegmentStatement(final FlightPlanDslParser.SegmentStatementContext ctx) {
        final DslCoordinate start = (DslCoordinate) visit(ctx.coordinate(0));
        final DslCoordinate end = (DslCoordinate) visit(ctx.coordinate(1));
        final Optional<DslQuantity> speed = Optional.ofNullable(ctx.speedClause())
                .map(speedContext -> (DslQuantity) visit(speedContext));
        final Optional<DslAltitudeSlot> altitudeSlot = Optional.ofNullable(ctx.altitudeClause())
                .map(altitudeContext -> (DslAltitudeSlot) visit(altitudeContext));
        final Optional<DslQuantity> width = Optional.ofNullable(ctx.widthClause())
                .map(widthContext -> (DslQuantity) visit(widthContext));
        final Optional<DslWind> wind = Optional.ofNullable(ctx.windClause())
                .map(windContext -> (DslWind) visit(windContext));

        return new DslSegment(
                start,
                end,
                speed,
                altitudeSlot,
                width,
                wind,
                location(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine())
        );
    }

    @Override
    public DslQuantity visitSpeedClause(final FlightPlanDslParser.SpeedClauseContext ctx) {
        return new DslQuantity(
                number(ctx.number()),
                ctx.speedUnit() == null ? "" : ctx.speedUnit().getText(),
                location(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine())
        );
    }

    @Override
    public DslQuantity visitLoadStatement(final FlightPlanDslParser.LoadStatementContext ctx) {
        return new DslQuantity(
                number(ctx.number()),
                ctx.unit() == null ? "" : ctx.unit().getText(),
                location(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine())
        );
    }

    @Override
    public DslAltitudeSlot visitAltitudeClause(final FlightPlanDslParser.AltitudeClauseContext ctx) {
        return new DslAltitudeSlot(
                number(ctx.number(0)),
                number(ctx.number(1)),
                ctx.unit() == null ? null : ctx.unit().getText(),
                location(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine())
        );
    }

    @Override
    public DslQuantity visitWidthClause(final FlightPlanDslParser.WidthClauseContext ctx) {
        return new DslQuantity(
                number(ctx.number()),
                ctx.unit() == null ? "" : ctx.unit().getText(),
                location(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine())
        );
    }

    @Override
    public DslWind visitWindClause(final FlightPlanDslParser.WindClauseContext ctx) {
        return new DslWind(
                number(ctx.number(0)),
                number(ctx.number(1)),
                ctx.windUnit() == null ? null : ctx.windUnit().getText(),
                location(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine())
        );
    }

    @Override
    public DslQuantity visitFuelStatement(final FlightPlanDslParser.FuelStatementContext ctx) {
        return new DslQuantity(
                number(ctx.number()),
                ctx.unit().getText(),
                location(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine())
        );
    }

    @Override
    public DslCoordinate visitCoordinate(final FlightPlanDslParser.CoordinateContext ctx) {
        return new DslCoordinate(number(ctx.number(0)), number(ctx.number(1)));
    }

    private DslFlightEndpoint endpoint(
            final FlightPlanDslParser.AirportCodeContext airportCode,
            final FlightPlanDslParser.DateTimeContext dateTime) {

        return new DslFlightEndpoint(
                airportCode.getText(),
                LocalDateTime.parse(normalizedDateTime(dateTime)),
                location(airportCode.getStart().getLine(), airportCode.getStart().getCharPositionInLine())
        );
    }

    private double number(final FlightPlanDslParser.NumberContext context) {
        return Double.parseDouble(context.getText());
    }

    private String normalizedDateTime(final FlightPlanDslParser.DateTimeContext context) {
        if (context.DATE_TIME() != null) {
            return context.DATE_TIME().getText();
        }

        return context.DATE().getText() + "T" + context.TIME().getText();
    }

    private DslSourceLocation location(final int line, final int column) {
        return new DslSourceLocation(line, column);
    }

    private String aircraftRegistration(final FlightPlanDslParser.FlightBlockContext context) {
        for (FlightPlanDslParser.FlightMetadataContext metadata : context.flightMetadata()) {
            if (metadata.aircraftStatement() != null) {
                return metadata.aircraftStatement().aircraftRegistration().getText();
            }
        }

        return null;
    }

    private String pilotIdentifier(final FlightPlanDslParser.FlightBlockContext context) {
        for (FlightPlanDslParser.FlightMetadataContext metadata : context.flightMetadata()) {
            if (metadata.pilotStatement() != null) {
                return metadata.pilotStatement().pilotIdentifier().getText();
            }
        }

        return null;
    }

    private DslQuantity load(final FlightPlanDslParser.FlightBlockContext context) {
        if (context.loadStatement().isEmpty()) {
            return null;
        }

        return (DslQuantity) visit(context.loadStatement().getFirst());
    }
}
