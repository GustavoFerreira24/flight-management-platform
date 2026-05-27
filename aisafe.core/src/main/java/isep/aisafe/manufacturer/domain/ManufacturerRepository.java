package isep.aisafe.manufacturer.domain;

import eapli.framework.domain.repositories.DomainRepository;

import java.util.Optional;

public interface ManufacturerRepository extends DomainRepository<ManufacturerDesignator, Manufacturer> {

    Optional<Manufacturer> findByDesignator(ManufacturerDesignator designator);
}
