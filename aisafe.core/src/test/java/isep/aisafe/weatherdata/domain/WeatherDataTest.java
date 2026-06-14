package isep.aisafe.weatherdata.domain;

import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests weather data aggregate and measurement domain behavior.
 */
class WeatherDataTest {

    /**
     * Purpose: verify successful weather data creation and measurement registration.
     * Scenario: create weather data for one day and area, then add a valid temperature measurement.
     * Expected output: the aggregate stores the day, area and one measurement.
     */
    @Test
    void ensureValidWeatherDataCanStoreMeasurement() {
        // Arrange
        final WeatherData weatherData = new WeatherData(
                LocalDate.of(2026, 5, 14),
                AirControlAreaCode.valueOf("LISACC")
        );

        // Act
        final WeatherProviderType providerType = WeatherProviderType.valueOf("Manual Input");
        weatherData.addMeasurement(
                provider(providerType),
                providerType,
                WeatherParameter.valueOf("temperature"),
                18.5,
                WeatherUnit.valueOf("C"),
                1200,
                WeatherCoordinates.valueOf(38.72, -9.14)
        );

        // Assert
        assertEquals(LocalDate.of(2026, 5, 14), weatherData.day());
        assertEquals(AirControlAreaCode.valueOf("LISACC"), weatherData.airControlAreaCode());
        assertEquals(1, weatherData.measurements().size());
        assertEquals("TEMPERATURE", weatherData.measurements().get(0).parameter().toString());
    }

