package isep.aisafe.application.pilot;

import eapli.framework.general.domain.model.EmailAddress;
import eapli.framework.infrastructure.authz.domain.model.Role;
import eapli.framework.infrastructure.authz.application.AuthzRegistry; // <-- Adicionado
import eapli.framework.infrastructure.authz.application.UserManagementService; // <-- Adicionado em vez do controlador de backoffice
import isep.aisafe.aircraftmodel.domain.AircraftModel;
import isep.aisafe.aircraftmodel.domain.AircraftModelDesignator;
import isep.aisafe.aircraftmodel.domain.AircraftModelRepository;
import isep.aisafe.airtransportcompany.domain.IATACode;
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
import java.util.*;

public class RegisterPilotController implements RegisterPilotService {

    private final AisafeAuthorizationService authz;
    private final PilotRepository pilotRepo;
    private final CustomerCollaboratorRepository collaboratorRepo;
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final UserRegistrationValidator userValidator;
    private final UserManagementService userService; // <-- ALTERADO: Agora usa diretamente o serviço da framework
    private final AircraftModelRepository aircraftModelRepo;

    /**
     * Construtor por defeito utilizado pela Interface de Utilizador (UI).
     */
    public RegisterPilotController() {
        this(
                new AisafeAuthorizationService(),
                PersistenceContext.repositories().pilots(),
                PersistenceContext.repositories().customerCollaborators(),
                new UserRegistrationValidator(),
                AuthzRegistry.userService(), // <-- ALTERADO: Vai buscar o serviço ao teu AuthzRegistry
                PersistenceContext.repositories().aircraftModels()
        );
    }

    /**
     * Construtor utilizado para testes unitários.
     */
    RegisterPilotController(
            final AisafeAuthorizationService authz,
            final PilotRepository pilotRepo,
            final CustomerCollaboratorRepository collaboratorRepo,
            final UserRegistrationValidator userValidator,
            final UserManagementService userService, // <-- ALTERADO
            final AircraftModelRepository aircraftModelRepo) {

        if (authz == null || pilotRepo == null || collaboratorRepo == null || userValidator == null || userService == null || aircraftModelRepo == null) {
            throw new IllegalArgumentException("As dependências não podem ser nulas.");
        }

        this.authz = authz;
        this.pilotRepo = pilotRepo;
        this.collaboratorRepo = collaboratorRepo;
        this.userValidator = userValidator;
        this.userService = userService; // <-- ALTERADO
        this.aircraftModelRepo = aircraftModelRepo;
    }

    /**
     * Regista um novo piloto no sistema.
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
            final String phoneNumber,
            final String aircraftModelDesignator) {

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
        userService.registerNewUser(username, rawPassword, firstName, lastName, email, roles);

        IATACode companyIATACode = collaborator.airTransportCompany().iataCode();

        EmailAddress emailAddress = EmailAddress.valueOf(email);
        PhoneNumber phone = PhoneNumber.valueOf(phoneNumber);

        List<AircraftModelDesignator> aircraftModelsDesignators = getAircraftModels(aircraftModelDesignator);

        Set<AircraftModelDesignator> certifiedAircraftModels = new HashSet<>();
        aircraftModelsDesignators.forEach(certifiedAircraftModels::add);

        for (AircraftModelDesignator designator : certifiedAircraftModels) {
            if (aircraftModelRepo.findByDesignator(designator).isEmpty()) {
                throw new IllegalArgumentException("Aircraft model with designator " + designator + " does not exist.");
            }
        }

        Pilot pilot = new Pilot(pilotID, certification, pilotStatus, companyIATACode, emailAddress, phone, firstName, lastName, certifiedAircraftModels);

        return (pilotRepo.save(pilot));
    }

    private List<AircraftModelDesignator> getAircraftModels(String aircraftModelDesignator) {
        List<String> inputs = Arrays.asList(aircraftModelDesignator.split(","));
        List<AircraftModelDesignator> finalDesignators = new ArrayList<>();

        Iterable<AircraftModel> existingModels = aircraftModelRepo.findAll();

        for (String inputStr : inputs) {
            String cleanInput = inputStr.trim().toUpperCase();
            boolean found = false;

            for (AircraftModel model : existingModels) {
                String designatorStr = model.identity().toString().toUpperCase();

                if (designatorStr.equals(cleanInput) || designatorStr.startsWith(cleanInput + " ")) {
                    finalDesignators.add(model.identity());
                    found = true;
                    break;
                }
            }

            if (!found) {
                finalDesignators.add(AircraftModelDesignator.valueOf(cleanInput));
            }
        }

        return finalDesignators;
    }

    public Iterable<AircraftModel> getAllAircraftModels() {
        return this.aircraftModelRepo.findAll();
    }
}