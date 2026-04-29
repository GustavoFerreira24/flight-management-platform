package isep.aisafe.aircraftmodel.domain;

import eapli.framework.domain.repositories.DomainRepository;

import java.util.Optional;

public interface AircraftModelRepository extends DomainRepository<AircraftModelDesignator, AircraftModel> {

    Optional<AircraftModel> findByDesignator(AircraftModelDesignator designator);
}
