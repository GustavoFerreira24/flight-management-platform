package isep.aisafe.application.flightmanagement;

import isep.aisafe.flightplan.domain.FlightPlanStatus;
import isep.aisafe.flightplan.importer.dsl.FlightPlanDslError;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Result of testing and validating a flight plan.
 */
public final class TestFlightPlanResult {

    private final boolean successful;
    private final FlightPlanStatus status;
    private final List<String> messages;
    private final List<FlightPlanDslError> dslErrors;

    private TestFlightPlanResult(
            final boolean successful,
            final FlightPlanStatus status,
            final List<String> messages,
            final List<FlightPlanDslError> dslErrors) {

        this.successful = successful;
        this.status = Objects.requireNonNull(status, "status cannot be null");
        this.messages = List.copyOf(Objects.requireNonNull(messages, "messages cannot be null"));
        this.dslErrors = List.copyOf(Objects.requireNonNull(dslErrors, "dslErrors cannot be null"));
    }

    public static TestFlightPlanResult success(final FlightPlanStatus status) {
        return new TestFlightPlanResult(
                true,
                status,
                List.of("Flight plan validated successfully."),
                List.of()
        );
    }

    public static TestFlightPlanResult failure(
            final FlightPlanStatus status,
            final List<String> messages) {

        return failure(status, messages, List.of());
    }

    public static TestFlightPlanResult failure(
            final FlightPlanStatus status,
            final List<String> messages,
            final List<FlightPlanDslError> dslErrors) {

        final List<String> safeMessages = new ArrayList<>(Objects.requireNonNull(messages, "messages cannot be null"));
        if (safeMessages.isEmpty()) {
            safeMessages.add("Flight plan validation failed.");
        }

        return new TestFlightPlanResult(false, status, safeMessages, dslErrors);
    }

    public boolean isSuccessful() {
        return successful;
    }

    public FlightPlanStatus status() {
        return status;
    }

    public List<String> messages() {
        return messages;
    }

    public List<FlightPlanDslError> dslErrors() {
        return dslErrors;
    }
}