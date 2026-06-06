package isep.aisafe.weatherdata.domain;

import eapli.framework.domain.repositories.DomainRepository;

import java.util.Optional;

/**
 * Repository for registered weather providers.
 */
public interface WeatherProviderRepository extends DomainRepository<WeatherProviderId, WeatherProvider> {

    /**
     * Finds a weather provider by its identifier.
     *
     * @param identifier weather provider identifier
     * @return provider, if it exists
     */
    Optional<WeatherProvider> findByIdentifier(WeatherProviderId identifier);
}
