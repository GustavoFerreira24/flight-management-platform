package isep.aisafe.persistence.impl.jpa;

import isep.aisafe.Application;
import isep.aisafe.aircraft.domain.AircraftRepository;
import isep.aisafe.aircraftmodel.domain.AircraftModelRepository;
import isep.aisafe.aircontrolarea.domain.AirControlAreaRepository;
import isep.aisafe.airport.domain.AirportRepository;
import isep.aisafe.airtransportcompany.domain.AirTransportCompanyRepository;
import isep.aisafe.customercollaborator.domain.CustomerCollaboratorRepository;
import isep.aisafe.enginemodel.domain.EngineModelRepository;
import isep.aisafe.flightroute.domain.FlightRouteRepository;
import isep.aisafe.infrastructure.persistence.RepositoryFactory;
import isep.aisafe.weatherdata.domain.WeatherDataRepository;
import eapli.framework.domain.repositories.TransactionalContext;
import eapli.framework.infrastructure.authz.domain.repositories.UserRepository;
import eapli.framework.infrastructure.authz.repositories.impl.jpa.JpaAutoTxUserRepository;
import eapli.framework.infrastructure.repositories.impl.jpa.JpaAutoTxRepository;

public class JpaRepositoryFactory implements RepositoryFactory {

    @Override
    public TransactionalContext newTransactionalContext() {
        return JpaAutoTxRepository.buildTransactionalContext(
                Application.settings().persistenceUnitName(),
                Application.settings().extendedPersistenceProperties()
        );
    }

    @Override
    public UserRepository users(final TransactionalContext autoTx) {
        return new JpaAutoTxUserRepository(autoTx);
    }

    @Override
    public UserRepository users() {
        return new JpaAutoTxUserRepository(
                Application.settings().persistenceUnitName(),
                Application.settings().extendedPersistenceProperties()
        );
    }

    @Override
    public AircraftModelRepository aircraftModels() {
        return new JpaAircraftModelRepository(Application.settings().persistenceUnitName());
    }

    @Override
    public EngineModelRepository engineModels() {
        return new JpaEngineModelRepository(Application.settings().persistenceUnitName());
    }

    @Override
    public AirTransportCompanyRepository airTransportCompanies() {
        return new JpaAirTransportCompanyRepository(Application.settings().persistenceUnitName());
    }

    @Override
    public AirControlAreaRepository airControlAreas() {
        return new JpaAirControlAreaRepository(Application.settings().persistenceUnitName());
    }

    @Override
    public AirportRepository airports() {
        return new JpaAirportRepository(Application.settings().persistenceUnitName());
    }

    @Override
    public CustomerCollaboratorRepository customerCollaborators() {
        return new JpaCustomerCollaboratorRepository(Application.settings().persistenceUnitName());
    }

    @Override
    public AircraftRepository aircraft() {
        return new JpaAircraftRepository(Application.settings().persistenceUnitName());
    }

    @Override
    public FlightRouteRepository flightRoutes() {
        return new JpaFlightRouteRepository(Application.settings().persistenceUnitName());
    }

    @Override
    public WeatherDataRepository weatherData() {
        return new JpaWeatherDataRepository(Application.settings().persistenceUnitName());
    }

}
