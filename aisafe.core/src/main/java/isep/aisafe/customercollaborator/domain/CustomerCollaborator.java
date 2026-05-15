package isep.aisafe.customercollaborator.domain;

import eapli.framework.domain.model.AggregateRoot;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

import java.io.Serializable;

/**
 * Represents a collaborator registered for a customer.
 *
 * <p>The collaborator is always associated with exactly one customer and one distinct
 * system user. The system username is currently the collaborator email.</p>
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

    @Embedded
    private CustomerReference customerReference;

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
     * @param customerReference customer associated with the collaborator
     * @param systemUsername username of the system user associated with the collaborator
     */
    public CustomerCollaborator(
            final String firstName,
            final String lastName,
            final CollaboratorEmail email,
            final CustomerReference customerReference,
            final SystemUsername systemUsername) {

        ensureNotBlank(firstName, "Collaborator first name is required.");
        ensureNotBlank(lastName, "Collaborator last name is required.");

        if (email == null) {
            throw new IllegalArgumentException("Collaborator email is required.");
        }

        if (customerReference == null) {
            throw new IllegalArgumentException("Customer collaborator must be associated with a customer.");
        }

        if (systemUsername == null) {
            throw new IllegalArgumentException("Collaborator system username is required.");
        }

        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.email = email;
        this.customerReference = customerReference;
        this.systemUsername = systemUsername;
        this.active = true;
    }

    private void ensureNotBlank(final String value, final String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
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

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    public CollaboratorEmail email() {
        return email;
    }

    public CustomerReference customerReference() {
        return customerReference;
    }

    public SystemUsername systemUsername() {
        return systemUsername;
    }

    public boolean isActive() {
        return active;
    }

    public void disable() {
        this.active = false;
    }

    public void enable() {
        this.active = true;
    }
}
