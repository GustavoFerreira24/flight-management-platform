package isep.aisafe.infrastructure.persistence;

import eapli.framework.domain.repositories.TransactionalContext;
import eapli.framework.infrastructure.authz.domain.repositories.UserRepository;
import isep.aisafe.aircraft.domain.AircraftRepository;
import isep.aisafe.aircraftmodel.domain.AircraftModelRepository;
import isep.aisafe.aircontrolarea.domain.AirControlAreaRepository;
import isep.aisafe.airport.domain.AirportRepository;
import isep.aisafe.airtransportcompany.domain.AirTransportCompanyRepository;
import isep.aisafe.customercollaborator.domain.CustomerCollaboratorRepository;
import isep.aisafe.enginemodel.domain.EngineModelRepository;
import isep.aisafe.manufacturer.domain.ManufacturerRepository;
import isep.aisafe.weatherdata.domain.WeatherDataRepository;

/**
 * Factory contract for repositories used by the application layer.
 */
public interface RepositoryFactory {

    /**
     * Creates a transactional context for operations that must share the same unit of work.
     *
     * @return new transactional context
     */
    TransactionalContext newTransactionalContext();

    /**
     * Returns a user repository bound to an existing transaction.
     *
     * @param autoTx transaction context to use
     * @return user repository using the provided transaction
     */
    UserRepository users(TransactionalContext autoTx);

    /**
     * Returns a user repository with its default transaction handling.
     *
     * @return user repository
     */
    UserRepository users();

    /**
     * Returns the repository for aircraft model aggregates.
     *
     * @return aircraft model repository
     */
    AircraftModelRepository aircraftModels();

    /**
     * Returns the repository for engine model aggregates.
     *
     * @return engine model repository
     */
    EngineModelRepository engineModels();

    /**
     * Returns the repository for manufacturer aggregates.
     *
     * @return manufacturer repository
     */
    ManufacturerRepository manufacturers();

    /**
     * Returns the repository for air transport company aggregates.
     *
     * @return air transport company repository
     */
    AirTransportCompanyRepository airTransportCompanies();

    /**
     * Returns the repository for air control area aggregates.
     *
     * @return air control area repository
     */
    AirControlAreaRepository airControlAreas();

    /**
     * Returns the repository for airport aggregates.
     *
     * @return airports repository
     */
    AirportRepository airports();

    /**
     * Returns the repository for customer collaborator aggregates.
     *
     * @return customer collaborator repository
     */
    CustomerCollaboratorRepository customerCollaborators();

    /**
     * Returns the repository for aircraft aggregates.
     *
     * @return aircraft repository
     */
    AircraftRepository aircraft();

    /**
     * Returns the repository for weather data aggregates.
     *
     * @return weather data repository
     */
    WeatherDataRepository weatherData();

}
