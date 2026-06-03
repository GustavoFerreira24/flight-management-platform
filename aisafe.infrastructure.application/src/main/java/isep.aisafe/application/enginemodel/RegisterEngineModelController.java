package isep.aisafe.application.enginemodel;

import isep.aisafe.enginemodel.domain.EngineModel;
import isep.aisafe.enginemodel.domain.EngineModelDesignator;
import isep.aisafe.enginemodel.domain.EngineModelRepository;
import isep.aisafe.enginemodel.domain.EngineType;
import isep.aisafe.enginemodel.domain.FuelType;
import isep.aisafe.infrastructure.authz.*;
import isep.aisafe.infrastructure.persistence.*;
import isep.aisafe.manufacturer.domain.Manufacturer;
import isep.aisafe.manufacturer.domain.ManufacturerDesignator;
import isep.aisafe.manufacturer.domain.ManufacturerRepository;

import java.util.ArrayList;
import java.util.List;

public class RegisterEngineModelController {

    private final AisafeAuthorizationService authz;
    private final EngineModelRepository engineModelRepository;
    private final ManufacturerRepository manufacturerRepository;

    public RegisterEngineModelController() {
        this(
                new AisafeAuthorizationService(),
                PersistenceContext.repositories().engineModels(),
                PersistenceContext.repositories().manufacturers()
        );
    }

    RegisterEngineModelController(
            final AisafeAuthorizationService authz,
            final EngineModelRepository engineModelRepository) {
        this(authz, engineModelRepository, null);
    }

    RegisterEngineModelController(
            final AisafeAuthorizationService authz,
            final EngineModelRepository engineModelRepository,
            final ManufacturerRepository manufacturerRepository) {
        this.authz = authz;
        this.engineModelRepository = engineModelRepository;
        this.manufacturerRepository = manufacturerRepository;
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

        final Manufacturer engineManufacturer = engineManufacturer(manufacturer);
        final EngineModelDesignator designator = EngineModelDesignator.valueOf(
                modelName,
                engineManufacturer.identity().toString()
        );

        if (engineModelRepository.findByDesignator(designator).isPresent()) {
            throw new IllegalArgumentException("Engine model already exists.");
        }

        final EngineModel engineModel = new EngineModel(
                designator,
                engineManufacturer,
                EngineType.valueOf(engineType),
                FuelType.valueOf(fuelType),
                power,
                thrust,
                tsfc
        );

        return engineModelRepository.save(engineModel);
    }

    public List<String> engineManufacturerDesignators() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.BACKOFFICE_CONFIGURATION);

        final List<String> designators = new ArrayList<>();

        if (manufacturerRepository == null) {
            return designators;
        }

        for (final Manufacturer manufacturer : manufacturerRepository.findAll()) {
            if (manufacturer.canManufactureEngineModels()) {
                designators.add(manufacturer.designator().toString());
            }
        }

        designators.sort(String::compareTo);
        return designators;
    }

    private Manufacturer engineManufacturer(final String manufacturerDesignator) {
        final ManufacturerDesignator designator = ManufacturerDesignator.valueOf(manufacturerDesignator);

        if (manufacturerRepository == null) {
            return Manufacturer.referenceFromText(manufacturerDesignator);
        }

        return manufacturerRepository.findByDesignator(designator)
                .orElseThrow(() -> new IllegalArgumentException("Manufacturer was not found."));
    }
}
