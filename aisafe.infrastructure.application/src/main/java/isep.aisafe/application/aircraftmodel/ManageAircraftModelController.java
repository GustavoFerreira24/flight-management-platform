package isep.aisafe.application.aircraftmodel;

import isep.aisafe.aircraftmodel.domain.AircraftModel;
import isep.aisafe.aircraftmodel.domain.AircraftModelDesignator;
import isep.aisafe.aircraftmodel.domain.AircraftModelMotorization;
import isep.aisafe.aircraftmodel.domain.AircraftModelRepository;
import isep.aisafe.aircraftmodel.domain.AircraftModelType;
import isep.aisafe.enginemodel.domain.EngineModel;
import isep.aisafe.enginemodel.domain.EngineModelDesignator;
import isep.aisafe.enginemodel.domain.EngineModelRepository;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.infrastructure.persistence.PersistenceContext;

import java.util.ArrayList;
import java.util.List;

public class ManageAircraftModelController {

    private final AisafeAuthorizationService authz = new AisafeAuthorizationService();
    private final AircraftModelRepository aircraftModelRepository;
    private final EngineModelRepository engineModelRepository;

    public ManageAircraftModelController() {
        this(
                PersistenceContext.repositories().aircraftModels(),
                PersistenceContext.repositories().engineModels()
        );
    }

    public ManageAircraftModelController(
            final AircraftModelRepository aircraftModelRepository,
            final EngineModelRepository engineModelRepository) {
        this.aircraftModelRepository = aircraftModelRepository;
        this.engineModelRepository = engineModelRepository;
    }

    public AircraftModel registerAircraftModel(
            final String modelName,
            final String manufacturer,
            final String type,
            final String motorization,
            final double maxRange,
            final double emptyWeight,
            final double mtow,
            final double mzfw,
            final double maxFuelCapacity,
            final double serviceCeiling,
            final double cruiseSpeed,
            final double wingArea,
            final double dragCoefficient,
            final double liftCoefficient,
            final int passengerCapacity,
            final List<String> certifiedEngineDesignators) {

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.BACKOFFICE_CONFIGURATION);

        final AircraftModelDesignator designator = AircraftModelDesignator.valueOf(modelName, manufacturer);

        if (aircraftModelRepository.findByDesignator(designator).isPresent()) {
            throw new IllegalArgumentException("Aircraft model already exists.");
        }

        if (certifiedEngineDesignators == null || certifiedEngineDesignators.isEmpty()) {
            throw new IllegalArgumentException("Aircraft model must have at least one certified engine model.");
        }

        final List<EngineModel> certifiedEngines = new ArrayList<>();

        for (String rawDesignator : certifiedEngineDesignators) {
            final EngineModelDesignator engineDesignator = EngineModelDesignator.valueOf(rawDesignator);

            final EngineModel engineModel = engineModelRepository
                    .findByDesignator(engineDesignator)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Engine model not found: " + engineDesignator));

            certifiedEngines.add(engineModel);
        }

        final AircraftModel aircraftModel = new AircraftModel(
                designator,
                AircraftModelType.valueOf(type),
                AircraftModelMotorization.valueOf(motorization),
                maxRange,
                emptyWeight,
                mtow,
                mzfw,
                maxFuelCapacity,
                serviceCeiling,
                cruiseSpeed,
                wingArea,
                dragCoefficient,
                liftCoefficient,
                passengerCapacity,
                certifiedEngines
        );

        return aircraftModelRepository.save(aircraftModel);
    }
}
