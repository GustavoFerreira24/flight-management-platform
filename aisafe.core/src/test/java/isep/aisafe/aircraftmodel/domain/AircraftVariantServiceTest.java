package isep.aisafe.aircraftmodel.domain;

import isep.aisafe.enginemodel.domain.EngineModel;
import isep.aisafe.enginemodel.domain.EngineModelDesignator;
import isep.aisafe.enginemodel.domain.EngineType;
import isep.aisafe.enginemodel.domain.FuelType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AircraftVariantServiceTest {

    private final AircraftVariantService service = new AircraftVariantService();

    @Test
    void ensureCertifiedEngineModelIsAccepted() {
        final EngineModel engineModel = engineModel("LEAP-SVC", "CFM International");
        final AircraftModel aircraftModel = aircraftModel(engineModel);

        assertDoesNotThrow(() -> service.ensureCertifiedVariant(aircraftModel, engineModel));
    }

    @Test
    void ensureNonCertifiedEngineModelIsRejected() {
        final EngineModel certifiedEngineModel = engineModel("LEAP-SVC", "CFM International");
        final EngineModel nonCertifiedEngineModel = engineModel("PW-SVC", "Pratt Whitney");
        final AircraftModel aircraftModel = aircraftModel(certifiedEngineModel);

        final IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () ->
                service.ensureCertifiedVariant(aircraftModel, nonCertifiedEngineModel));

        assertEquals("Engine model is not certified for this aircraft model.", result.getMessage());
    }

    @Test
    void ensureNullAircraftModelIsRejected() {
        final EngineModel engineModel = engineModel("LEAP-SVC", "CFM International");

        final IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () ->
                service.ensureCertifiedVariant(null, engineModel));

        assertEquals("Aircraft model cannot be null.", result.getMessage());
    }

    @Test
    void ensureNullEngineModelIsRejected() {
        final EngineModel engineModel = engineModel("LEAP-SVC", "CFM International");
        final AircraftModel aircraftModel = aircraftModel(engineModel);

        final IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () ->
                service.ensureCertifiedVariant(aircraftModel, null));

        assertEquals("Engine model cannot be null.", result.getMessage());
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

    private AircraftModel aircraftModel(final EngineModel certifiedEngineModel) {
        return new AircraftModel(
                AircraftModelDesignator.valueOf("A320-SVC", "Airbus"),
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
                186,
                List.of(certifiedEngineModel)
        );
    }
}
