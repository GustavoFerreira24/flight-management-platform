package isep.aisafe.application.pilot;

import isep.aisafe.aircraftmodel.domain.AircraftModelRepository;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.airtransportcompany.domain.AirTransportCompanyRepository;
import isep.aisafe.application.usermanagement.RegisterBackofficeUserController;
import isep.aisafe.application.usermanagement.UserRegistrationValidator;
import isep.aisafe.customercollaborator.domain.CustomerCollaborator;
import isep.aisafe.customercollaborator.domain.CustomerCollaboratorRepository;
import isep.aisafe.customercollaborator.domain.SystemUsername;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.infrastructure.persistence.PersistenceContext;
import isep.aisafe.pilot.domain.Pilot;
import isep.aisafe.pilot.domain.PilotRepository;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class ListPilotRosterController {


    private final AisafeAuthorizationService authz;
    private final PilotRepository pilotRepo;
    private final CustomerCollaboratorRepository collaboratorRepo;
    private final AirTransportCompanyRepository airTransportCompanyRepo;
    private final UserRegistrationValidator userValidator;
    private final RegisterBackofficeUserController backofficeUserController;

    public ListPilotRosterController() {
        this(
                new AisafeAuthorizationService(),
                PersistenceContext.repositories().pilots(),
                PersistenceContext.repositories().customerCollaborators(),
                PersistenceContext.repositories().airTransportCompanies(),
                new UserRegistrationValidator(),
                new RegisterBackofficeUserController()
        );
    }

    public ListPilotRosterController(
            final AisafeAuthorizationService authz,
            final PilotRepository pilotRepo,
            final CustomerCollaboratorRepository collaboratorRepo,
            final AirTransportCompanyRepository airTransportCompanyRepo,
            final UserRegistrationValidator userValidator,
            final RegisterBackofficeUserController backofficeUserController) {

        if (authz == null || pilotRepo == null || collaboratorRepo == null || airTransportCompanyRepo == null || userValidator == null || backofficeUserController == null) {
            throw new IllegalArgumentException("As dependências não podem ser nulas.");
        }

        this.authz = authz;
        this.pilotRepo = pilotRepo;
        this.collaboratorRepo = collaboratorRepo;
        this.airTransportCompanyRepo = airTransportCompanyRepo;
        this.userValidator = userValidator;
        this.backofficeUserController = backofficeUserController;
    }

    public Set<Pilot> listPilotRoster() {

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.AIR_COMPANY_CONFIGURATION);

        SystemUsername collaboratorUsername = SystemUsername.valueOf(authz.authenticatedUsername());
        CustomerCollaborator collaborator = collaboratorRepo.findBySystemUsername(collaboratorUsername)
                .orElseThrow(() -> new IllegalStateException("Authenticated user is not a customer collaborator."));

        if (!collaborator.isAssociatedWithAirTransportCompany()) {
            throw new IllegalStateException("Authenticated user is not associated with an air transport company.");
        }

        final AirTransportCompany airTransportCompany = collaborator.airTransportCompany();

        Set<Pilot> roster = new HashSet<>();

        for (Pilot pilot : pilotRepo.findAll()) {
            if (pilot.companyIATACode().equals(airTransportCompany.iataCode())) {
                roster.add(pilot);
            }
        }

        return roster;
    }
}
