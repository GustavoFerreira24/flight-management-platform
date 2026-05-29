package isep.aisafe.app.backoffice.console;

import eapli.framework.infrastructure.authz.application.AuthzRegistry;
import eapli.framework.infrastructure.authz.application.UserManagementService;
import eapli.framework.infrastructure.authz.domain.model.Role;
import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.aircontrolarea.domain.AirControlAreaRepository;
import isep.aisafe.aircontrolarea.domain.FuelUnit;
import isep.aisafe.aircontrolarea.domain.GeographicBoundary;
import isep.aisafe.aircontrolarea.domain.MinimumFuelRequirement;
import isep.aisafe.aircraft.domain.Aircraft;
import isep.aisafe.aircraft.domain.AircraftOperationalStatus;
import isep.aisafe.aircraft.domain.AircraftRegistration;
import isep.aisafe.aircraft.domain.AircraftRegistrationCountry;
import isep.aisafe.aircraft.domain.AircraftRepository;
import isep.aisafe.aircraft.domain.CabinConfiguration;
import isep.aisafe.aircraftmodel.domain.AircraftModel;
import isep.aisafe.aircraftmodel.domain.AircraftModelDesignator;
import isep.aisafe.aircraftmodel.domain.AircraftModelMotorization;
import isep.aisafe.aircraftmodel.domain.AircraftModelRepository;
import isep.aisafe.aircraftmodel.domain.AircraftModelType;
import isep.aisafe.airport.domain.Airport;
import isep.aisafe.airport.domain.AirportBuilder;
import isep.aisafe.airport.domain.AirportICAOCode;
import isep.aisafe.airport.domain.AirportRepository;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.airtransportcompany.domain.AirTransportCompanyRepository;
import isep.aisafe.airtransportcompany.domain.Country;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.airtransportcompany.domain.ICAOCode;
import isep.aisafe.customercollaborator.domain.CollaboratorEmail;
import isep.aisafe.customercollaborator.domain.CustomerCollaborator;
import isep.aisafe.customercollaborator.domain.CustomerCollaboratorRepository;
import isep.aisafe.customercollaborator.domain.CustomerReference;
import isep.aisafe.customercollaborator.domain.SystemUsername;
import isep.aisafe.enginemodel.domain.EngineModel;
import isep.aisafe.enginemodel.domain.EngineModelDesignator;
import isep.aisafe.enginemodel.domain.EngineModelRepository;
import isep.aisafe.enginemodel.domain.EngineType;
import isep.aisafe.enginemodel.domain.FuelType;
import isep.aisafe.infrastructure.persistence.PersistenceContext;
import isep.aisafe.usermanagement.domain.AisafeRoles;

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

        register(userService, "ipma", "Password1",
                "IPMA", "Provider", "ipma@aisafe.com",
                Set.of(AisafeRoles.WEATHER_PERSON));

        register(userService, "fco", "Password1",
                "Flight", "Control", "fco@aisafe.com",
                Set.of(AisafeRoles.FLIGHT_CONTROL_OPERATOR));

        bootstrapAirTransportCompanies();
        bootstrapAirControlAreas();
        bootstrapAirports();
        bootstrapCustomerCollaborators(userService);
        bootstrapAircraftAndEngineModels();
        bootstrapDemoAircraftFleet();
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

        final EngineModel ge90b = registerEngineModelIfMissing(
                engineModelRepository,
                EngineModelDesignator.valueOf("GE 90B", "General Electric"),
                EngineType.valueOf("TURBOFAN"),
                FuelType.valueOf("JET A-1"),
                0.84,
                489000.0,
                0.00016
        );

        final AircraftModelDesignator a320neo = AircraftModelDesignator.valueOf("A320neo", "Airbus");

        if (aircraftModelRepository.findByDesignator(a320neo).isEmpty()) {
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

        final AircraftModelDesignator boeing777 = AircraftModelDesignator.valueOf("777-200ER", "Boeing");

        if (aircraftModelRepository.findByDesignator(boeing777).isEmpty()) {
            final AircraftModel aircraftModel = new AircraftModel(
                    boeing777,
                    AircraftModelType.valueOf("PASSENGER"),
                    AircraftModelMotorization.valueOf("TURBOFAN"),
                    14200,
                    145150,
                    347450,
                    209000,
                    181280,
                    13100,
                    905,
                    440.0,
                    0.017,
                    0.87,
                    314,
                    List.of(ge90b)
            );

            aircraftModelRepository.save(aircraftModel);
            System.out.println("Bootstrapped aircraft model: " + boeing777);
        }
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

        registerAirTransportCompanyIfMissing(
                repository,
                "Air Europa",
                "Spain",
                "UX",
                "AEA"
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
                "Lisbon Area Control Centre",
                30.0,
                45.0,
                -15.0,
                5.0,
                5000.0,
                "KG"
        );

        registerAirControlAreaIfMissing(
                repository,
                "IBRACC",
                "Iberian Area Control Centre",
                35.0,
                43.0,
                -10.0,
                0.0,
                5000.0,
                "KG"
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
                Set.of(AisafeRoles.FLIGHT_CONTROL_OPERATOR)
        );

        registerCustomerCollaboratorIfMissing(
                userService,
                collaboratorRepository,
                "Ryanair",
                "Collaborator",
                "ryanair.collaborator@aisafe.com",
                "Password1",
                CustomerReference.airTransportCompany("FR"),
                Set.of(AisafeRoles.AIR_TRANSPORT_COMPANY_COLLABORATOR)
        );

        registerCustomerCollaboratorIfMissing(
                userService,
                collaboratorRepository,
                "Air Europa",
                "Collaborator",
                "aireuropa.collaborator@aisafe.com",
                "Password1",
                CustomerReference.airTransportCompany("UX"),
                Set.of(AisafeRoles.AIR_TRANSPORT_COMPANY_COLLABORATOR)
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
        final SystemUsername systemUsername = SystemUsername.valueOf(collaboratorEmail.toString());

        if (repository.findByEmail(collaboratorEmail).isPresent()) {
            return repository.findByEmail(collaboratorEmail).get();
        }

        final CustomerCollaborator collaborator = new CustomerCollaborator(
                firstName,
                lastName,
                collaboratorEmail,
                customerReference,
                systemUsername
        );

        final CustomerCollaborator savedCollaborator = repository.save(collaborator);

        register(
                userService,
                collaboratorEmail.toString(),
                password,
                firstName,
                lastName,
                collaboratorEmail.toString(),
                roles
        );

        System.out.println("Bootstrapped customer collaborator: " + collaboratorEmail);

        return savedCollaborator;
    }

    /**
     * Registers an air control area if it does not already exist.
     *
     * @param repository air control area repository
     * @param areaCode air control area code
     * @param name air control area name
     * @param minLatitude minimum latitude
     * @param maxLatitude maximum latitude
     * @param minLongitude minimum longitude
     * @param maxLongitude maximum longitude
     * @param minimumFuelAmount minimum fuel amount
     * @param fuelUnit fuel unit
     * @return existing or newly created air control area
     */
    private static AirControlArea registerAirControlAreaIfMissing(
            final AirControlAreaRepository repository,
            final String areaCode,
            final String name,
            final double minLatitude,
            final double maxLatitude,
            final double minLongitude,
            final double maxLongitude,
            final double minimumFuelAmount,
            final String fuelUnit) {

        final AirControlAreaCode code = AirControlAreaCode.valueOf(areaCode);

        if (repository.findByCode(code).isPresent()) {
            return repository.findByCode(code).get();
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
                code,
                name,
                geographicBoundary,
                minimumFuelRequirement
        );

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

    private static void bootstrapAirports() {
        final AirportRepository repository = PersistenceContext.repositories().airports();
        final AirControlAreaRepository areaRepo = PersistenceContext.repositories().airControlAreas();

        final AirControlArea iberianArea = areaRepo.ofIdentity(AirControlAreaCode.valueOf("IBRACC"))
                .orElseThrow(() -> new IllegalStateException("Bootstrap: IBRACC area not found"));

        registerAirportIfMissing(
                repository,
                "LPPR", "OPO", "Francisco de Sa Carneiro Airport", "Portugal", "Porto",
                41.2481003, -8.6813898, 69.0,
                iberianArea
        );

        registerAirportIfMissing(
                repository,
                "LPPT", "LIS", "Humberto Delgado Airport", "Portugal", "Lisbon",
                38.7812996, -9.1359196, 114.0,
                iberianArea
        );

        registerAirportIfMissing(
                repository,
                "LEMD", "MAD", "Adolfo Suarez Madrid Barajas International Airport", "Spain", "Madrid",
                40.4936, -3.56676, 610.0,
                iberianArea
        );
    }

    private static void bootstrapDemoAircraftFleet() {
        final AircraftRepository aircraftRepository = PersistenceContext.repositories().aircraft();
        final AirTransportCompanyRepository companyRepository = PersistenceContext.repositories().airTransportCompanies();
        final AircraftModelRepository aircraftModelRepository = PersistenceContext.repositories().aircraftModels();
        final EngineModelRepository engineModelRepository = PersistenceContext.repositories().engineModels();

        final AircraftModel aircraftModel = aircraftModelRepository
                .findByDesignator(AircraftModelDesignator.valueOf("777-200ER", "Boeing"))
                .orElseThrow(() -> new IllegalStateException("Bootstrap: Boeing 777-200ER model not found"));
        final EngineModel engineModel = engineModelRepository
                .findByDesignator(EngineModelDesignator.valueOf("GE 90B", "General Electric"))
                .orElseThrow(() -> new IllegalStateException("Bootstrap: GE 90B engine model not found"));

        final AirTransportCompany airEuropa = companyRepository
                .findByIataCode(IATACode.valueOf("UX"))
                .orElseThrow(() -> new IllegalStateException("Bootstrap: Air Europa not found"));
        final AirTransportCompany ryanair = companyRepository
                .findByIataCode(IATACode.valueOf("FR"))
                .orElseThrow(() -> new IllegalStateException("Bootstrap: Ryanair not found"));

        registerAircraftIfMissing(
                aircraftRepository,
                "EC-UX7",
                "Spain",
                2012,
                10,
                279,
                35,
                0,
                airEuropa,
                aircraftModel,
                engineModel
        );

        registerAircraftIfMissing(
                aircraftRepository,
                "EI-FR7",
                "Ireland",
                2014,
                10,
                314,
                0,
                0,
                ryanair,
                aircraftModel,
                engineModel
        );
    }

    static Aircraft registerAircraftIfMissing(
            final AircraftRepository repository,
            final String registration,
            final String registrationCountry,
            final int manufactureYear,
            final int crewSize,
            final int economySeats,
            final int businessSeats,
            final int firstClassSeats,
            final AirTransportCompany company,
            final AircraftModel aircraftModel,
            final EngineModel engineModel) {

        final AircraftRegistration aircraftRegistration = AircraftRegistration.valueOf(registration);

        if (repository.findByRegistration(aircraftRegistration).isPresent()) {
            return repository.findByRegistration(aircraftRegistration).get();
        }

        final Aircraft aircraft = new Aircraft(
                aircraftRegistration,
                AircraftRegistrationCountry.valueOf(registrationCountry),
                CabinConfiguration.valueOf(economySeats, businessSeats, firstClassSeats),
                AircraftOperationalStatus.active(),
                crewSize,
                company,
                aircraftModel,
                engineModel,
                manufactureYear
        );

        final Aircraft savedAircraft = repository.save(aircraft);
        System.out.println("Bootstrapped aircraft: " + aircraftRegistration);

        return savedAircraft;
    }

    static Airport registerAirportIfMissing(
            final AirportRepository repository,
            final String icao,
            final String iata,
            final String name,
            final String countryName,
            final String town,
            final double lat,
            final double lon,
            final double elev,
            final AirControlArea area) {

        final AirportICAOCode icaoCode = AirportICAOCode.valueOf(icao);

        // Verifica se já existe para não duplicar
        if (repository.ofIdentity(icaoCode).isPresent()) {
            return repository.ofIdentity(icaoCode).get();
        }

        // Usamos o Builder que criaste para garantir que as validações de domínio são feitas
        final AirportBuilder builder = new AirportBuilder();
        final Airport airport = builder
                .withData(icao, iata, name, countryName,town)
                .withLocation(lat, lon, elev)
                .inArea(area)
                .build();

        repository.save(airport);
        System.out.println("Bootstrapped airport: " + icaoCode);

        return airport;
    }
}
