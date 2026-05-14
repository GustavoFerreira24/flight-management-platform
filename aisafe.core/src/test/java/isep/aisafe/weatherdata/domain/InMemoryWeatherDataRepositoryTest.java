package isep.aisafe.weatherdata.domain;

import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryWeatherDataRepositoryTest {

    /**
     * Purpose: verify weather data lookup by day and air control area.
     * Scenario: save weather data and search using the same day and area code.
     * Expected output: the weather data aggregate is found.
     */
    @Test
    void ensureWeatherDataCanBeFoundByDayAndAirControlArea() {
        // Arrange
        final InMemoryWeatherDataRepository repository = new InMemoryWeatherDataRepository();
        final LocalDate day = LocalDate.of(2026, 5, 14);
        final AirControlAreaCode areaCode = AirControlAreaCode.valueOf("LISACC");
        final WeatherData weatherData = new WeatherData(day, areaCode);

        // Act
        repository.save(weatherData);

        // Assert
        assertTrue(repository.findByDayAndAirControlArea(day, areaCode).isPresent());
        assertTrue(repository.findByDayAndAirControlArea(
                LocalDate.of(2026, 5, 15),
                areaCode
        ).isEmpty());
    }
}
