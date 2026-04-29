package isep.aisafe.infrastructure.persistence;

import eapli.framework.domain.repositories.TransactionalContext;
import eapli.framework.infrastructure.authz.domain.repositories.UserRepository;
import isep.aisafe.aircraftmodel.domain.AircraftModelRepository;
import isep.aisafe.enginemodel.domain.EngineModelRepository;

public interface RepositoryFactory {

    TransactionalContext newTransactionalContext();

    UserRepository users(TransactionalContext autoTx);

    UserRepository users();

    AircraftModelRepository aircraftModels();

    EngineModelRepository engineModels();
}
