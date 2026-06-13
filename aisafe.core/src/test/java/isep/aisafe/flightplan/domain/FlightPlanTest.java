package isep.aisafe.flightplan.domain;

import eapli.framework.general.domain.model.EmailAddress;
import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.aircontrolarea.domain.FuelUnit;
import isep.aisafe.aircontrolarea.domain.GeographicBoundary;
import isep.aisafe.aircontrolarea.domain.MinimumFuelRequirement;
import isep.aisafe.aircraft.domain.Aircraft;
import isep.aisafe.aircraft.domain.AircraftOperationalStatus;
import isep.aisafe.aircraft.domain.AircraftRegistration;
import isep.aisafe.aircraft.domain.AircraftRegistrationCountry;
import isep.aisafe.aircraft.domain.CabinConfiguration;
import isep.aisafe.aircraftmodel.domain.AircraftModel;
import isep.aisafe.aircraftmodel.domain.AircraftModelDesignator;
import isep.aisafe.aircraftmodel.domain.AircraftModelMotorization;
import isep.aisafe.aircraftmodel.domain.AircraftModelType;
import isep.aisafe.airport.domain.Airport;
import isep.aisafe.airport.domain.AirportICAOCode;
import isep.aisafe.airport.domain.AirportIATACode;
import isep.aisafe.airport.domain.Elevation;
import isep.aisafe.airport.domain.GpsCoordinates;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.airtransportcompany.domain.Country;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.airtransportcompany.domain.ICAOCode;
import isep.aisafe.enginemodel.domain.EngineModel;
import isep.aisafe.enginemodel.domain.EngineModelDesignator;
import isep.aisafe.enginemodel.domain.EngineType;
import isep.aisafe.enginemodel.domain.FuelType;
import isep.aisafe.flight.domain.Flight;
import isep.aisafe.flight.domain.FlightDesignator;
import isep.aisafe.flightroute.domain.FlightRoute;
import isep.aisafe.flightroute.domain.RouteDesignator;
import isep.aisafe.pilot.domain.*;
import isep.aisafe.weatherdata.domain.WeatherCoordinates;
import isep.aisafe.weatherdata.domain.WeatherData;
import isep.aisafe.weatherdata.domain.WeatherParameter;
import isep.aisafe.weatherdata.domain.WeatherProvider;
import isep.aisafe.weatherdata.domain.WeatherProviderType;
import isep.aisafe.weatherdata.domain.WeatherUnit;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FlightPlanTest {

    /**
     * Purpose: verify flight plan creation.
     * Scenario: create a flight plan with valid identity and pilot.
     * Expected output: the flight plan exposes the given identity and has no weather data.
     */
    @Test
    void ensureFlightPlanCanBeCreated() {
        // Arrange
        final Flight flight = flight();
        final Aircraft aircraft = aircraft();
        final Pilot pilot = pilot();
        final FuelQuantity fuelQuantity = fuelQuantity();

        // Act
        final FlightPlan flightPlan = new FlightPlan(
                FlightPlanIdentifier.valueOf("FP001"),
                flight,
                aircraft,
                pilot,
                fuelQuantity
        );

        // Assert
        assertEquals(FlightPlanIdentifier.valueOf("FP001"), flightPlan.identity());
        assertSame(flight, flightPlan.flight());
        assertSame(aircraft, flightPlan.aircraft());
        assertSame(pilot, flightPlan.pilot());
        assertEquals(FlightPlanStatus.DRAFT, flightPlan.status());
        assertEquals(fuelQuantity, flightPlan.fuelQuantity());
        assertTrue(flightPlan.weatherDataIds().isEmpty());
        assertTrue(flightPlan.canBeTested());
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
        assertEquals(FlightPlanStatus.DRAFT, flightPlan.status());
    }

    /**
     * Purpose: verify flight plan validation.
     * Scenario: validate a draft flight plan.
     * Expected output: the flight plan exposes the validated status.
     */
    @Test
    void ensureFlightPlanCanBeValidated() {
        // Arrange
        final FlightPlan flightPlan = flightPlan();

        // Act
        flightPlan.validate();

        // Assert
        assertEquals(FlightPlanStatus.VALIDATED, flightPlan.status());
        assertFalse(flightPlan.canBeTested());
    }

    /**
     * Purpose: verify flight plan rejection.
     * Scenario: reject a draft flight plan.
     * Expected output: the flight plan exposes the rejected status.
     */
    @Test
    void ensureFlightPlanCanBeRejected() {
        // Arrange
        final FlightPlan flightPlan = flightPlan();

        // Act
        flightPlan.reject();

        // Assert
        assertEquals(FlightPlanStatus.REJECTED, flightPlan.status());
        assertFalse(flightPlan.canBeTested());
    }

    /**
     * Purpose: verify previous validation invalidation when weather data changes.
     * Scenario: add new weather data to a previously validated flight plan.
     * Expected output: the flight plan returns to draft because validation is no longer current.
     */
    @Test
    void ensureAddingWeatherDataInvalidatesPreviousValidation() {
        // Arrange
        final FlightPlan flightPlan = flightPlan();
        flightPlan.validate();

        // Act
        flightPlan.addWeatherData(weatherData());

        // Assert
        assertEquals(FlightPlanStatus.DRAFT, flightPlan.status());
        assertTrue(flightPlan.canBeTested());
    }

    /**
     * Purpose: verify previous rejection invalidation when weather data changes.
     * Scenario: add new weather data to a previously rejected flight plan.
     * Expected output: the flight plan returns to draft because the previous test result is no longer current.
     */
    @Test
    void ensureAddingWeatherDataInvalidatesPreviousRejection() {
        // Arrange
        final FlightPlan flightPlan = flightPlan();
        flightPlan.reject();

        // Act
        flightPlan.addWeatherData(weatherData());

        // Assert
        assertEquals(FlightPlanStatus.DRAFT, flightPlan.status());
        assertTrue(flightPlan.canBeTested());
    }

    /**
     * Purpose: verify duplicate weather data does not invalidate an unchanged flight plan.
     * Scenario: add already associated weather data to a validated flight plan.
     * Expected output: the status remains validated because no weather data was added.
     */
    @Test
    void ensureDuplicateWeatherDataDoesNotInvalidatePreviousValidation() {
        // Arrange
        final FlightPlan flightPlan = flightPlan();
        final WeatherData weatherData = weatherData();
        flightPlan.addWeatherData(weatherData);
        flightPlan.validate();

        // Act
        flightPlan.addWeatherData(weatherData);

        // Assert
        assertEquals(FlightPlanStatus.VALIDATED, flightPlan.status());
        assertEquals(1, flightPlan.weatherDataIds().size());
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
     * Purpose: verify validation state transitions.
     * Scenario: move a draft flight plan to under validation and then validate it.
     * Expected output: the flight plan becomes validated.
     */
    @Test
    void ensureFlightPlanCanMoveFromUnderValidationToValidated() {
        // Arrange
        final FlightPlan flightPlan = flightPlan();

        // Act
        flightPlan.markUnderValidation();
        flightPlan.validate();

        // Assert
        assertEquals(FlightPlanStatus.VALIDATED, flightPlan.status());
        assertFalse(flightPlan.canBeTested());
    }

    /**
     * Purpose: verify invalid lifecycle transitions.
     * Scenario: try to validate or reject flight plans already in final states.
     * Expected output: invalid transitions are rejected.
     */
    @Test
    void ensureInvalidLifecycleTransitionsAreRejected() {
        // Arrange
        final FlightPlan validatedFlightPlan = flightPlan();
        final FlightPlan rejectedFlightPlan = flightPlan();

        validatedFlightPlan.validate();
        rejectedFlightPlan.reject();

        // Act / Assert
        assertThrows(IllegalArgumentException.class, validatedFlightPlan::validate);
        assertThrows(IllegalArgumentException.class, validatedFlightPlan::reject);
        assertThrows(IllegalArgumentException.class, rejectedFlightPlan::validate);
        assertThrows(IllegalArgumentException.class, rejectedFlightPlan::reject);
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
        assertThrows(IllegalArgumentException.class, () -> FuelQuantity.valueOf(0, FuelUnit.valueOf("KG")));
        assertThrows(IllegalArgumentException.class, () -> FuelQuantity.valueOf(-1, FuelUnit.valueOf("KG")));
        assertThrows(IllegalArgumentException.class, () -> FuelQuantity.valueOf(1, null));
        assertThrows(IllegalArgumentException.class, () -> new FlightPlan(
                null,
                flight(),
                aircraft(),
                pilot(),
                fuelQuantity()
        ));
        assertThrows(IllegalArgumentException.class, () -> new FlightPlan(
                FlightPlanIdentifier.valueOf("FP001"),
                null,
                aircraft(),
                pilot(),
                fuelQuantity()
        ));
        assertThrows(IllegalArgumentException.class, () -> new FlightPlan(
                FlightPlanIdentifier.valueOf("FP001"),
                flight(),
                null,
                pilot(),
                fuelQuantity()
        ));
        assertThrows(IllegalArgumentException.class, () -> new FlightPlan(
                FlightPlanIdentifier.valueOf("FP001"),
                flight(),
                aircraft(),
                null,
                fuelQuantity()
        ));
        assertThrows(IllegalArgumentException.class, () -> new FlightPlan(
                FlightPlanIdentifier.valueOf("FP001"),
                flight(),
                aircraft(),
                pilot(),
                null
        ));
        assertThrows(IllegalArgumentException.class, () -> flightPlan.addWeatherData(null));
    }

    /**
     * Purpose: verify aircraft assignment validation.
     * Scenario: create a flight plan with a decommissioned aircraft.
     * Expected output: the flight plan is rejected.
     */
    @Test
    void ensureFlightPlanRejectsAircraftThatCannotBeUsedForFlights() {
        // Arrange
        final Aircraft aircraft = aircraft();
        aircraft.decommission();

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new FlightPlan(
                FlightPlanIdentifier.valueOf("FP001"),
                flight(),
                aircraft,
                pilot(),
                fuelQuantity()
        ));
    }

    /**
     * Purpose: verify aircraft company consistency.
     * Scenario: create a flight plan with an aircraft from another air transport company.
     * Expected output: the flight plan is rejected.
     */
    @Test
    void ensureFlightPlanRejectsAircraftFromDifferentCompany() {
        // Arrange
        final Aircraft aircraft = aircraft(company("Ryanair", "Ireland", "FR", "RYR"));

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new FlightPlan(
                FlightPlanIdentifier.valueOf("FP001"),
                flight(),
                aircraft,
                pilot(),
                fuelQuantity()
        ));
    }

    /**
     * Purpose: verify pilot company consistency.
     * Scenario: create a flight plan with a pilot from another air transport company.
     * Expected output: the flight plan is rejected.
     */
    @Test
    void ensureFlightPlanRejectsPilotFromDifferentCompany() {
        // Arrange
        final Pilot pilot = pilot("FR");

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new FlightPlan(
                FlightPlanIdentifier.valueOf("FP001"),
                flight(),
                aircraft(),
                pilot,
                fuelQuantity()
        ));
    }

    /**
     * Purpose: verify pilot status validation.
     * Scenario: create a flight plan with an inactive pilot.
     * Expected output: the flight plan is rejected.
     */
    @Test
    void ensureFlightPlanRejectsInactivePilot() {
        // Arrange
        final Pilot pilot = inactivePilot();

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new FlightPlan(
                FlightPlanIdentifier.valueOf("FP001"),
                flight(),
                aircraft(),
                pilot,
                fuelQuantity()
        ));
    }

    private FlightPlan flightPlan() {
        return new FlightPlan(
                FlightPlanIdentifier.valueOf("FP001"),
                flight(),
                aircraft(),
                pilot(),
                fuelQuantity()
        );
    }

    private Flight flight() {
        return new Flight(
                FlightDesignator.valueOf("TP123"),
                route(),
                isep.aisafe.flight.domain.Departure.valueOf(LocalDateTime.of(2027, 1, 10, 10, 0))
        );
    }

    private FlightRoute route() {
        return new FlightRoute(
                RouteDesignator.valueOf("TP123"),
                company(),
                airport("LPPT", "LIS", "Lisbon Airport", "Lisbon"),
                airport("LPPR", "OPO", "Porto Airport", "Porto")
        );
    }

    private Aircraft aircraft() {
        return aircraft(company());
    }

    private Aircraft aircraft(final AirTransportCompany company) {
        final EngineModel engineModel = engineModel();

        return new Aircraft(
                AircraftRegistration.valueOf("CS-TXA"),
                AircraftRegistrationCountry.valueOf("Portugal"),
                CabinConfiguration.valueOf(100, 10, 0),
                AircraftOperationalStatus.active(),
                6,
                company,
                aircraftModel(engineModel),
                engineModel
        );
    }

    private Pilot pilot() {
        return pilot("TP");
    }

    private Pilot pilot(final String companyIataCode) {
        final Certification certification = new Certification("01-01-2026", "01-01-2028");

        return new Pilot(
                PilotID.valueOf("ABC"),
                certification,
                PilotStatus.valueOf(certification),
                IATACode.valueOf(companyIataCode),
                EmailAddress.valueOf("pilot@example.com"),
                PhoneNumber.valueOf("+351912345678"),
                "Ben",
                "Dover",
                Set.of(AircraftModelDesignator.valueOf("A320"))
        );
    }

    private Pilot inactivePilot() {
        final Certification certification = new Certification("01-01-2020", "01-01-2021");

        return new Pilot(
                PilotID.valueOf("DEF"),
                certification,
                PilotStatus.valueOf(certification),
                IATACode.valueOf("TP"),
                EmailAddress.valueOf("pilot@example.com"),
                PhoneNumber.valueOf("+351912345678"),
                "Ben",
                "Dover",
                Set.of(AircraftModelDesignator.valueOf("A320"))
        );
    }

    private FuelQuantity fuelQuantity() {
        return FuelQuantity.valueOf(5000, FuelUnit.valueOf("KG"));
    }

    private Airport airport(
            final String icao,
            final String iata,
            final String name,
            final String town) {

        return new Airport(
                AirportICAOCode.valueOf(icao),
                AirportIATACode.valueOf(iata),
                name,
                Country.valueOf("Portugal"),
                town,
                new GpsCoordinates(38.7, -9.1),
                new Elevation(114.0),
                airControlArea(icao, name)
        );
    }

    private AirControlArea airControlArea(final String icao, final String name) {
        return new AirControlArea(
                AirControlAreaCode.valueOf(icao + "FIR"),
                name + " Control",
                GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0),
                MinimumFuelRequirement.valueOf(5000.0, FuelUnit.valueOf("KG"))
        );
    }

    private AirTransportCompany company() {
        return company("TAP Air Portugal", "Portugal", "TP", "TAP");
    }

    private AirTransportCompany company(
            final String name,
            final String country,
            final String iata,
            final String icao) {

        return new AirTransportCompany(
                name,
                Country.valueOf(country),
                IATACode.valueOf(iata),
                ICAOCode.valueOf(icao)
        );
    }

    private EngineModel engineModel() {
        return new EngineModel(
                EngineModelDesignator.valueOf("LEAP-X", "CFM International"),
                EngineType.valueOf("TURBOFAN"),
                FuelType.valueOf("JET A-1"),
                120.0,
                140.0,
                0.60
        );
    }

    private AircraftModel aircraftModel(final EngineModel certifiedEngineModel) {
        return new AircraftModel(
                AircraftModelDesignator.valueOf("A320", "Airbus"),
                AircraftModelType.valueOf("PASSENGER"),
                AircraftModelMotorization.valueOf("TURBOFAN"),
                6300,
                44500,
                79000,
                64500,
                27200,
                12000,
                833,
                122.6,
                0.024,
                0.62,
                180,
                List.of(certifiedEngineModel)
        );
    }

    private WeatherData weatherData() {
        final WeatherData weatherData = new WeatherData(
                LocalDate.of(2027, 1, 10),
                AirControlAreaCode.valueOf("CONACC")
        );

        final WeatherProviderType providerType = WeatherProviderType.valueOf("GROUND_SENSOR");

        weatherData.addMeasurement(
                new WeatherProvider("IPMA", List.of(providerType)),
                providerType,
                WeatherParameter.valueOf("TEMPERATURE"),
                18.5,
                WeatherUnit.valueOf("C"),
                1200,
                WeatherCoordinates.valueOf(38.72, -9.14)
        );

        return weatherData;
    }
}
