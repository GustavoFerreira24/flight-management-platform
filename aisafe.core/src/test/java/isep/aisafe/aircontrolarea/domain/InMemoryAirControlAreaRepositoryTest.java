package isep.aisafe.aircontrolarea.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryAirControlAreaRepositoryTest {

    /**
     * Verifica que a pesquisa por codigo devolve uma area guardada.
     */
    @Test
    void ensureFindByCodeReturnsSavedArea() {
        final InMemoryAirControlAreaRepository repository = new InMemoryAirControlAreaRepository();

        final AirControlArea area = new AirControlArea(
                AirControlAreaCode.valueOf("LPPTFIR"),
                "Lisbon FIR",
                GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0),
                MinimumFuelRequirement.valueOf(5000.0, FuelUnit.valueOf("KG"))
        );

        repository.save(area);

        assertTrue(repository.findByCode(AirControlAreaCode.valueOf("LPPTFIR")).isPresent());
        assertEquals(area, repository.findByCode(AirControlAreaCode.valueOf("LPPTFIR")).get());
    }

    /**
     * Verifica que a pesquisa por codigo devolve vazio quando a area nao existe.
     */
    @Test
    void ensureFindByCodeReturnsEmptyWhenAreaDoesNotExist() {
        final InMemoryAirControlAreaRepository repository = new InMemoryAirControlAreaRepository();

        assertTrue(repository.findByCode(AirControlAreaCode.valueOf("LPPTFIR")).isEmpty());
    }
}