package isep.aisafe.airtransportcompany.domain;

import eapli.framework.domain.repositories.DomainRepository;

import java.util.Optional;

public interface AirTransportCompanyRepository
        extends DomainRepository<ICAOCode, AirTransportCompany> {

    Optional<AirTransportCompany> findByIataCode(IATACode iataCode);

    Optional<AirTransportCompany> findByIcaoCode(ICAOCode icaoCode);

    Optional<AirTransportCompany> findByName(String name);
}
