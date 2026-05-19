package isep.aisafe.aircraft.domain;

import isep.aisafe.aircraftmodel.domain.AircraftModel;
import isep.aisafe.aircraftmodel.domain.AircraftModelDesignator;
import isep.aisafe.aircraftmodel.domain.AircraftModelMotorization;
import isep.aisafe.aircraftmodel.domain.AircraftModelType;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.airtransportcompany.domain.Country;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.airtransportcompany.domain.ICAOCode;
import isep.aisafe.enginemodel.domain.EngineModel;
import isep.aisafe.enginemodel.domain.EngineModelDesignator;
import isep.aisafe.enginemodel.domain.EngineType;
import isep.aisafe.enginemodel.domain.FuelType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryAircraftRepositoryTest {

    /**
     * Verifica que uma aeronave gravada pode ser encontrada pela matricula.
     */
    @Test
    void ensureFindByRegistrationReturnsSavedAircraft() {
        final InMemoryAircraftRepository repository = new InMemoryAircraftRepository();

        final EngineModel engineModel = engineModel("REPO-LEAP-1", "CFM International");
        final AircraftModel aircraftModel = aircraftModel("Repo A320", "Airbus", 186, engineModel);
        final AirTransportCompany company = company("TAP Air Portugal", "Portugal", "TP", "TAP");

        final Aircraft aircraft = aircraft("CS-R01", company, aircraftModel, engineModel);

        repository.save(aircraft);

        assertTrue(repository.findByRegistration(AircraftRegistration.valueOf("CS-R01")).isPresent());
        assertEquals(aircraft, repository.findByRegistration(AircraftRegistration.valueOf("CS-R01")).get());
    }

    /**
     * Verifica que a pesquisa por matricula devolve vazio quando a aeronave nao existe.
     */
    @Test
    void ensureFindByRegistrationReturnsEmptyWhenAircraftDoesNotExist() {
        final InMemoryAircraftRepository repository = new InMemoryAircraftRepository();

        assertTrue(repository.findByRegistration(AircraftRegistration.valueOf("CS-R02")).isEmpty());
    }

    /**
     * Verifica que a pesquisa por companhia devolve a aeronave gravada dessa companhia.
     */
    @Test
    void ensureFindByCompanyReturnsSavedAircraftFromCompany() {
        final InMemoryAircraftRepository repository = new InMemoryAircraftRepository();

        final EngineModel engineModel = engineModel("REPO-LEAP-2", "CFM International");
        final AircraftModel aircraftModel = aircraftModel("Repo A321", "Airbus", 186, engineModel);
        final AirTransportCompany company = company("TAP Air Portugal", "Portugal", "TP", "TAP");

        final Aircraft aircraft = aircraft("CS-R03", company, aircraftModel, engineModel);

        repository.save(aircraft);

        final Iterable<Aircraft> result = repository.findByCompany(company);

        assertTrue(result.iterator().hasNext());
        assertEquals(aircraft, result.iterator().next());
    }

    /**
     * Verifica que a pesquisa por companhia nao devolve aeronaves de outra companhia.
     */
    @Test
    void ensureFindByCompanyDoesNotReturnAircraftFromAnotherCompany() {
        final InMemoryAircraftRepository repository = new InMemoryAircraftRepository();

        final EngineModel engineModel = engineModel("REPO-LEAP-3", "CFM International");
        final AircraftModel aircraftModel = aircraftModel("Repo A319", "Airbus", 150, engineModel);

        final AirTransportCompany tap = company("TAP Air Portugal", "Portugal", "TP", "TAP");
        final AirTransportCompany ryanair = company("Ryanair", "Ireland", "FR", "RYR");

        final Aircraft aircraft = aircraft("CS-R04", tap, aircraftModel, engineModel);

        repository.save(aircraft);

        assertFalse(repository.findByCompany(ryanair).iterator().hasNext());
    }

    private Aircraft aircraft(
            final String registration,
            final AirTransportCompany company,
            final AircraftModel aircraftModel,
            final EngineModel engineModel) {

        return new Aircraft(
                AircraftRegistration.valueOf(registration),
                AircraftRegistrationCountry.valueOf("Portugal"),
                CabinConfiguration.valueOf(100, 10, 0),
                AircraftOperationalStatus.active(),
                6,
                company,
                aircraftModel,
                engineModel
        );
    }

    private AirTransportCompany company(
            final String name,
            final String country,
            final String iata,
            final String icao) {

        return new AirTransportCompany(
                name,
                Country.valueOf(country),
                IATACode.valueOf(iata),
                ICAOCode.valueOf(icao)
        );
    }

    private EngineModel engineModel(final String modelName, final String manufacturer) {
        return new EngineModel(
                EngineModelDesignator.valueOf(modelName, manufacturer),
                EngineType.valueOf("TURBOFAN"),
                FuelType.valueOf("JET A-1"),
                120.0,
                140.0,
                0.60
        );
    }

    private AircraftModel aircraftModel(
            final String modelName,
            final String manufacturer,
            final int passengerCapacity,
            final EngineModel certifiedEngineModel) {

        return new AircraftModel(
                AircraftModelDesignator.valueOf(modelName, manufacturer),
                AircraftModelType.valueOf("PASSENGER"),
                AircraftModelMotorization.valueOf("TURBOFAN"),
                6300,
                44500,
                79000,
                64500,
                27200,
                12000,
                833,
                122.6,
                0.024,
                0.62,
                passengerCapacity,
                List.of(certifiedEngineModel)
        );
    }
}
