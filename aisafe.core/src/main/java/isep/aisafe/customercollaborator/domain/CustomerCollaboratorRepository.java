package isep.aisafe.customercollaborator.domain;

import eapli.framework.domain.repositories.DomainRepository;
import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;

import java.util.List;
import java.util.Optional;

/**
 * Repository for customer collaborator aggregates.
 */
public interface CustomerCollaboratorRepository
        extends DomainRepository<CollaboratorEmail, CustomerCollaborator> {

    /**
     * Finds a collaborator by email.
     *
     * @param email collaborator email
     * @return matching collaborator, if one exists
     */
    Optional<CustomerCollaborator> findByEmail(CollaboratorEmail email);

    /**
     * Finds a collaborator by the username of the associated system user.
     *
     * @param systemUsername system username associated with the collaborator
     * @return matching collaborator, if one exists
     */
    Optional<CustomerCollaborator> findBySystemUsername(SystemUsername systemUsername);

    /**
     * Finds collaborators associated with an air transport company.
     *
     * @param airTransportCompany air transport company associated with the collaborators
     * @return matching collaborators
     */
    List<CustomerCollaborator> findByAirTransportCompany(AirTransportCompany airTransportCompany);

    /**
     * Finds collaborators associated with an air control area.
     *
     * @param airControlArea air control area associated with the collaborators
     * @return matching collaborators
     */
    List<CustomerCollaborator> findByAirControlArea(AirControlArea airControlArea);
}
