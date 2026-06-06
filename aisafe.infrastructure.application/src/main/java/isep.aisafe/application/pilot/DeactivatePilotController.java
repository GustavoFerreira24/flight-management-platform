package isep.aisafe.application.pilot;

import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.application.usermanagement.RegisterBackofficeUserController;
import isep.aisafe.application.usermanagement.UserRegistrationValidator;
import isep.aisafe.customercollaborator.domain.CustomerCollaborator;
import isep.aisafe.customercollaborator.domain.CustomerCollaboratorRepository;
import isep.aisafe.customercollaborator.domain.SystemUsername;
import isep.aisafe.flightplan.domain.FlightPlan;
import isep.aisafe.flightplan.domain.FlightPlanRepository;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.pilot.domain.Pilot;
import isep.aisafe.pilot.domain.PilotID;
import isep.aisafe.pilot.domain.PilotRepository;
import isep.aisafe.infrastructure.persistence.PersistenceContext;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

public class DeactivatePilotController {

    private final PilotRepository pilotRepository;
    private final FlightPlanRepository flightPlanRepository;
    private final CustomerCollaboratorRepository collaboratorRepo;
    private final UserRegistrationValidator userValidator;
    private final RegisterBackofficeUserController backofficeUserController;
    private final AisafeAuthorizationService authz;

    public DeactivatePilotController() {

        this(
                new AisafeAuthorizationService(),
                PersistenceContext.repositories().pilots(),
                PersistenceContext.repositories().flightPlans(),
                PersistenceContext.repositories().customerCollaborators(),
                new UserRegistrationValidator(),
                new RegisterBackofficeUserController()
        );
    }

    DeactivatePilotController(
            final AisafeAuthorizationService authz,
            final PilotRepository pilotRepository,
            final FlightPlanRepository flightPlanRepository,
            final CustomerCollaboratorRepository collaboratorRepo,
            final UserRegistrationValidator userValidator,
            final RegisterBackofficeUserController backofficeUserController) {

        if (authz == null || pilotRepository == null || flightPlanRepository == null || collaboratorRepo == null || userValidator == null || backofficeUserController == null) {
            throw new IllegalArgumentException("As dependências não podem ser nulas.");
        }

        this.authz = authz;
        this.pilotRepository = pilotRepository;
        this.flightPlanRepository = flightPlanRepository;
        this.collaboratorRepo = collaboratorRepo;
        this.userValidator = userValidator;
        this.backofficeUserController = backofficeUserController;
    }

    public void deactivatePilot(String id) {

        if (id == null) {
            throw new IllegalArgumentException("O ID do piloto não pode ser nulo.");
        }

        PilotID pilotID = PilotID.valueOf(id);

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.AIR_COMPANY_CONFIGURATION);

        Pilot pilot = pilotRepository.ofIdentity(pilotID).orElseThrow(() -> new IllegalArgumentException("Piloto não encontrado."));

        Iterable<FlightPlan> flightPlans = flightPlanRepository.findAll();

        for (FlightPlan flightPlan : flightPlans) {
            if (flightPlan.pilot().identity().equals(pilotID)) {
                throw new IllegalStateException("Pilot still has active flight plans.");
            }
        }

        SystemUsername collaboratorUsername = SystemUsername.valueOf(authz.authenticatedUsername());
        CustomerCollaborator collaborator = collaboratorRepo.findBySystemUsername(collaboratorUsername)
                .orElseThrow(() -> new IllegalStateException("Authenticated user is not a customer collaborator."));

        if (!collaborator.isAssociatedWithAirTransportCompany()) {
            throw new IllegalStateException("Authenticated user is not associated with an air transport company.");
        }

        final AirTransportCompany airTransportCompany = collaborator.airTransportCompany();

        if (!pilot.companyIATACode().equals(airTransportCompany.iataCode())) {
            throw new IllegalStateException("Authenticated user is not associated with the same air transport company as the pilot.");
        }

        pilot.deactivate();

        pilotRepository.save(pilot);
    }

    public void activatePilot(PilotID id) {

        Pilot pilot = pilotRepository.ofIdentity(id).orElseThrow(() -> new IllegalArgumentException("Piloto não encontrado."));

        pilot.activate();

        pilotRepository.save(pilot);
    }
}
