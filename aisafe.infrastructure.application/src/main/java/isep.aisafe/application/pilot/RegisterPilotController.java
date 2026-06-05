package isep.aisafe.application.pilot;

import eapli.framework.general.domain.model.EmailAddress;
import eapli.framework.infrastructure.authz.application.UserManagementService;
import eapli.framework.infrastructure.authz.domain.model.Role;
import eapli.framework.infrastructure.authz.domain.model.SystemUser;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.application.usermanagement.RegisterBackofficeUserController;
import isep.aisafe.application.usermanagement.UserRegistrationValidator;
import isep.aisafe.customercollaborator.domain.CustomerCollaborator;
import isep.aisafe.customercollaborator.domain.SystemUsername;
import isep.aisafe.infrastructure.authz.*;
import isep.aisafe.infrastructure.persistence.*;
import isep.aisafe.pilot.domain.*;
import isep.aisafe.pilot.domain.PilotRepository;
import isep.aisafe.customercollaborator.domain.CustomerCollaboratorRepository;
import isep.aisafe.usermanagement.domain.AisafeRoles;

import java.time.format.DateTimeFormatter;
import java.util.Set;

public class RegisterPilotController implements RegisterPilotService {

    private final AisafeAuthorizationService authz;
    private final PilotRepository pilotRepo;
    private final CustomerCollaboratorRepository collaboratorRepo;
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final UserRegistrationValidator userValidator;
    private final RegisterBackofficeUserController backofficeUserController;

    /**
     * Construtor por defeito utilizado pela Interface de Utilizador (UI).
     * Inicializa os serviços e repositórios através do contexto de persistência.
     */
    public RegisterPilotController() {
        this(
                new AisafeAuthorizationService(),
                PersistenceContext.repositories().pilots(),
                PersistenceContext.repositories().customerCollaborators(),
                new UserRegistrationValidator(),
                new RegisterBackofficeUserController()
        );
    }

    /**
     * Construtor utilizado para testes unitários, permitindo a injeção de dependências.
     *
     * @param authz serviço de autorização
     * @param pilotRepo repositório de aeroportos
     */
    RegisterPilotController(
            final AisafeAuthorizationService authz,
            final PilotRepository pilotRepo,
            final CustomerCollaboratorRepository collaboratorRepo,
            final UserRegistrationValidator userValidator,
            final RegisterBackofficeUserController backofficeUserController) {

        if (authz == null || pilotRepo == null || collaboratorRepo == null|| userValidator == null || backofficeUserController == null) {
            throw new IllegalArgumentException("As dependências não podem ser nulas.");
        }

        this.authz = authz;
        this.pilotRepo = pilotRepo;
        this.collaboratorRepo = collaboratorRepo;
        this.userValidator = userValidator;
        this.backofficeUserController = backofficeUserController;
    }

    /**
     * Regista um novo piloto no sistema.
     *
     * @param id ID do piloto (3 números)
     * @param issuedAtStr a partir de quando a certificação é válida
     * @param validUntilStr até quando a certificação é válida
     * @return o piloto registrado
     **/

    public Pilot registerPilot(
            final String id,
            final String issuedAtStr,
            final String validUntilStr,
            final String username,
            final String firstName,
            final String lastName,
            final String email,
            final String rawPassword,
            final String phoneNumber
            ) {

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.AIR_COMPANY_CONFIGURATION);

        final PilotID pilotID = PilotID.valueOf(id);
        if (pilotRepo.ofIdentity(pilotID).isPresent()) {
            throw new IllegalArgumentException("Pilot's ID " + id + " already exists.");
        }

        Certification certification = new Certification(issuedAtStr, validUntilStr);
        PilotStatus pilotStatus = new PilotStatus(certification);

        SystemUsername collaboratorUsername = SystemUsername.valueOf(authz.authenticatedUsername());
        CustomerCollaborator collaborator = collaboratorRepo.findBySystemUsername(collaboratorUsername)
                .orElseThrow(() -> new IllegalStateException("Authenticated user is not a customer collaborator."));

        if (!collaborator.isAssociatedWithAirTransportCompany()) {
            throw new IllegalStateException("Authenticated user is not associated with an air transport company.");
        }

        Set<Role> roles = Set.of(AisafeRoles.PILOT);

        userValidator.validate(username, rawPassword, firstName, lastName, email, roles);
        backofficeUserController.registerUser(username, rawPassword, firstName, lastName, email, roles);

        IATACode companyIATACode = collaborator.airTransportCompany().iataCode();

        EmailAddress emailAddress = EmailAddress.valueOf(email);
        PhoneNumber phone = PhoneNumber.valueOf(phoneNumber);

        Pilot pilot = new Pilot(pilotID, certification, pilotStatus, companyIATACode, emailAddress, phone, firstName, lastName);

        return (pilotRepo.save(pilot));

    }



}
