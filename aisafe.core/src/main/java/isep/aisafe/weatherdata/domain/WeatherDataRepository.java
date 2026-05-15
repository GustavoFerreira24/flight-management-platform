package isep.aisafe.weatherdata.domain;

import eapli.framework.domain.repositories.DomainRepository;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Repository for weather data aggregates.
 */
public interface WeatherDataRepository extends DomainRepository<WeatherDataId, WeatherData> {

    Optional<WeatherData> findByDayAndAirControlArea(LocalDate day, AirControlAreaCode airControlAreaCode);
}
