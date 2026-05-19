package isep.aisafe.airtransportcompany.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryAirTransportCompanyRepositoryTest {

    /**
     * Verifica que a pesquisa por codigo IATA devolve a companhia guardada.
     */
    @Test
    void ensureFindByIataCodeReturnsSavedCompany() {
        final InMemoryAirTransportCompanyRepository repository = new InMemoryAirTransportCompanyRepository();

        final AirTransportCompany company = new AirTransportCompany(
                "Repository Test Airline One",
                Country.valueOf("Portugal"),
                IATACode.valueOf("RA"),
                ICAOCode.valueOf("RAA")
        );

        repository.save(company);

        assertTrue(repository.findByIataCode(IATACode.valueOf("RA")).isPresent());
        assertEquals(company, repository.findByIataCode(IATACode.valueOf("RA")).get());
    }

    /**
     * Verifica que a pesquisa por codigo IATA devolve vazio quando nao existe companhia.
     */
    @Test
    void ensureFindByIataCodeReturnsEmptyWhenCompanyDoesNotExist() {
        final InMemoryAirTransportCompanyRepository repository = new InMemoryAirTransportCompanyRepository();

        assertTrue(repository.findByIataCode(IATACode.valueOf("RB")).isEmpty());
    }

    /**
     * Verifica que a pesquisa por codigo ICAO devolve a companhia guardada.
     */
    @Test
    void ensureFindByIcaoCodeReturnsSavedCompany() {
        final InMemoryAirTransportCompanyRepository repository = new InMemoryAirTransportCompanyRepository();

        final AirTransportCompany company = new AirTransportCompany(
                "Repository Test Airline Two",
                Country.valueOf("Portugal"),
                IATACode.valueOf("RC"),
                ICAOCode.valueOf("RCC")
        );

        repository.save(company);

        assertTrue(repository.findByIcaoCode(ICAOCode.valueOf("RCC")).isPresent());
        assertEquals(company, repository.findByIcaoCode(ICAOCode.valueOf("RCC")).get());
    }

    /**
     * Verifica que a pesquisa por codigo ICAO devolve vazio quando nao existe companhia.
     */
    @Test
    void ensureFindByIcaoCodeReturnsEmptyWhenCompanyDoesNotExist() {
        final InMemoryAirTransportCompanyRepository repository = new InMemoryAirTransportCompanyRepository();

        assertTrue(repository.findByIcaoCode(ICAOCode.valueOf("RDD")).isEmpty());
    }
}
