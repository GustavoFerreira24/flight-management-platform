package isep.aisafe.airtransportcompany.domain;

import eapli.framework.infrastructure.repositories.impl.inmemory.InMemoryDomainRepository;

import java.util.Optional;

public class InMemoryAirTransportCompanyRepository
        extends InMemoryDomainRepository<AirTransportCompany, ICAOCode>
        implements AirTransportCompanyRepository {

    @Override
    public Optional<AirTransportCompany> findByIataCode(final IATACode iataCode) {
        return matchOne(company -> company.iataCode().equals(iataCode));
    }

    @Override
    public Optional<AirTransportCompany> findByIcaoCode(final ICAOCode icaoCode) {
        return matchOne(company -> company.icaoCode().equals(icaoCode));
    }

    @Override
    public Optional<AirTransportCompany> findByName(final String name) {
        return matchOne(company -> company.name().equalsIgnoreCase(name));
    }
}
