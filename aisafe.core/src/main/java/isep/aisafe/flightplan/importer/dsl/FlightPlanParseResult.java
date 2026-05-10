package isep.aisafe.flightplan.importer.dsl;

import java.util.List;
import java.util.Objects;

/**
 * Result of a Core Flight DSL lexical and syntactic validation operation.
 */
public final class FlightPlanParseResult {

    private final List<FlightPlanDslError> errors;
    private final String parseTree;
    private final DslFlightPlan flightPlan;

    private FlightPlanParseResult(
            final List<FlightPlanDslError> errors,
            final String parseTree,
            final DslFlightPlan flightPlan) {
        this.errors = List.copyOf(Objects.requireNonNull(errors, "errors cannot be null"));
        this.parseTree = parseTree;
        this.flightPlan = flightPlan;
    }

    /**
     * Creates a successful parsing result.
     *
     * @param parseTree the generated parse tree as text
     * @return a successful result
     */
    public static FlightPlanParseResult success(final String parseTree) {
        return success(parseTree, null);
    }

    /**
     * Creates a successful parsing result with an internal DSL representation.
     *
     * @param parseTree the generated parse tree as text
     * @param flightPlan the internal flight plan representation
     * @return a successful result
     */
    public static FlightPlanParseResult success(final String parseTree, final DslFlightPlan flightPlan) {
        return new FlightPlanParseResult(
                List.of(),
                Objects.requireNonNull(parseTree, "parseTree cannot be null"),
                flightPlan
        );
    }

    /**
     * Creates a failed parsing result.
     *
     * @param errors the collected lexical and syntactic errors
     * @return a failed result
     */
    public static FlightPlanParseResult failure(final List<FlightPlanDslError> errors) {
        if (Objects.requireNonNull(errors, "errors cannot be null").isEmpty()) {
            throw new IllegalArgumentException("errors cannot be empty for a failed result");
        }

        return new FlightPlanParseResult(errors, null, null);
    }

    /**
     * Checks if the file was accepted by lexical and syntactic validation.
     *
     * @return true when no errors were found
     */
    public boolean isValid() {
        return errors.isEmpty();
    }

    /**
     * Returns the collected validation errors.
     *
     * @return immutable list of errors
     */
    public List<FlightPlanDslError> errors() {
        return errors;
    }

    /**
     * Returns the parse tree representation when parsing is successful.
     *
     * @return the parse tree as text, or null when validation failed
     */
    public String parseTree() {
        return parseTree;
    }

    /**
     * Returns the internal DSL representation when validation succeeds.
     *
     * @return the parsed flight plan, or null when validation failed
     */
    public DslFlightPlan flightPlan() {
        return flightPlan;
    }
}
