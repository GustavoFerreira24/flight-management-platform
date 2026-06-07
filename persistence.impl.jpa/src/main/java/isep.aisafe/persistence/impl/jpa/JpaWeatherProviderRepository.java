package isep.aisafe.persistence.impl.jpa;

import eapli.framework.domain.repositories.TransactionalContext;
import eapli.framework.infrastructure.repositories.impl.jpa.JpaAutoTxRepository;
import isep.aisafe.Application;
import isep.aisafe.weatherdata.domain.WeatherProvider;
import isep.aisafe.weatherdata.domain.WeatherProviderId;
import isep.aisafe.weatherdata.domain.WeatherProviderRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * JPA implementation of the weather provider repository.
 */
class JpaWeatherProviderRepository
        extends JpaAutoTxRepository<WeatherProvider, WeatherProviderId, WeatherProviderId>
        implements WeatherProviderRepository {

    JpaWeatherProviderRepository(final TransactionalContext autoTx) {
        super(autoTx, "identifier");
    }

    JpaWeatherProviderRepository(final String persistenceUnitName) {
        super(persistenceUnitName, Application.settings().extendedPersistenceProperties(), "identifier");
    }

    @Override
    public Optional<WeatherProvider> findByIdentifier(final WeatherProviderId identifier) {
        final Map<String, Object> params = new HashMap<>();
        params.put("identifier", identifier);

        return matchOne("e.identifier=:identifier", params);
    }
}
