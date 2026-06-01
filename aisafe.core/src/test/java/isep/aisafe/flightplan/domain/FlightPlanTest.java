package isep.aisafe.flightplan.domain;

import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.weatherdata.domain.WeatherCoordinates;
import isep.aisafe.weatherdata.domain.WeatherData;
import isep.aisafe.weatherdata.domain.WeatherParameter;
import isep.aisafe.weatherdata.domain.WeatherProvider;
import isep.aisafe.weatherdata.domain.WeatherProviderType;
import isep.aisafe.weatherdata.domain.WeatherUnit;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FlightPlanTest {

    /**
     * Purpose: verify flight plan creation.
     * Scenario: create a flight plan with valid identity and pilot.
     * Expected output: the flight plan exposes the given identity and has no weather data.
     */
    @Test
    void ensureFlightPlanCanBeCreated() {
        // Arrange / Act
        final FlightPlan flightPlan = flightPlan();

        // Assert
        assertEquals(FlightPlanIdentifier.valueOf("TP123"), flightPlan.identity());
        assertEquals(PilotIdentifier.valueOf("pilot01"), flightPlan.pilotIdentifier());
        assertTrue(flightPlan.weatherDataIds().isEmpty());
    }

    /**
     * Purpose: verify weather data association.
     * Scenario: add existing weather data to a flight plan.
     * Expected output: the weather data identity is stored in the flight plan.
     */
    @Test
    void ensureWeatherDataCanBeAddedToFlightPlan() {
        // Arrange
        final FlightPlan flightPlan = flightPlan();
        final WeatherData weatherData = weatherData();

        // Act
        flightPlan.addWeatherData(weatherData);

        // Assert
        assertEquals(1, flightPlan.weatherDataIds().size());
        assertEquals(weatherData.identity(), flightPlan.weatherDataIds().get(0));
    }

    /**
     * Purpose: verify duplicate weather data handling.
     * Scenario: add the same weather data twice to the same flight plan.
     * Expected output: the weather data identity is only stored once.
     */
    @Test
    void ensureDuplicateWeatherDataIsNotAddedTwice() {
        // Arrange
        final FlightPlan flightPlan = flightPlan();
        final WeatherData weatherData = weatherData();

        // Act
        flightPlan.addWeatherData(weatherData);
        flightPlan.addWeatherData(weatherData);

        // Assert
        assertEquals(1, flightPlan.weatherDataIds().size());
    }

    /**
     * Purpose: verify required flight plan data validation.
     * Scenario: create flight plans or associations with missing data.
     * Expected output: invalid input is rejected.
     */
    @Test
    void ensureRequiredFlightPlanDataIsRejected() {
        // Arrange
        final FlightPlan flightPlan = flightPlan();

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> FlightPlanIdentifier.valueOf(null));
        assertThrows(IllegalArgumentException.class, () -> FlightPlanIdentifier.valueOf(" "));
        assertThrows(IllegalArgumentException.class, () -> PilotIdentifier.valueOf(null));
        assertThrows(IllegalArgumentException.class, () -> PilotIdentifier.valueOf(" "));
        assertThrows(IllegalArgumentException.class, () -> new FlightPlan(null, PilotIdentifier.valueOf("pilot01")));
        assertThrows(IllegalArgumentException.class, () -> new FlightPlan(FlightPlanIdentifier.valueOf("TP123"), null));
        assertThrows(IllegalArgumentException.class, () -> flightPlan.addWeatherData(null));
    }

    private FlightPlan flightPlan() {
        return new FlightPlan(
                FlightPlanIdentifier.valueOf("TP123"),
                PilotIdentifier.valueOf("pilot01")
        );
    }

    private WeatherData weatherData() {
        final WeatherData weatherData = new WeatherData(
                LocalDate.of(2027, 1, 10),
                AirControlAreaCode.valueOf("CONACC")
        );
        weatherData.addMeasurement(
                WeatherProvider.valueOf("IPMA", WeatherProviderType.valueOf("GROUND_SENSOR")),
                WeatherParameter.valueOf("TEMPERATURE"),
                18.5,
                WeatherUnit.valueOf("C"),
                1200,
                WeatherCoordinates.valueOf(38.72, -9.14)
        );
        return weatherData;
    }
}
