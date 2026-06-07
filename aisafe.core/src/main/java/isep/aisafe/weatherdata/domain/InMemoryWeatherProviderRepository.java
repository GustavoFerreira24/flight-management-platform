package isep.aisafe.weatherdata.domain;

import eapli.framework.infrastructure.repositories.impl.inmemory.InMemoryDomainRepository;

import java.util.Optional;

/**
 * In-memory implementation of the weather provider repository.
 */
public class InMemoryWeatherProviderRepository
        extends InMemoryDomainRepository<WeatherProvider, WeatherProviderId>
        implements WeatherProviderRepository {

    /**
     * Finds a weather provider by its identifier.
     *
     * @param identifier weather provider identifier
     * @return provider, if it exists
     */
    @Override
    public Optional<WeatherProvider> findByIdentifier(final WeatherProviderId identifier) {
        return matchOne(provider -> provider.identity().equals(identifier));
    }
}
