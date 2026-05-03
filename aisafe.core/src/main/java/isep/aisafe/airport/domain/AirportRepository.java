package isep.aisafe.airport.domain;

import eapli.framework.domain.repositories.DomainRepository;
import isep.aisafe.ICAOCode;

public interface AirportRepository extends DomainRepository<ICAOCode, Airport> {
}