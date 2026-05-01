package isep.aisafe.application.enginemodel;

import isep.aisafe.enginemodel.domain.EngineModel;
import isep.aisafe.enginemodel.domain.EngineModelDesignator;
import isep.aisafe.enginemodel.domain.EngineModelRepository;
import isep.aisafe.enginemodel.domain.EngineType;
import isep.aisafe.enginemodel.domain.FuelType;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.infrastructure.persistence.PersistenceContext;

public class RegisterEngineModelController {

    private final AisafeAuthorizationService authz;
    private final EngineModelRepository engineModelRepository;

    public RegisterEngineModelController() {
        this(new AisafeAuthorizationService(), PersistenceContext.repositories().engineModels());
    }

    public RegisterEngineModelController(final EngineModelRepository engineModelRepository) {
        this(new AisafeAuthorizationService(), engineModelRepository);
    }

    RegisterEngineModelController(
            final AisafeAuthorizationService authz,
            final EngineModelRepository engineModelRepository) {
        this.authz = authz;
        this.engineModelRepository = engineModelRepository;
    }

    public EngineModel registerEngineModel(
            final String modelName,
            final String manufacturer,
            final String engineType,
            final String fuelType,
            final double power,
            final double thrust,
            final double tsfc) {

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.BACKOFFICE_CONFIGURATION);

        final EngineModelDesignator designator = EngineModelDesignator.valueOf(modelName, manufacturer);

        if (engineModelRepository.findByDesignator(designator).isPresent()) {
            throw new IllegalArgumentException("Engine model already exists.");
        }

        final EngineModel engineModel = new EngineModel(
                designator,
                EngineType.valueOf(engineType),
                FuelType.valueOf(fuelType),
                power,
                thrust,
                tsfc
        );

        return engineModelRepository.save(engineModel);
    }
}
