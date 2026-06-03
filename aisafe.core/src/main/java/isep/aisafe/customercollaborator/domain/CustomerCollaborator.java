package isep.aisafe.customercollaborator.domain;

import eapli.framework.domain.model.AggregateRoot;
import eapli.framework.validations.Preconditions;
import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;

import java.io.Serializable;

/**
 * Represents a collaborator registered for a customer.
 *
 * <p>The collaborator is always associated with exactly one customer and one distinct
 * system user. The customer can be either an air transport company or an air control area.
 * The system username is currently the collaborator email.</p>
 */
@Entity
public class CustomerCollaborator implements AggregateRoot<CollaboratorEmail>, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long pk;

    @Version
    private Long version;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "EMAIL", nullable = false, unique = true))
    private CollaboratorEmail email;

    @ManyToOne(optional = true)
    @JoinColumn(name = "AIR_TRANSPORT_COMPANY_IATA_CODE", referencedColumnName = "IATA_CODE")
    private AirTransportCompany airTransportCompany;

    @ManyToOne(optional = true)
    @JoinColumn(name = "AIR_CONTROL_AREA_CODE", referencedColumnName = "AREA_CODE")
    private AirControlArea airControlArea;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "SYSTEM_USERNAME", nullable = false, unique = true))
    private SystemUsername systemUsername;

    @Column(name = "ACTIVE", nullable = false)
    private boolean active;

    protected CustomerCollaborator() {
    }

    /**
     * Creates a customer collaborator with mandatory identity data.
     *
     * @param firstName collaborator first name
     * @param lastName collaborator last name
     * @param email collaborator email
     * @param airTransportCompany air transport company associated with the collaborator
     * @param systemUsername username of the system user associated with the collaborator
     */
    public CustomerCollaborator(
            final String firstName,
            final String lastName,
            final CollaboratorEmail email,
            final AirTransportCompany airTransportCompany,
            final SystemUsername systemUsername) {

        this(firstName, lastName, email, airTransportCompany, null, systemUsername);
    }

    /**
     * Creates a customer collaborator associated with an air control area.
     *
     * @param firstName collaborator first name
     * @param lastName collaborator last name
     * @param email collaborator email
     * @param airControlArea air control area associated with the collaborator
     * @param systemUsername username of the system user associated with the collaborator
     */
    public CustomerCollaborator(
            final String firstName,
            final String lastName,
            final CollaboratorEmail email,
            final AirControlArea airControlArea,
            final SystemUsername systemUsername) {

        this(firstName, lastName, email, null, airControlArea, systemUsername);
    }

    public CustomerCollaborator(
            final String firstName,
            final String lastName,
            final CollaboratorEmail email,
            final AirTransportCompany airTransportCompany,
            final AirControlArea airControlArea,
            final SystemUsername systemUsername) {

        ensureNotBlank(firstName, "Collaborator first name is required.");
        ensureNotBlank(lastName, "Collaborator last name is required.");
        Preconditions.nonNull(email, "Collaborator email is required.");
        Preconditions.ensure(
                (airTransportCompany != null) != (airControlArea != null),
                "Customer collaborator must be associated with exactly one customer."
        );
        Preconditions.nonNull(systemUsername, "Collaborator system username is required.");

        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.email = email;
        this.airTransportCompany = airTransportCompany;
        this.airControlArea = airControlArea;
        this.systemUsername = systemUsername;
        this.active = true;
    }

    private void ensureNotBlank(final String value, final String message) {
        Preconditions.ensure(value != null && !value.isBlank(), message);
    }

    @Override
    public CollaboratorEmail identity() {
        return email;
    }

    @Override
    public boolean sameAs(final Object other) {
        if (!(other instanceof CustomerCollaborator)) {
            return false;
        }

        final CustomerCollaborator that = (CustomerCollaborator) other;
        return identity().equals(that.identity());
    }

    /**
     * Returns collaborator first name
     *
     * @return collaborator first name
     */
    public String firstName() {
        return firstName;
    }

    /**
     * Returns collaborator last name
     *
     * @return collaborator last name
     */
    public String lastName() {
        return lastName;
    }

    /**
     * Returns collaborator email
     *
     * @return collaborator email
     */
    public CollaboratorEmail email() {
        return email;
    }

    /**
     * Returns air transport company associated with the collaborator.
     *
     * @return air transport company associated with the collaborator, or null if not applicable
     */
    public AirTransportCompany airTransportCompany() {
        return airTransportCompany;
    }

    /**
     * Returns air control area associated with the collaborator.
     *
     * @return air control area associated with the collaborator, or null if not applicable
     */
    public AirControlArea airControlArea() {
        return airControlArea;
    }

    public boolean isAssociatedWithAirTransportCompany() {
        return airTransportCompany != null;
    }

    public boolean isAssociatedWithAirControlArea() {
        return airControlArea != null;
    }

    /**
     * Returns  username of the system user associated with the collaborator
     *
     * @return  username of the system user associated with the collaborator
     */
    public SystemUsername systemUsername() {
        return systemUsername;
    }


    /**
     * Returns weather the collaborator is active or not
     *
     * @return  weather the collaborator is active or not
     */
    public boolean isActive() {
        return active;
    }

    /**
     * disables the collaborator
     */
    public void disable() {
        this.active = false;
    }

    /**
     * enables the collaborator
     */
    public void enable() {
        this.active = true;
    }
}
