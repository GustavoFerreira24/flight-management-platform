package isep.aisafe.aircraft.domain;

import isep.aisafe.aircraftmodel.domain.AircraftModel;
import isep.aisafe.aircraftmodel.domain.AircraftModelDesignator;
import isep.aisafe.aircraftmodel.domain.AircraftModelMotorization;
import isep.aisafe.aircraftmodel.domain.AircraftModelType;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.airtransportcompany.domain.Country;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.airtransportcompany.domain.ICAOCode;
import isep.aisafe.enginemodel.domain.EngineModel;
import isep.aisafe.enginemodel.domain.EngineModelDesignator;
import isep.aisafe.enginemodel.domain.EngineType;
import isep.aisafe.enginemodel.domain.FuelType;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AircraftTest {

    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final Aircraft aircraft = new Aircraft();

        assertNotNull(aircraft);
    }

    @Test
    void ensureValidAircraftIsAccepted() {
        final EngineModel engineModel = engineModel("LEAP-1A", "CFM International");
        final AircraftModel aircraftModel = aircraftModel("A320neo", "Airbus", 186, engineModel);
        final AirTransportCompany company = company("TAP Air Portugal", "Portugal", "TP", "TAP");

        final Aircraft aircraft = new Aircraft(
                AircraftRegistration.valueOf("CS-TVA"),
                AircraftRegistrationCountry.valueOf("Portugal"),
                CabinConfiguration.valueOf(150, 12, 0),
                AircraftOperationalStatus.active(),
                6,
                company,
                aircraftModel,
                engineModel
        );

        assertEquals(AircraftRegistration.valueOf("CS-TVA"), aircraft.identity());
        assertEquals(AircraftRegistration.valueOf("CS-TVA"), aircraft.registration());
        assertEquals(AircraftRegistrationCountry.valueOf("Portugal"), aircraft.registrationCountry());
        assertEquals(CabinConfiguration.valueOf(150, 12, 0), aircraft.cabinConfiguration());
        assertEquals(AircraftOperationalStatus.active(), aircraft.operationalStatus());
        assertEquals(6, aircraft.crewSize());
        assertEquals(Year.now().getValue(), aircraft.manufactureYear());
        assertEquals(0, aircraft.ageInYears());
        assertEquals(company, aircraft.company());
        assertEquals(aircraftModel, aircraft.aircraftModel());
        assertEquals(engineModel, aircraft.engineModel());
    }

    @Test
    void ensureAircraftManufactureYearIsAccepted() {
        final EngineModel engineModel = engineModel("LEAP-AGE", "CFM International");
        final AircraftModel aircraftModel = aircraftModel("A320age", "Airbus", 186, engineModel);
        final int manufactureYear = Year.now().getValue() - 8;

        final Aircraft aircraft = new Aircraft(
                AircraftRegistration.valueOf("CS-AGE"),
                AircraftRegistrationCountry.valueOf("Portugal"),
                CabinConfiguration.valueOf(150, 12, 0),
                AircraftOperationalStatus.active(),
                6,
                company("TAP Air Portugal", "Portugal", "TP", "TAP"),
                aircraftModel,
                engineModel,
                manufactureYear
        );

        assertEquals(manufactureYear, aircraft.manufactureYear());
        assertEquals(8, aircraft.ageInYears());
    }

    @Test
    void ensureInvalidManufactureYearIsRejected() {
        final EngineModel engineModel = engineModel("LEAP-YEAR", "CFM International");
        final AircraftModel aircraftModel = aircraftModel("A320year", "Airbus", 186, engineModel);

        assertThrows(IllegalArgumentException.class, () -> new Aircraft(
                AircraftRegistration.valueOf("CS-Y01"),
                AircraftRegistrationCountry.valueOf("Portugal"),
                CabinConfiguration.valueOf(150, 12, 0),
                AircraftOperationalStatus.active(),
                6,
                company("TAP Air Portugal", "Portugal", "TP", "TAP"),
                aircraftModel,
                engineModel,
                1902
        ));

        assertThrows(IllegalArgumentException.class, () -> new Aircraft(
                AircraftRegistration.valueOf("CS-Y02"),
                AircraftRegistrationCountry.valueOf("Portugal"),
                CabinConfiguration.valueOf(150, 12, 0),
                AircraftOperationalStatus.active(),
                6,
                company("TAP Air Portugal", "Portugal", "TP", "TAP"),
                aircraftModel,
                engineModel,
                Year.now().getValue() + 1
        ));
    }

    @Test
    void ensureNullRegistrationIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Aircraft(
                null,
                AircraftRegistrationCountry.valueOf("Portugal"),
                CabinConfiguration.valueOf(150, 12, 0),
                AircraftOperationalStatus.active(),
                6,
                company("TAP Air Portugal", "Portugal", "TP", "TAP"),
                aircraftModel("A320neo", "Airbus", 186, engineModel("LEAP-1A", "CFM International")),
                engineModel("LEAP-1A", "CFM International")
        ));
    }

    @Test
    void ensureNullRegistrationCountryIsRejected() {
        final EngineModel engineModel = engineModel("LEAP-1B", "CFM International");
        final AircraftModel aircraftModel = aircraftModel("B737 MAX", "Boeing", 180, engineModel);

        assertThrows(IllegalArgumentException.class, () -> new Aircraft(
                AircraftRegistration.valueOf("CS-TVB"),
                null,
                CabinConfiguration.valueOf(150, 12, 0),
                AircraftOperationalStatus.active(),
                6,
                company("TAP Air Portugal", "Portugal", "TP", "TAP"),
                aircraftModel,
                engineModel
        ));
    }

    @Test
    void ensureNullCabinConfigurationIsRejected() {
        final EngineModel engineModel = engineModel("LEAP-1C", "CFM International");
        final AircraftModel aircraftModel = aircraftModel("C919", "Comac", 180, engineModel);

        assertThrows(IllegalArgumentException.class, () -> new Aircraft(
                AircraftRegistration.valueOf("CS-TVC"),
                AircraftRegistrationCountry.valueOf("Portugal"),
                null,
                AircraftOperationalStatus.active(),
                6,
                company("TAP Air Portugal", "Portugal", "TP", "TAP"),
                aircraftModel,
                engineModel
        ));
    }

    @Test
    void ensureNullOperationalStatusIsRejected() {
        final EngineModel engineModel = engineModel("TRENT700", "Rolls Royce");
        final AircraftModel aircraftModel = aircraftModel("A330", "Airbus", 250, engineModel);

        assertThrows(IllegalArgumentException.class, () -> new Aircraft(
                AircraftRegistration.valueOf("CS-TVD"),
                AircraftRegistrationCountry.valueOf("Portugal"),
                CabinConfiguration.valueOf(200, 20, 0),
                null,
                8,
                company("TAP Air Portugal", "Portugal", "TP", "TAP"),
                aircraftModel,
                engineModel
        ));
    }

    @Test
    void ensureInvalidCrewSizeIsRejected() {
        final EngineModel engineModel = engineModel("GE90", "GE Aerospace");
        final AircraftModel aircraftModel = aircraftModel("B777", "Boeing", 300, engineModel);

        assertThrows(IllegalArgumentException.class, () -> new Aircraft(
                AircraftRegistration.valueOf("CS-TVE"),
                AircraftRegistrationCountry.valueOf("Portugal"),
                CabinConfiguration.valueOf(250, 30, 0),
                AircraftOperationalStatus.active(),
                0,
                company("TAP Air Portugal", "Portugal", "TP", "TAP"),
                aircraftModel,
                engineModel
        ));
    }

    @Test
    void ensureNullCompanyIsRejected() {
        final EngineModel engineModel = engineModel("PW1100G", "Pratt Whitney");
        final AircraftModel aircraftModel = aircraftModel("A220", "Airbus", 140, engineModel);

        assertThrows(IllegalArgumentException.class, () -> new Aircraft(
                AircraftRegistration.valueOf("CS-TVF"),
                AircraftRegistrationCountry.valueOf("Portugal"),
                CabinConfiguration.valueOf(120, 10, 0),
                AircraftOperationalStatus.active(),
                5,
                null,
                aircraftModel,
                engineModel
        ));
    }

    @Test
    void ensureNullAircraftModelIsRejected() {
        final EngineModel engineModel = engineModel("PW1500G", "Pratt Whitney");

        assertThrows(IllegalArgumentException.class, () -> new Aircraft(
                AircraftRegistration.valueOf("CS-TVH"),
                AircraftRegistrationCountry.valueOf("Portugal"),
                CabinConfiguration.valueOf(120, 10, 0),
                AircraftOperationalStatus.active(),
                5,
                company("TAP Air Portugal", "Portugal", "TP", "TAP"),
                null,
                engineModel
        ));
    }

    @Test
    void ensureNullEngineModelIsRejected() {
        final EngineModel engineModel = engineModel("CF34", "GE Aerospace");
        final AircraftModel aircraftModel = aircraftModel("E190", "Embraer", 110, engineModel);

        assertThrows(IllegalArgumentException.class, () -> new Aircraft(
                AircraftRegistration.valueOf("CS-TVI"),
                AircraftRegistrationCountry.valueOf("Portugal"),
                CabinConfiguration.valueOf(100, 8, 0),
                AircraftOperationalStatus.active(),
                4,
                company("TAP Air Portugal", "Portugal", "TP", "TAP"),
                aircraftModel,
                null
        ));
    }

    @Test
    void ensureTotalSeatsCannotExceedModelCapacity() {
        final EngineModel engineModel = engineModel("LEAP-X1", "CFM International");
        final AircraftModel aircraftModel = aircraftModel("A321neo", "Airbus", 100, engineModel);

        assertThrows(IllegalArgumentException.class, () -> new Aircraft(
                AircraftRegistration.valueOf("CS-TVJ"),
                AircraftRegistrationCountry.valueOf("Portugal"),
                CabinConfiguration.valueOf(101, 0, 0),
                AircraftOperationalStatus.active(),
                6,
                company("TAP Air Portugal", "Portugal", "TP", "TAP"),
                aircraftModel,
                engineModel
        ));
    }

    @Test
    void ensureEngineModelMustBeCertifiedForAircraftModel() {
        final EngineModel certifiedEngine = engineModel("LEAP-X2", "CFM International");
        final EngineModel nonCertifiedEngine = engineModel("PW-X2", "Pratt Whitney");
        final AircraftModel aircraftModel = aircraftModel("A319neo", "Airbus", 150, certifiedEngine);

        assertThrows(IllegalArgumentException.class, () -> new Aircraft(
                AircraftRegistration.valueOf("CS-TVK"),
                AircraftRegistrationCountry.valueOf("Portugal"),
                CabinConfiguration.valueOf(120, 10, 0),
                AircraftOperationalStatus.active(),
                6,
                company("TAP Air Portugal", "Portugal", "TP", "TAP"),
                aircraftModel,
                nonCertifiedEngine
        ));
    }

    @Test
    void ensureSameAsReturnsTrueForSameInstance() {
        final EngineModel engineModel = engineModel("LEAP-X3", "CFM International");
        final AircraftModel aircraftModel = aircraftModel("A318", "Airbus", 130, engineModel);

        final Aircraft aircraft = aircraft(
                "CS-TVL",
                company("TAP Air Portugal", "Portugal", "TP", "TAP"),
                aircraftModel,
                engineModel
        );

        assertTrue(aircraft.sameAs(aircraft));
    }

    @Test
    void ensureSameAsReturnsTrueForSameIdentity() {
        final EngineModel engineModel = engineModel("LEAP-X4", "CFM International");
        final AircraftModel aircraftModel = aircraftModel("A320ceo", "Airbus", 180, engineModel);

        final Aircraft first = aircraft(
                "CS-TVM",
                company("TAP Air Portugal", "Portugal", "TP", "TAP"),
                aircraftModel,
                engineModel
        );

        final Aircraft second = aircraft(
                "CS-TVM",
                company("Ryanair", "Ireland", "FR", "RYR"),
                aircraftModel,
                engineModel
        );

        assertTrue(first.sameAs(second));
    }

    @Test
    void ensureSameAsReturnsFalseForDifferentIdentity() {
        final EngineModel engineModel = engineModel("LEAP-X5", "CFM International");
        final AircraftModel aircraftModel = aircraftModel("A321ceo", "Airbus", 190, engineModel);

        final Aircraft first = aircraft(
                "CS-TVN",
                company("TAP Air Portugal", "Portugal", "TP", "TAP"),
                aircraftModel,
                engineModel
        );

        final Aircraft second = aircraft(
                "CS-TVO",
                company("TAP Air Portugal", "Portugal", "TP", "TAP"),
                aircraftModel,
                engineModel
        );

        assertFalse(first.sameAs(second));
    }

    @Test
    void ensureSameAsReturnsFalseForDifferentObjectType() {
        final EngineModel engineModel = engineModel("LEAP-X6", "CFM International");
        final AircraftModel aircraftModel = aircraftModel("A320LR", "Airbus", 180, engineModel);

        final Aircraft aircraft = aircraft(
                "CS-TVP",
                company("TAP Air Portugal", "Portugal", "TP", "TAP"),
                aircraftModel,
                engineModel
        );

        assertFalse(aircraft.sameAs("not an aircraft"));
    }

    @Test
    void ensureSameAsReturnsFalseForNull() {
        final EngineModel engineModel = engineModel("LEAP-X7", "CFM International");
        final AircraftModel aircraftModel = aircraftModel("A320XLR", "Airbus", 180, engineModel);

        final Aircraft aircraft = aircraft(
                "CS-TVQ",
                company("TAP Air Portugal", "Portugal", "TP", "TAP"),
                aircraftModel,
                engineModel
        );

        assertFalse(aircraft.sameAs(null));
    }

    @Test
    void ensureAircraftCanBeDecommissioned() {
        final EngineModel engineModel = engineModel("LEAP-X8", "CFM International");
        final AircraftModel aircraftModel = aircraftModel("A320-RET", "Airbus", 180, engineModel);

        final Aircraft aircraft = aircraft(
                "CS-TVR",
                company("TAP Air Portugal", "Portugal", "TP", "TAP"),
                aircraftModel,
                engineModel
        );

        aircraft.decommission();

        assertEquals(AircraftOperationalStatus.decommissioned(), aircraft.operationalStatus());
        assertTrue(aircraft.isDecommissioned());
        assertFalse(aircraft.canBeUsedForFlights());
    }

    @Test
    void ensureAlreadyDecommissionedAircraftCannotBeDecommissionedAgain() {
        final EngineModel engineModel = engineModel("LEAP-X9", "CFM International");
        final AircraftModel aircraftModel = aircraftModel("A321-RET", "Airbus", 190, engineModel);

        final Aircraft aircraft = aircraft(
                "CS-TVS",
                company("TAP Air Portugal", "Portugal", "TP", "TAP"),
                aircraftModel,
                engineModel
        );

        aircraft.decommission();

        final IllegalStateException result = assertThrows(IllegalStateException.class, aircraft::decommission);

        assertEquals("Aircraft is already decommissioned.", result.getMessage());
    }

    private Aircraft aircraft(
            final String registration,
            final AirTransportCompany company,
            final AircraftModel aircraftModel,
            final EngineModel engineModel) {

        return new Aircraft(
                AircraftRegistration.valueOf(registration),
                AircraftRegistrationCountry.valueOf("Portugal"),
                CabinConfiguration.valueOf(100, 10, 0),
                AircraftOperationalStatus.active(),
                6,
                company,
                aircraftModel,
                engineModel
        );
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

    private EngineModel engineModel(final String modelName, final String manufacturer) {
        return new EngineModel(
                EngineModelDesignator.valueOf(modelName, manufacturer),
                EngineType.valueOf("TURBOFAN"),
                FuelType.valueOf("JET A-1"),
                120.0,
                140.0,
                0.60
        );
    }

    private AircraftModel aircraftModel(
            final String modelName,
            final String manufacturer,
            final int passengerCapacity,
            final EngineModel certifiedEngineModel) {

        return new AircraftModel(
                AircraftModelDesignator.valueOf(modelName, manufacturer),
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
                passengerCapacity,
                List.of(certifiedEngineModel)
        );
    }
}