    /**
     * Purpose: verify that weather data must be associated with one air control area.
     * Scenario: create weather data without an air control area code.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureAirControlAreaIsRequired() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                new WeatherData(LocalDate.of(2026, 5, 14), null)
        );
    }

    /**
     * Purpose: verify that a measurement source is mandatory.
     * Scenario: add a measurement without a provider.
     * Expected output: an IllegalArgumentException is thrown and no measurement is added.
     */
    @Test
    void ensureMeasurementProviderIsRequired() {
        // Arrange
        final WeatherData weatherData = weatherData();

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                weatherData.addMeasurement(
                        null,
                        WeatherProviderType.valueOf("Manual"),
                        WeatherParameter.valueOf("temperature"),
                        18.5,
                        WeatherUnit.valueOf("C"),
                        1200,
                        WeatherCoordinates.valueOf(38.72, -9.14)
                )
        );
        assertTrue(weatherData.measurements().isEmpty());
    }

    /**
     * Purpose: verify unit compatibility with weather parameters.
     * Scenario: register temperature using wind speed units.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureIncompatibleUnitIsRejected() {
        // Arrange
        final WeatherData weatherData = weatherData();

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                weatherData.addMeasurement(
                        provider(WeatherProviderType.valueOf("Manual")),
                        WeatherProviderType.valueOf("Manual"),
                        WeatherParameter.valueOf("temperature"),
                        18.5,
                        WeatherUnit.valueOf("M/S"),
                        1200,
                        WeatherCoordinates.valueOf(38.72, -9.14)
                )
        );
    }

    /**
     * Purpose: verify weather values are constrained by parameter type.
     * Scenario: register a negative wind speed.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureInvalidWindSpeedIsRejected() {
        // Arrange
        final WeatherData weatherData = weatherData();

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                weatherData.addMeasurement(
                        provider(WeatherProviderType.valueOf("Manual")),
                        WeatherProviderType.valueOf("Manual"),
                        WeatherParameter.valueOf("wind speed"),
                        -1,
                        WeatherUnit.valueOf("M/S"),
                        1200,
                        WeatherCoordinates.valueOf(38.72, -9.14)
                )
        );
    }

    /**
     * Purpose: verify parameter-specific value validation.
     * Scenario: register invalid temperature, wind direction, pressure and humidity values.
     * Expected output: each invalid value is rejected.
     */
    @Test
    void ensureInvalidParameterValuesAreRejected() {
        // Arrange
        final WeatherData weatherData = weatherData();
        final WeatherProviderType providerType = WeatherProviderType.valueOf("Manual");
        final WeatherProvider provider = provider(providerType);
        final WeatherCoordinates coordinates = WeatherCoordinates.valueOf(38.72, -9.14);

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> weatherData.addMeasurement(
                provider, providerType, WeatherParameter.valueOf("temperature"), 100, WeatherUnit.valueOf("C"), 1200, coordinates
        ));
        assertThrows(IllegalArgumentException.class, () -> weatherData.addMeasurement(
                provider, providerType, WeatherParameter.valueOf("wind direction"), 361, WeatherUnit.valueOf("DEG"), 1200, coordinates
        ));
        assertThrows(IllegalArgumentException.class, () -> weatherData.addMeasurement(
                provider, providerType, WeatherParameter.valueOf("pressure"), 700, WeatherUnit.valueOf("HPA"), 1200, coordinates
        ));
        assertThrows(IllegalArgumentException.class, () -> weatherData.addMeasurement(
                provider, providerType, WeatherParameter.valueOf("humidity"), -1, WeatherUnit.valueOf("PERCENT"), 1200, coordinates
        ));
    }

    /**
     * Purpose: verify mandatory measurement fields and altitude validation.
     * Scenario: add measurements with missing parameter/unit/coordinates or negative altitude.
     * Expected output: IllegalArgumentException is thrown for each invalid measurement.
     */
    @Test
    void ensureMeasurementRequiresMandatoryFieldsAndValidAltitude() {
        // Arrange
        final WeatherData weatherData = weatherData();
        final WeatherProviderType providerType = WeatherProviderType.valueOf("Manual");
        final WeatherProvider provider = provider(providerType);
        final WeatherParameter parameter = WeatherParameter.valueOf("temperature");
        final WeatherUnit unit = WeatherUnit.valueOf("C");
        final WeatherCoordinates coordinates = WeatherCoordinates.valueOf(38.72, -9.14);

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> weatherData.addMeasurement(
                provider, providerType, null, 18.5, unit, 1200, coordinates
        ));
        assertThrows(IllegalArgumentException.class, () -> weatherData.addMeasurement(
                provider, providerType, parameter, 18.5, null, 1200, coordinates
        ));
        assertThrows(IllegalArgumentException.class, () -> weatherData.addMeasurement(
                provider, providerType, parameter, 18.5, unit, 1200, null
        ));
        assertThrows(IllegalArgumentException.class, () -> weatherData.addMeasurement(
                provider, providerType, parameter, 18.5, unit, -1, coordinates
        ));
    }

    /**
     * Purpose: verify getters for weather measurements.
     * Scenario: add a valid pressure measurement.
     * Expected output: all measurement values are available through accessors.
     */
    @Test
    void ensureMeasurementExposesRegisteredValues() {
        // Arrange
        final WeatherData weatherData = weatherData();
        final WeatherProviderType providerType = WeatherProviderType.valueOf("Manual");
        final WeatherProvider provider = provider(providerType);
        final WeatherParameter parameter = WeatherParameter.valueOf("pressure");
        final WeatherUnit unit = WeatherUnit.valueOf("HPA");
        final WeatherCoordinates coordinates = WeatherCoordinates.valueOf(38.72, -9.14);

        // Act
        weatherData.addMeasurement(provider, providerType, parameter, 1013, unit, 1200, coordinates);
        final WeatherMeasurement measurement = weatherData.measurements().get(0);

        // Assert
        assertEquals(provider, measurement.provider());
        assertEquals(providerType, measurement.providerType());
        assertEquals(parameter, measurement.parameter());
        assertEquals(1013, measurement.value());
        assertEquals(unit, measurement.unit());
        assertEquals(1200, measurement.altitude());
        assertEquals(coordinates, measurement.coordinates());
    }

    /**
     * Purpose: verify measurement coordinates are valid.
     * Scenario: create coordinates outside the latitude range.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureInvalidCoordinatesAreRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> WeatherCoordinates.valueOf(91, -9.14));
    }

    /**
     * Purpose: verify that weather data requires a day.
     * Scenario: create weather data without a day.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureDayIsRequired() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                new WeatherData(null, AirControlAreaCode.valueOf("LISACC"))
        );
    }

    /**
     * Purpose: verify aggregate identity comparison.
     * Scenario: compare weather data records by day and air control area.
     * Expected output: sameAs returns true only for records with the same identity.
     */
    @Test
    void ensureSameAsUsesDayAndAirControlAreaIdentity() {
        // Arrange
        final WeatherData first = new WeatherData(
                LocalDate.of(2026, 5, 14),
                AirControlAreaCode.valueOf("LISACC")
        );
        final WeatherData second = new WeatherData(
                LocalDate.of(2026, 5, 14),
                AirControlAreaCode.valueOf("LISACC")
        );
        final WeatherData third = new WeatherData(
                LocalDate.of(2026, 5, 15),
                AirControlAreaCode.valueOf("LISACC")
        );

        // Act / Assert
        assertTrue(first.sameAs(second));
        assertFalse(first.sameAs(third));
        assertFalse(first.sameAs("not weather data"));
    }

    /**
     * Purpose: verify persistence constructors remain available.
     * Scenario: instantiate weather aggregate and measurement through protected no-argument constructors.
     * Expected output: instances can be created for JPA without using domain factory methods.
     */
    @Test
    void ensureWeatherPersistenceConstructorsAreAvailable() {
        // Arrange / Act / Assert
        assertNotNull(new WeatherData());
        assertNotNull(new WeatherMeasurement());
    }

    private WeatherData weatherData() {
        return new WeatherData(
                LocalDate.of(2026, 5, 14),
                AirControlAreaCode.valueOf("LISACC")
        );
    }

    private WeatherProvider provider(final WeatherProviderType providerType) {
        return new WeatherProvider("Manual", java.util.List.of(providerType));
    }
}
