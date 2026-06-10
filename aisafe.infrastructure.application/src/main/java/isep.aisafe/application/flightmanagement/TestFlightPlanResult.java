package isep.aisafe.application.flightmanagement;

import isep.aisafe.flightplan.domain.FlightPlanTestStatus;
import isep.aisafe.flightplan.importer.dsl.FlightPlanDslError;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Result of testing and validating a flight plan.
 */
public final class TestFlightPlanResult {

    private final boolean successful;
    private final FlightPlanTestStatus testStatus;
    private final List<String> messages;
    private final List<FlightPlanDslError> dslErrors;

    private TestFlightPlanResult(
            final boolean successful,
            final FlightPlanTestStatus testStatus,
            final List<String> messages,
            final List<FlightPlanDslError> dslErrors) {

        this.successful = successful;
        this.testStatus = Objects.requireNonNull(testStatus, "testStatus cannot be null");
        this.messages = List.copyOf(Objects.requireNonNull(messages, "messages cannot be null"));
        this.dslErrors = List.copyOf(Objects.requireNonNull(dslErrors, "dslErrors cannot be null"));
    }

    public static TestFlightPlanResult success(final FlightPlanTestStatus testStatus) {
        return new TestFlightPlanResult(
                true,
                testStatus,
                List.of("Flight plan tested successfully."),
                List.of()
        );
    }

    public static TestFlightPlanResult failure(
            final FlightPlanTestStatus testStatus,
            final List<String> messages) {

        return failure(testStatus, messages, List.of());
    }

    public static TestFlightPlanResult failure(
            final FlightPlanTestStatus testStatus,
            final List<String> messages,
            final List<FlightPlanDslError> dslErrors) {

        final List<String> safeMessages = new ArrayList<>(Objects.requireNonNull(messages, "messages cannot be null"));
        if (safeMessages.isEmpty()) {
            safeMessages.add("Flight plan test failed.");
        }

        return new TestFlightPlanResult(false, testStatus, safeMessages, dslErrors);
    }

    public boolean isSuccessful() {
        return successful;
    }

    public FlightPlanTestStatus testStatus() {
        return testStatus;
    }

    public List<String> messages() {
        return messages;
    }

    public List<FlightPlanDslError> dslErrors() {
        return dslErrors;
    }
}
