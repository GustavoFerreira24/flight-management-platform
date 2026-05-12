package isep.aisafe.airport.domain;

import isep.aisafe.aircontrolarea.domain.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para o repositório de aeroportos em memória.
 * Segue o padrão de criação de entidades reais para validar o comportamento do repositório.
 */
class InMemoryAirportRepositoryTest {

    @Test
    void ensureFindByIcaoCodeReturnsSavedAirport() {
        final InMemoryAirportRepository repository = new InMemoryAirportRepository();
        final AirControlArea area = airControlArea("LISACC");
        final Airport airport = airport("LPPR", "OPO", "Francisco Sá Carneiro", "Porto", area);

        repository.save(airport);

        assertTrue(repository.findByIcaoCode(AirportICAOCode.valueOf("LPPR")).isPresent());
        assertEquals(airport, repository.findByIcaoCode(AirportICAOCode.valueOf("LPPR")).get());
    }

    @Test
    void ensureFindByIataCodeReturnsSavedAirport() {
        final InMemoryAirportRepository repository = new InMemoryAirportRepository();
        final AirControlArea area = airControlArea("LISACC");
        final Airport airport = airport("LPPT", "LIS", "Humberto Delgado", "Lisboa", area);

        repository.save(airport);

        assertTrue(repository.findByIataCode(AirportIATACode.valueOf("LIS")).isPresent());
        assertEquals(airport, repository.findByIataCode(AirportIATACode.valueOf("LIS")).get());
    }

    @Test
    void ensureFindByIcaoCodeReturnsEmptyWhenNotFound() {
        final InMemoryAirportRepository repository = new InMemoryAirportRepository();

        assertTrue(repository.findByIcaoCode(AirportICAOCode.valueOf("LPPT")).isEmpty());
    }

    @Test
    void ensureFindByIataCodeReturnsEmptyWhenNotFound() {
        final InMemoryAirportRepository repository = new InMemoryAirportRepository();

        assertTrue(repository.findByIataCode(AirportIATACode.valueOf("LIS")).isEmpty());
    }

    /**
     * Métodos auxiliares (Factories) para criar objetos de domínio reais sem Mocks.
     */

    private Airport airport(
            final String icao,
            final String iata,
            final String name,
            final String town,
            final AirControlArea area) {

        return new Airport(
                AirportICAOCode.valueOf(icao),
                AirportIATACode.valueOf(iata),
                name,
                town,
                new GpsCoordinates(41.248, -8.681),
                new Elevation(69.0),
                area
        );
    }

    private AirControlArea airControlArea(final String code) {
        return new AirControlArea(
                AirControlAreaCode.valueOf(code),
                "Lisbon ACC",
                GeographicBoundary.valueOf(35.0, 45.0, -15.0, 5.0),
                MinimumFuelRequirement.valueOf(5000, FuelUnit.valueOf("KG"))
        );
    }
}