package isep.aisafe.app.backoffice.console;

import eapli.framework.infrastructure.authz.application.AuthzRegistry;
import eapli.framework.infrastructure.authz.application.UserManagementService;
import eapli.framework.infrastructure.authz.domain.model.Role;
import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.aircontrolarea.domain.AirControlAreaRepository;
import isep.aisafe.aircraftmodel.domain.AircraftModel;
import isep.aisafe.aircraftmodel.domain.AircraftModelDesignator;
import isep.aisafe.aircraftmodel.domain.AircraftModelMotorization;
import isep.aisafe.aircraftmodel.domain.AircraftModelRepository;
import isep.aisafe.aircraftmodel.domain.AircraftModelType;
import isep.aisafe.enginemodel.domain.EngineModel;
import isep.aisafe.enginemodel.domain.EngineModelDesignator;
import isep.aisafe.enginemodel.domain.EngineModelRepository;
import isep.aisafe.enginemodel.domain.EngineType;
import isep.aisafe.enginemodel.domain.FuelType;
import isep.aisafe.infrastructure.persistence.PersistenceContext;
import isep.aisafe.usermanagement.domain.AisafeRoles;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.airtransportcompany.domain.AirTransportCompanyRepository;
import isep.aisafe.airtransportcompany.domain.Country;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.airtransportcompany.domain.ICAOCode;
import isep.aisafe.customercollaborator.domain.CollaboratorEmail;
import isep.aisafe.customercollaborator.domain.CustomerCollaborator;
import isep.aisafe.customercollaborator.domain.CustomerCollaboratorRepository;
import isep.aisafe.customercollaborator.domain.CustomerReference;

import java.util.List;
import java.util.Set;

public class AisafeBootstrapper {

    public static void bootstrap() {

        final UserManagementService userService = AuthzRegistry.userService();

        register(userService, "admin", "Password1",
                "Admin", "User", "admin@aisafe.com",
                Set.of(AisafeRoles.ADMIN));

        register(userService, "backoffice", "Password1",
                "Backoffice", "User", "backoffice@aisafe.com",
                Set.of(AisafeRoles.BACKOFFICE_OPERATOR));

        register(userService, "weather", "Password1",
                "Weather", "Person", "weather@aisafe.com",
                Set.of(AisafeRoles.WEATHER_PERSON));

        register(userService, "fco", "Password1",
                "Flight", "Control", "fco@aisafe.com",
                Set.of(AisafeRoles.FLIGHT_CONTROL_OPERATOR));

        bootstrapAirTransportCompanies();
        bootstrapAirControlAreas();
        bootstrapCustomerCollaborators(userService);
        bootstrapAircraftAndEngineModels();
    }

    private static void bootstrapAircraftAndEngineModels() {
        final EngineModelRepository engineModelRepository = PersistenceContext.repositories().engineModels();
        final AircraftModelRepository aircraftModelRepository = PersistenceContext.repositories().aircraftModels();

        final EngineModel cfmLeap = registerEngineModelIfMissing(
                engineModelRepository,
                EngineModelDesignator.valueOf("LEAP-1A", "CFM International"),
                EngineType.valueOf("TURBOFAN"),
                FuelType.valueOf("JET A-1"),
                120.0,
                140.0,
                0.60
        );

        final AircraftModelDesignator a320neo = AircraftModelDesignator.valueOf("A320neo", "Airbus");

        if (aircraftModelRepository.findByDesignator(a320neo).isPresent()) {
            return;
        }

        final AircraftModel aircraftModel = new AircraftModel(
                a320neo,
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
                List.of(cfmLeap)
        );

        aircraftModelRepository.save(aircraftModel);
        System.out.println("Bootstrapped aircraft model: " + a320neo);
    }

    private static EngineModel registerEngineModelIfMissing(
            final EngineModelRepository repository,
            final EngineModelDesignator designator,
            final EngineType engineType,
            final FuelType fuelType,
            final double power,
            final double thrust,
            final double tsfc) {

        return repository.findByDesignator(designator).orElseGet(() -> {
            final EngineModel engineModel = new EngineModel(
                    designator,
                    engineType,
                    fuelType,
                    power,
                    thrust,
                    tsfc
            );

            final EngineModel savedEngineModel = repository.save(engineModel);
            System.out.println("Bootstrapped engine model: " + designator);
            return savedEngineModel;
        });
    }

    private static void register(
            final UserManagementService userService,
            final String username,
            final String password,
            final String firstName,
            final String lastName,
            final String email,
            final Set<Role> roles) {

        try {
            userService.registerNewUser(
                    username,
                    password,
                    firstName,
                    lastName,
                    email,
                    roles
            );
            System.out.println("User created: " + username);
        } catch (Exception e) {
            System.out.println("User already exists: " + username);
        }
    }

    /**
     * Bootstraps initial air transport companies.
     */
    private static void bootstrapAirTransportCompanies() {
        final AirTransportCompanyRepository repository =
                PersistenceContext.repositories().airTransportCompanies();

        registerAirTransportCompanyIfMissing(
                repository,
                "TAP Air Portugal",
                "Portugal",
                "TP",
                "TAP"
        );

        registerAirTransportCompanyIfMissing(
                repository,
                "Ryanair",
                "Ireland",
                "FR",
                "RYR"
        );

        registerAirTransportCompanyIfMissing(
                repository,
                "Lufthansa",
                "Germany",
                "LH",
                "DLH"
        );
    }

