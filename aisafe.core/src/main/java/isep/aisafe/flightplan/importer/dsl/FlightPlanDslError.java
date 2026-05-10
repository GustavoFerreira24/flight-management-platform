package isep.aisafe.flightplan.importer.dsl;

import java.util.Objects;

/**
 * Error reported while validating a Core Flight DSL file.
 *
 */
public final class FlightPlanDslError {

    private final FlightPlanDslErrorType type;
    private final int line;
    private final int column;
    private final String message;

    /**
     * Creates a DSL validation error.
     *
     * @param type the error type
     * @param line the source line
     * @param column the source column
     * @param message the error message
     */
    public FlightPlanDslError(
            final FlightPlanDslErrorType type,
            final int line,
            final int column,
            final String message) {

        this.type = Objects.requireNonNull(type, "type cannot be null");
        this.message = Objects.requireNonNull(message, "message cannot be null");

        if (line < 1) {
            throw new IllegalArgumentException("line must be greater than or equal to 1");
        }

        if (column < 0) {
            throw new IllegalArgumentException("column must be greater than or equal to 0");
        }

        this.line = line;
        this.column = column;
    }

    public FlightPlanDslErrorType type() {
        return type;
    }

    public int line() {
        return line;
    }

    public int column() {
        return column;
    }

    public String message() {
        return message;
    }
}
