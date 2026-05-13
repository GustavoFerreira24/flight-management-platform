package isep.aisafe.application.aircraft;

import isep.aisafe.aircraft.domain.Aircraft;
import isep.aisafe.aircraft.domain.AircraftOperationalStatus;
import isep.aisafe.aircraft.domain.AircraftRegistration;
import isep.aisafe.aircraft.domain.AircraftRegistrationCountry;
import isep.aisafe.aircraft.domain.AircraftRepository;
import isep.aisafe.aircraft.domain.CabinConfiguration;
import isep.aisafe.aircraftmodel.domain.AircraftModel;
import isep.aisafe.aircraftmodel.domain.AircraftModelDesignator;
import isep.aisafe.aircraftmodel.domain.AircraftModelRepository;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.airtransportcompany.domain.AirTransportCompanyRepository;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.customercollaborator.domain.CustomerCollaborator;
import isep.aisafe.customercollaborator.domain.CustomerCollaboratorRepository;
import isep.aisafe.customercollaborator.domain.CustomerReference;
import isep.aisafe.customercollaborator.domain.CustomerType;
import isep.aisafe.customercollaborator.domain.SystemUsername;
import isep.aisafe.enginemodel.domain.EngineModel;
import isep.aisafe.enginemodel.domain.EngineModelDesignator;
import isep.aisafe.enginemodel.domain.EngineModelRepository;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.infrastructure.persistence.PersistenceContext;

/**
 * Controller for registering aircraft in an air transport company's fleet.
 */
public class RegisterAircraftController {

    private final AisafeAuthorizationService authz;
    private final AircraftRepository aircraftRepository;
    private final AircraftModelRepository aircraftModelRepository;
    private final EngineModelRepository engineModelRepository;
    private final AirTransportCompanyRepository airTransportCompanyRepository;
    private final CustomerCollaboratorRepository customerCollaboratorRepository;

    /**
     * Creates the controller with default services and repositories.
     */
    public RegisterAircraftController() {
        this(
                new AisafeAuthorizationService(),
                PersistenceContext.repositories().aircraft(),
                PersistenceContext.repositories().aircraftModels(),
                PersistenceContext.repositories().engineModels(),
                PersistenceContext.repositories().airTransportCompanies(),
                PersistenceContext.repositories().customerCollaborators()
        );
    }

    RegisterAircraftController(
            final AisafeAuthorizationService authz,
            final AircraftRepository aircraftRepository,
            final AircraftModelRepository aircraftModelRepository,
            final EngineModelRepository engineModelRepository,
            final AirTransportCompanyRepository airTransportCompanyRepository,
            final CustomerCollaboratorRepository customerCollaboratorRepository) {

        if (authz == null) {
            throw new IllegalArgumentException("Authorization service cannot be null.");
        }

        if (aircraftRepository == null) {
            throw new IllegalArgumentException("Aircraft repository cannot be null.");
        }

        if (aircraftModelRepository == null) {
            throw new IllegalArgumentException("Aircraft model repository cannot be null.");
        }

        if (engineModelRepository == null) {
            throw new IllegalArgumentException("Engine model repository cannot be null.");
        }

        if (airTransportCompanyRepository == null) {
            throw new IllegalArgumentException("Air transport company repository cannot be null.");
        }

        if (customerCollaboratorRepository == null) {
            throw new IllegalArgumentException("Customer collaborator repository cannot be null.");
        }

        this.authz = authz;
        this.aircraftRepository = aircraftRepository;
        this.aircraftModelRepository = aircraftModelRepository;
        this.engineModelRepository = engineModelRepository;
        this.airTransportCompanyRepository = airTransportCompanyRepository;
        this.customerCollaboratorRepository = customerCollaboratorRepository;
    }

