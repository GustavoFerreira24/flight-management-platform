package isep.aisafe.infrastructure.persistence;

import eapli.framework.domain.repositories.TransactionalContext;
import eapli.framework.infrastructure.authz.domain.repositories.UserRepository;
import isep.aisafe.aircraftmodel.domain.AircraftModelRepository;
import isep.aisafe.aircontrolarea.domain.AirControlAreaRepository;
import isep.aisafe.airtransportcompany.domain.AirTransportCompanyRepository;
import isep.aisafe.customercollaborator.domain.CustomerCollaboratorRepository;
import isep.aisafe.enginemodel.domain.EngineModelRepository;

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

    AirTransportCompanyRepository airTransportCompanies();

    AirControlAreaRepository airControlAreas();

    CustomerCollaboratorRepository customerCollaborators();

}