    /**
     * Bootstraps initial air control areas used by customer collaborators.
     */
    private static void bootstrapAirControlAreas() {
        final AirControlAreaRepository repository =
                PersistenceContext.repositories().airControlAreas();

        registerAirControlAreaIfMissing(
                repository,
                "LISACC",
                "Lisbon Area Control Centre"
        );
    }

    /**
     * Bootstraps initial customer collaborators associated with existing customers.
     *
     * @param userService user service used to create matching system users
     */
    private static void bootstrapCustomerCollaborators(final UserManagementService userService) {
        final CustomerCollaboratorRepository collaboratorRepository =
                PersistenceContext.repositories().customerCollaborators();

        registerCustomerCollaboratorIfMissing(
                userService,
                collaboratorRepository,
                "TAP",
                "Collaborator",
                "tap.collaborator@aisafe.com",
                "Password1",
                CustomerReference.airTransportCompany("TP"),
                Set.of(AisafeRoles.AIR_TRANSPORT_COMPANY_COLLABORATOR)
        );

        registerCustomerCollaboratorIfMissing(
                userService,
                collaboratorRepository,
                "Lisbon",
                "Controller",
                "lisacc.collaborator@aisafe.com",
                "Password1",
                CustomerReference.airControlArea("LISACC"),
                Set.of(AisafeRoles.AIR_CONTROL_AREA_COLLABORATOR)
        );
    }

    /**
     * Registers a customer collaborator and its system user if the collaborator does not exist yet.
     *
     * @param userService user service used to create the system user
     * @param repository customer collaborator repository
     * @param firstName collaborator first name
     * @param lastName collaborator last name
     * @param email collaborator email and system username
     * @param password initial system-user password
     * @param customerReference existing customer associated with the collaborator
     * @param roles system-user roles
     * @return existing or newly created customer collaborator
     */
    static CustomerCollaborator registerCustomerCollaboratorIfMissing(
            final UserManagementService userService,
            final CustomerCollaboratorRepository repository,
            final String firstName,
            final String lastName,
            final String email,
            final String password,
            final CustomerReference customerReference,
            final Set<Role> roles) {

        final CollaboratorEmail collaboratorEmail = CollaboratorEmail.valueOf(email);

        if (repository.findByEmail(collaboratorEmail).isPresent()) {
            return repository.findByEmail(collaboratorEmail).get();
        }

        register(
                userService,
                collaboratorEmail.toString(),
                password,
                firstName,
                lastName,
                collaboratorEmail.toString(),
                roles
        );

        final CustomerCollaborator collaborator = new CustomerCollaborator(
                firstName,
                lastName,
                collaboratorEmail,
                customerReference,
                collaboratorEmail.toString()
        );

        final CustomerCollaborator savedCollaborator = repository.save(collaborator);
        System.out.println("Bootstrapped customer collaborator: " + collaboratorEmail);

        return savedCollaborator;
    }

    /**
     * Registers an air control area if it does not already exist.
     *
     * @param repository air control area repository
     * @param areaCode air control area code
     * @param name air control area name
     * @return existing or newly created air control area
     */
    private static AirControlArea registerAirControlAreaIfMissing(
            final AirControlAreaRepository repository,
            final String areaCode,
            final String name) {

        final AirControlAreaCode code = AirControlAreaCode.valueOf(areaCode);

        if (repository.findByCode(code).isPresent()) {
            return repository.findByCode(code).get();
        }

        final AirControlArea airControlArea = new AirControlArea(code, name);
        final AirControlArea savedAirControlArea = repository.save(airControlArea);
        System.out.println("Bootstrapped air control area: " + code);

        return savedAirControlArea;
    }

    /**
     * Registers an air transport company if it does not already exist.
     *
     * @param repository  the air transport company repository
     * @param name        the company name
     * @param countryName the company country
     * @param iataCode    the company IATA code
     * @param icaoCode    the company ICAO code
     * @return the existing or newly created air transport company
     */
    private static AirTransportCompany registerAirTransportCompanyIfMissing(
            final AirTransportCompanyRepository repository,
            final String name,
            final String countryName,
            final String iataCode,
            final String icaoCode) {

        final Country country = Country.valueOf(countryName);
        final IATACode companyIataCode = IATACode.valueOf(iataCode);
        final ICAOCode companyIcaoCode = ICAOCode.valueOf(icaoCode);

        if (repository.findByIataCode(companyIataCode).isPresent()) {
            return repository.findByIataCode(companyIataCode).get();
        }

        if (repository.findByIcaoCode(companyIcaoCode).isPresent()) {
            throw new IllegalArgumentException("Air transport company ICAO code already exists in bootstrap.");
        }

        final AirTransportCompany airTransportCompany = new AirTransportCompany(
                name,
                country,
                companyIataCode,
                companyIcaoCode
        );

        repository.save(airTransportCompany);
        System.out.println("Bootstrapped air transport company: " + companyIataCode);

        return airTransportCompany;
    }
}
