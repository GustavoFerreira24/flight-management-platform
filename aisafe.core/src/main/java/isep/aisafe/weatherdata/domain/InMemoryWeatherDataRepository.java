package isep.aisafe.weatherdata.domain;

import eapli.framework.infrastructure.repositories.impl.inmemory.InMemoryDomainRepository;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;

import java.time.LocalDate;
import java.util.Optional;

/**
 * In-memory implementation of the weather data repository.
 */
public class InMemoryWeatherDataRepository
        extends InMemoryDomainRepository<WeatherData, WeatherDataId>
        implements WeatherDataRepository {

    @Override
    public Optional<WeatherData> findByDayAndAirControlArea(
            final LocalDate day,
            final AirControlAreaCode airControlAreaCode) {

        return matchOne(weatherData -> weatherData.day().equals(day)
                && weatherData.airControlAreaCode().equals(airControlAreaCode));
    }
}
