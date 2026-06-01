package isep.aisafe.flightplan.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryFlightPlanRepositoryTest {

    /**
     * Purpose: verify repository lookup by flight plan identifier.
     * Scenario: save a flight plan and search by its identifier.
     * Expected output: the saved flight plan is returned.
     */
    @Test
    void ensureFindByIdentifierReturnsSavedFlightPlan() {
        // Arrange
        final InMemoryFlightPlanRepository repository = new InMemoryFlightPlanRepository();
        final FlightPlan flightPlan = new FlightPlan(
                FlightPlanIdentifier.valueOf("TP123"),
                PilotIdentifier.valueOf("pilot01")
        );
        repository.save(flightPlan);

        // Act / Assert
        assertTrue(repository.findByIdentifier(FlightPlanIdentifier.valueOf("TP123")).isPresent());
        assertSame(flightPlan, repository.findByIdentifier(FlightPlanIdentifier.valueOf("TP123")).get());
    }

    /**
     * Purpose: verify repository lookup when the flight plan does not exist.
     * Scenario: search for an unknown flight plan identifier.
     * Expected output: no flight plan is returned.
     */
    @Test
    void ensureFindByIdentifierReturnsEmptyWhenFlightPlanDoesNotExist() {
        // Arrange
        final InMemoryFlightPlanRepository repository = new InMemoryFlightPlanRepository();

        // Act / Assert
        assertTrue(repository.findByIdentifier(FlightPlanIdentifier.valueOf("TP404")).isEmpty());
    }
}
