package isep.aisafe.weatherdata.domain;

import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class WeatherValueObjectsTest {

    /**
     * Purpose: verify weather data identity behavior.
     * Scenario: create identities with the same and different day/area values.
     * Expected output: identities compare by normalized composed value.
     */
    @Test
    void ensureWeatherDataIdUsesDayAndArea() {
        // Arrange
        final WeatherDataId first = WeatherDataId.valueOf(
                LocalDate.of(2026, 5, 14),
                AirControlAreaCode.valueOf("LISACC")
        );
        final WeatherDataId second = WeatherDataId.valueOf(
                LocalDate.of(2026, 5, 14),
                AirControlAreaCode.valueOf("lisacc")
        );
        final WeatherDataId third = WeatherDataId.valueOf(
                LocalDate.of(2026, 5, 15),
                AirControlAreaCode.valueOf("LISACC")
        );

        // Act / Assert
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, third);
        assertNotEquals(first, "not an id");
        assertTrue(first.compareTo(third) < 0);
        assertEquals("2026-05-14|LISACC", first.toString());
    }

    /**
     * Purpose: verify mandatory fields for weather data identity.
     * Scenario: create identities without day or air control area code.
     * Expected output: IllegalArgumentException is thrown.
     */
    @Test
    void ensureWeatherDataIdRequiresDayAndArea() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                WeatherDataId.valueOf(null, AirControlAreaCode.valueOf("LISACC"))
        );
        assertThrows(IllegalArgumentException.class, () ->
                WeatherDataId.valueOf(LocalDate.of(2026, 5, 14), null)
        );
    }

    /**
     * Purpose: verify weather coordinates behavior.
     * Scenario: create equal and different coordinate pairs.
     * Expected output: coordinates expose latitude/longitude and compare by value.
     */
    @Test
    void ensureWeatherCoordinatesUseLatitudeAndLongitude() {
        // Arrange
        final WeatherCoordinates first = WeatherCoordinates.valueOf(38.72, -9.14);
        final WeatherCoordinates second = WeatherCoordinates.valueOf(38.72, -9.14);
        final WeatherCoordinates third = WeatherCoordinates.valueOf(40.64, -8.65);

        // Act / Assert
        assertEquals(38.72, first.latitude());
        assertEquals(-9.14, first.longitude());
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, third);
        assertNotEquals(first, "not coordinates");
    }

    /**
     * Purpose: verify coordinate bounds.
     * Scenario: create coordinates outside latitude and longitude ranges.
     * Expected output: IllegalArgumentException is thrown.
     */
    @Test
    void ensureWeatherCoordinatesMustBeWithinValidBounds() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> WeatherCoordinates.valueOf(-91, 0));
        assertThrows(IllegalArgumentException.class, () -> WeatherCoordinates.valueOf(0, 181));
    }

    /**
     * Purpose: verify weather provider type normalization and equality.
     * Scenario: create provider types with extra whitespace and different casing.
     * Expected output: values are normalized and compare by value.
     */
    @Test
    void ensureWeatherProviderTypeIsNormalized() {
        // Arrange
        final WeatherProviderType first = WeatherProviderType.valueOf(" manual  input ");
        final WeatherProviderType second = WeatherProviderType.valueOf("MANUAL INPUT");
        final WeatherProviderType third = WeatherProviderType.valueOf("EXTERNAL");

        // Act / Assert
        assertEquals("MANUAL INPUT", first.toString());
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, third);
        assertNotEquals(first, "not a type");
        assertThrows(IllegalArgumentException.class, () -> WeatherProviderType.valueOf(" "));
    }

    /**
     * Purpose: verify weather provider behavior.
     * Scenario: create providers with normalized type and same/different names.
     * Expected output: provider exposes name/type and compares by value.
     */
    @Test
    void ensureWeatherProviderUsesNameAndType() {
        // Arrange
        final WeatherProviderType type = WeatherProviderType.valueOf("manual");
        final WeatherProvider first = WeatherProvider.valueOf(" Manual   Station ", type);
        final WeatherProvider second = WeatherProvider.valueOf("Manual Station", type);
        final WeatherProvider third = WeatherProvider.valueOf("Other Station", type);

        // Act / Assert
        assertEquals("Manual Station", first.name());
        assertEquals(type, first.type());
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, third);
        assertNotEquals(first, "not a provider");
        assertThrows(IllegalArgumentException.class, () -> WeatherProvider.valueOf(" ", type));
        assertThrows(IllegalArgumentException.class, () -> WeatherProvider.valueOf("Station", null));
    }

    /**
     * Purpose: verify weather unit normalization and equality.
     * Scenario: create units with different casing and invalid blank value.
     * Expected output: units normalize to uppercase and compare by value.
     */
    @Test
    void ensureWeatherUnitIsNormalized() {
        // Arrange
        final WeatherUnit first = WeatherUnit.valueOf(" hpa ");
        final WeatherUnit second = WeatherUnit.valueOf("HPA");
        final WeatherUnit third = WeatherUnit.valueOf("C");

        // Act / Assert
        assertEquals("HPA", first.toString());
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, third);
        assertNotEquals(first, "not a unit");
        assertThrows(IllegalArgumentException.class, () -> WeatherUnit.valueOf(null));
    }

    /**
     * Purpose: verify weather parameter normalization and unit compatibility.
     * Scenario: create parameters with spaces/hyphens and check accepted units.
     * Expected output: parameters normalize and validate units according to parameter type.
     */
    @Test
    void ensureWeatherParameterValidatesCompatibleUnits() {
        // Arrange
        final WeatherParameter windSpeed = WeatherParameter.valueOf("wind-speed");
        final WeatherParameter temperature = WeatherParameter.valueOf("temperature");

        // Act / Assert
        assertEquals("WIND_SPEED", windSpeed.name());
        assertEquals("WIND_SPEED", windSpeed.toString());
        assertTrue(windSpeed.isCompatibleWith(WeatherUnit.valueOf("M/S")));
        assertTrue(windSpeed.isCompatibleWith(WeatherUnit.valueOf("KT")));
        assertFalse(windSpeed.isCompatibleWith(WeatherUnit.valueOf("C")));
        assertFalse(windSpeed.isCompatibleWith(null));
        assertNotEquals(windSpeed, temperature);
        assertEquals(windSpeed, WeatherParameter.valueOf("wind speed"));
        assertEquals(windSpeed.hashCode(), WeatherParameter.valueOf("wind speed").hashCode());
        assertNotEquals(windSpeed, "not a parameter");
        assertThrows(IllegalArgumentException.class, () -> WeatherParameter.valueOf("cloudiness"));
        assertThrows(IllegalArgumentException.class, () -> WeatherParameter.valueOf(" "));
    }
}
