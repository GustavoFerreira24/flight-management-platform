package isep.aisafe.airport.domain;

import eapli.framework.infrastructure.repositories.impl.inmemory.InMemoryDomainRepository;

import java.util.Optional;

/**
 * Implementação em memória do repositório de aeroportos para efeitos de teste.
 */
public class InMemoryAirportRepository
        extends InMemoryDomainRepository<Airport, AirportICAOCode>
        implements AirportRepository {

    @Override
    public Optional<Airport> findByIataCode(AirportIATACode iataCode) {
        // Percorre a coleção em memória e encontra o primeiro que coincida com o IATA
        return matchOne(airport -> airport.iataCode().equals(iataCode));
    }

    @Override
    public Optional<Airport> findByIcaoCode(AirportICAOCode icaoCode) {
        // O ICAO é a identidade (ID), por isso usamos o ofIdentity
        return ofIdentity(icaoCode);
    }
}