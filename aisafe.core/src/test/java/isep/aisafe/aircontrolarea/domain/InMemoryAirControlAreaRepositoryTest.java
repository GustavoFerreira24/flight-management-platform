package isep.aisafe.aircontrolarea.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryAirControlAreaRepositoryTest {

    /**
     * Purpose: verify the in-memory repository lookup needed by application tests.
     * Scenario: save an air control area and search by the same code.
     * Expected output: the air control area is found.
     */
    @Test
    void ensureAirControlAreaCanBeFoundByCode() {
        final InMemoryAirControlAreaRepository repository = new InMemoryAirControlAreaRepository();
        final AirControlAreaCode code = AirControlAreaCode.valueOf("LISACC");
        final AirControlArea area = new AirControlArea(code, "Lisbon Area Control Centre");

        repository.save(area);

        assertTrue(repository.findByCode(code).isPresent());
    }

    /**
     * Purpose: verify lookup behavior when no air control area exists.
     * Scenario: search an empty repository by code.
     * Expected output: no air control area is found.
     */
    @Test
    void ensureUnknownAirControlAreaIsNotFound() {
        final InMemoryAirControlAreaRepository repository = new InMemoryAirControlAreaRepository();

        assertTrue(repository.findByCode(AirControlAreaCode.valueOf("LISACC")).isEmpty());
    }
}