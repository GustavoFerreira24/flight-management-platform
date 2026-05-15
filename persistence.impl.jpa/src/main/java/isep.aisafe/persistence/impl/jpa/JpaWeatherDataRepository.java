package isep.aisafe.persistence.impl.jpa;

import eapli.framework.infrastructure.repositories.impl.jpa.JpaAutoTxRepository;
import isep.aisafe.Application;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.weatherdata.domain.WeatherData;
import isep.aisafe.weatherdata.domain.WeatherDataId;
import isep.aisafe.weatherdata.domain.WeatherDataRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * JPA implementation of the weather data repository.
 */
class JpaWeatherDataRepository
        extends JpaAutoTxRepository<WeatherData, WeatherDataId, WeatherDataId>
        implements WeatherDataRepository {

    JpaWeatherDataRepository(final String persistenceUnitName) {
        super(persistenceUnitName, Application.settings().extendedPersistenceProperties(), "id");
    }

    @Override
    public Optional<WeatherData> findByDayAndAirControlArea(
            final LocalDate day,
            final AirControlAreaCode airControlAreaCode) {

        final Map<String, Object> params = new HashMap<>();
        params.put("day", day);
        params.put("airControlAreaCode", airControlAreaCode);

        return matchOne("e.day=:day AND e.airControlAreaCode=:airControlAreaCode", params);
    }
}
