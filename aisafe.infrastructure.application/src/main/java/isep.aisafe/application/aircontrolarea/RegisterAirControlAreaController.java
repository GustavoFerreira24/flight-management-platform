package isep.aisafe.application.aircontrolarea;

import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.aircontrolarea.domain.AirControlAreaRepository;
import isep.aisafe.aircontrolarea.domain.FuelUnit;
import isep.aisafe.aircontrolarea.domain.GeographicBoundary;
import isep.aisafe.aircontrolarea.domain.MinimumFuelRequirement;
import isep.aisafe.infrastructure.authz.*;
import isep.aisafe.infrastructure.persistence.*;
import eapli.framework.validations.Preconditions;


/**
 * Controller for registering air control areas.
 */
public class RegisterAirControlAreaController {

    private final AisafeAuthorizationService authz;
    private final AirControlAreaRepository airControlAreaRepository;

    /**
     * Creates the controller with the default authorization service and repository.
     */
    public RegisterAirControlAreaController() {
        this(new AisafeAuthorizationService(), PersistenceContext.repositories().airControlAreas());
    }

    /**
     * Creates the controller with a custom repository.
     * This constructor is mainly useful for tests.
     *
     * @param airControlAreaRepository the air control area repository
     */
    public RegisterAirControlAreaController(final AirControlAreaRepository airControlAreaRepository) {
        this(new AisafeAuthorizationService(), airControlAreaRepository);
    }

    RegisterAirControlAreaController(
            final AisafeAuthorizationService authz,
            final AirControlAreaRepository airControlAreaRepository) {

        Preconditions.nonNull(authz, "Authorization service cannot be null.");
        Preconditions.nonNull(airControlAreaRepository, "Air control area repository cannot be null.");

        this.authz = authz;
        this.airControlAreaRepository = airControlAreaRepository;
    }

    /**
     * Registers a new air control area.
     *
     * @param code the air control area code
     * @param name the air control area name
     * @param minLatitude the minimum latitude
     * @param maxLatitude the maximum latitude
     * @param minLongitude the minimum longitude
     * @param maxLongitude the maximum longitude
     * @param minimumFuelAmount the minimum fuel amount
     * @param fuelUnit the fuel unit
     * @return the registered air control area
     */
    public AirControlArea registerAirControlArea(
            final String code,
            final String name,
            final double minLatitude,
            final double maxLatitude,
            final double minLongitude,
            final double maxLongitude,
            final double minimumFuelAmount,
            final String fuelUnit) {

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.BACKOFFICE_CONFIGURATION);

        final AirControlAreaCode areaCode = AirControlAreaCode.valueOf(code);

        if (airControlAreaRepository.findByCode(areaCode).isPresent()) {
            throw new IllegalArgumentException("Air control area code already exists.");
        }

        final GeographicBoundary geographicBoundary = GeographicBoundary.valueOf(
                minLatitude,
                maxLatitude,
                minLongitude,
                maxLongitude
        );

        final MinimumFuelRequirement minimumFuelRequirement = MinimumFuelRequirement.valueOf(
                minimumFuelAmount,
                FuelUnit.valueOf(fuelUnit)
        );

        final AirControlArea airControlArea = new AirControlArea(
                areaCode,
                name,
                geographicBoundary,
                minimumFuelRequirement
        );

        return airControlAreaRepository.save(airControlArea);
    }
}