package isep.aisafe.application.pilot;

import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.airtransportcompany.domain.IATACode;
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

public class AddPilotController {

    private final PilotRepository pilotRepository;
    private final CustomerCollaboratorRepository collaboratorRepo;
    private final AisafeAuthorizationService authz;

    public AddPilotController() {

        this(
                new AisafeAuthorizationService(),
                PersistenceContext.repositories().pilots(),
                PersistenceContext.repositories().customerCollaborators()
        );
    }

    AddPilotController(
            final AisafeAuthorizationService authz,
            final PilotRepository pilotRepository,
            final CustomerCollaboratorRepository collaboratorRepo) {

        if (authz == null || pilotRepository == null || collaboratorRepo == null) {
            throw new IllegalArgumentException("As dependências não podem ser nulas.");
        }

        this.authz = authz;
        this.pilotRepository = pilotRepository;
        this.collaboratorRepo = collaboratorRepo;
    }

    public void addPilot(String id) {

        if (id == null) {
            throw new IllegalArgumentException("O ID do piloto não pode ser nulo.");
        }

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.AIR_COMPANY_CONFIGURATION);

        PilotID pilotID = PilotID.valueOf(id);
        Pilot pilot = pilotRepository.ofIdentity(pilotID).orElseThrow(() -> new IllegalArgumentException("Piloto não encontrado."));

        SystemUsername collaboratorUsername = SystemUsername.valueOf(authz.authenticatedUsername());
        CustomerCollaborator collaborator = collaboratorRepo.findBySystemUsername(collaboratorUsername)
                .orElseThrow(() -> new IllegalStateException("Authenticated user is not a customer collaborator."));

        if (!collaborator.isAssociatedWithAirTransportCompany()) {
            throw new IllegalStateException("Authenticated user is not associated with an air transport company.");
        }

        final AirTransportCompany airTransportCompany = collaborator.airTransportCompany();
        final IATACode newPilotIATACode = airTransportCompany.iataCode();

        pilot.assignToCompany(newPilotIATACode);

        pilotRepository.save(pilot);
    }

    public Iterable<Pilot> getAllPilots() {
        return this.pilotRepository.findAll();
    }
}