    /**
     * Registers an aircraft in the authenticated collaborator's company fleet.
     *
     * @param registration the aircraft registration number
     * @param registrationCountry the aircraft registration country
     * @param crewSize the crew size
     * @param economySeats the number of economy seats
     * @param businessSeats the number of business seats
     * @param firstClassSeats the number of first class seats
     * @param aircraftModelName the aircraft model name
     * @param aircraftModelManufacturer the aircraft model manufacturer
     * @param engineModelName the engine model name
     * @param engineModelManufacturer the engine model manufacturer
     * @return the registered aircraft
     */
    public Aircraft registerAircraft(
            final String registration,
            final String registrationCountry,
            final int crewSize,
            final int economySeats,
            final int businessSeats,
            final int firstClassSeats,
            final String aircraftModelName,
            final String aircraftModelManufacturer,
            final String engineModelName,
            final String engineModelManufacturer) {

        return registerAircraft(
                registration,
                registrationCountry,
                AircraftOperationalStatus.active().toString(),
                crewSize,
                economySeats,
                businessSeats,
                firstClassSeats,
                aircraftModelName,
                aircraftModelManufacturer,
                engineModelName,
                engineModelManufacturer
        );
    }

    /**
     * Registers an aircraft with an explicit operational status.
     *
     * @param registration the aircraft registration number
     * @param registrationCountry the aircraft registration country
     * @param operationalStatus the initial operational status
     * @param crewSize the crew size
     * @param economySeats the number of economy seats
     * @param businessSeats the number of business seats
     * @param firstClassSeats the number of first class seats
     * @param aircraftModelName the aircraft model name
     * @param aircraftModelManufacturer the aircraft model manufacturer
     * @param engineModelName the engine model name
     * @param engineModelManufacturer the engine model manufacturer
     * @return the registered aircraft
     */
    public Aircraft registerAircraft(
            final String registration,
            final String registrationCountry,
            final String operationalStatus,
            final int crewSize,
            final int economySeats,
            final int businessSeats,
            final int firstClassSeats,
            final String aircraftModelName,
            final String aircraftModelManufacturer,
            final String engineModelName,
            final String engineModelManufacturer) {

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.AIR_COMPANY_CONFIGURATION);

        final AircraftRegistration aircraftRegistration = AircraftRegistration.valueOf(registration);

        if (aircraftRepository.findByRegistration(aircraftRegistration).isPresent()) {
            throw new IllegalArgumentException("Aircraft registration already exists.");
        }

        final AirTransportCompany company = authenticatedCollaboratorCompany();

        final AircraftModelDesignator aircraftModelDesignator =
                AircraftModelDesignator.valueOf(aircraftModelName, aircraftModelManufacturer);

        final AircraftModel aircraftModel = aircraftModelRepository
                .findByDesignator(aircraftModelDesignator)
                .orElseThrow(() -> new IllegalArgumentException("Aircraft model was not found."));

        final EngineModelDesignator engineModelDesignator =
                EngineModelDesignator.valueOf(engineModelName, engineModelManufacturer);

        final EngineModel engineModel = engineModelRepository
                .findByDesignator(engineModelDesignator)
                .orElseThrow(() -> new IllegalArgumentException("Engine model was not found."));

        final AircraftRegistrationCountry country = AircraftRegistrationCountry.valueOf(registrationCountry);

        final CabinConfiguration cabinConfiguration = CabinConfiguration.valueOf(
                economySeats,
                businessSeats,
                firstClassSeats
        );

        final AircraftOperationalStatus status = AircraftOperationalStatus.valueOf(operationalStatus);

        final Aircraft aircraft = new Aircraft(
                aircraftRegistration,
                country,
                cabinConfiguration,
                status,
                crewSize,
                company,
                aircraftModel,
                engineModel
        );

        return aircraftRepository.save(aircraft);
    }

    private AirTransportCompany authenticatedCollaboratorCompany() {
        final String username = authz.authenticatedUsername();
        final SystemUsername systemUsername = SystemUsername.valueOf(username);

        final CustomerCollaborator collaborator = customerCollaboratorRepository
                .findBySystemUsername(systemUsername)
                .orElseThrow(() -> new IllegalStateException("Authenticated user is not a customer collaborator."));

        final CustomerReference customerReference = collaborator.customerReference();

        if (customerReference.customerType() != CustomerType.AIR_TRANSPORT_COMPANY) {
            throw new IllegalStateException(
                    "Authenticated collaborator is not associated with an air transport company."
            );
        }

        final IATACode companyIataCode = IATACode.valueOf(customerReference.customerIdentity());

        return airTransportCompanyRepository
                .findByIataCode(companyIataCode)
                .orElseThrow(() -> new IllegalStateException("Associated air transport company was not found."));
    }
}
