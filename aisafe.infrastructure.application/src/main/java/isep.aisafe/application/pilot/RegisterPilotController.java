package isep.aisafe.application.pilot;

import isep.aisafe.infrastructure.authz.*;
import isep.aisafe.infrastructure.persistence.*;
import isep.aisafe.pilot.domain.*;
import isep.aisafe.pilot.domain.PilotRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RegisterPilotController {

    private final AisafeAuthorizationService authz;
    private final PilotRepository pilotRepo;
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Construtor por defeito utilizado pela Interface de Utilizador (UI).
     * Inicializa os serviços e repositórios através do contexto de persistência.
     */
    public RegisterPilotController() {
        this(
                new AisafeAuthorizationService(),
                PersistenceContext.repositories().pilots()
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
            final PilotRepository pilotRepo) {

        if (authz == null || pilotRepo == null) {
            throw new IllegalArgumentException("As dependências não podem ser nulas.");
        }

        this.authz = authz;
        this.pilotRepo = pilotRepo;
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
            final String validUntilStr) {

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.AIR_COMPANY_CONFIGURATION);

        final PilotID pilotID = PilotID.valueOf(id);
        if (pilotRepo.ofIdentity(pilotID).isPresent()) {
            throw new IllegalArgumentException("Pilot's ID " + id + " already exists.");
        }

        Certification certification = new Certification(issuedAtStr, validUntilStr);
        PilotStatus pilotStatus = new PilotStatus(certification);

        Pilot pilot = new Pilot(pilotID, certification, pilotStatus);

        return (pilotRepo.save(pilot));



    }



}
