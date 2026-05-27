package isep.aisafe.manufacturer.domain;

import eapli.framework.infrastructure.repositories.impl.inmemory.InMemoryDomainRepository;

import java.util.Optional;

public class InMemoryManufacturerRepository
        extends InMemoryDomainRepository<Manufacturer, ManufacturerDesignator>
        implements ManufacturerRepository {

    @Override
    public Optional<Manufacturer> findByDesignator(final ManufacturerDesignator designator) {
        return matchOne(manufacturer -> manufacturer.designator().equals(designator));
    }
}
