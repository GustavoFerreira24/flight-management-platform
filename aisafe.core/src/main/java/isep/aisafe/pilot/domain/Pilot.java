package isep.aisafe.pilot.domain;

import eapli.framework.domain.model.AggregateRoot;
import eapli.framework.domain.model.DomainEntities;
import eapli.framework.general.domain.model.EmailAddress;
import isep.aisafe.airtransportcompany.domain.IATACode;
import jakarta.persistence.*;

import javax.management.relation.Role;
import java.io.Serializable;

@Entity
@Table(name = "Pilot")
public class Pilot implements AggregateRoot<PilotID>, Serializable {

    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "PilotID", nullable = false))
    private PilotID id;

    @Column(nullable = false, name = "FirstName")
    private String firstName;

    @Column(nullable = false, name = "LastName")
    private String lastName;

    @Embedded
    @AttributeOverride(name = "email", column = @Column(name = "Email", nullable = false))
    private EmailAddress email;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "PhoneNumber", nullable = false))
    private PhoneNumber phoneNumber;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "issuedAt", column = @Column(name = "CertificationIssuedAt", nullable = false)),
            @AttributeOverride(name = "validUntil", column = @Column(name = "CertificationValidUntil", nullable = false))
    })
    private Certification certification;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "PilotStatus", nullable = false))
    private PilotStatus pilotStatus;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "CompanyIATACode", nullable = false))
    private IATACode companyIATACode;
    /**
     * Required by ORM.
     */
    protected Pilot() {
    }

    /**
     * Creates a new Pilot.
     *
     * @param id            the pilot's unique identifier
     * @param pilotStatus   the pilot's status
     * @param certification the pilot's certification details
     */
    public Pilot(
            final PilotID id,
            final Certification certification,
            final PilotStatus pilotStatus,
            final IATACode companyIATACode,
            final EmailAddress email,
            final PhoneNumber phoneNumber,
            final String firstName,
            final String lastName) {

        if (id == null) {
            throw new IllegalArgumentException("Pilot ID cannot be null.");
        }

        if (pilotStatus == null) {
            throw new IllegalArgumentException("Pilot status cannot be null.");
        }

        if (certification == null) {
            throw new IllegalArgumentException("Pilot certification cannot be null.");
        }

        if (companyIATACode == null) {
            throw new IllegalArgumentException("Company IATA code cannot be null.");
        }

        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null.");
        }

        if (phoneNumber == null) {
            throw new IllegalArgumentException("Phone number cannot be null.");
        }

        if (firstName == null) {
            throw new IllegalArgumentException("First name cannot be null.");
        }

        if (lastName == null) {
            throw new IllegalArgumentException("Last name cannot be null.");
        }

        this.id = id;
        this.pilotStatus = pilotStatus;
        this.certification = certification;
        this.companyIATACode = companyIATACode;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;

    }

    @Override
    public boolean sameAs(final Object other) {
        return DomainEntities.areEqual(this, other);
    }

    /**
     * Returns pilot id
     *
     * @return pilot id
     */
    @Override
    public PilotID identity() {
        return this.id;
    }

    /**
     * Returns pilot id
     *
     * @return pilot id
     */
    public PilotID pilotID() {
        return id;
    }

    /**
     * Returns pilot status
     *
     * @return pilot status
     */
    public PilotStatus pilotStatus() {
        return pilotStatus;
    }

    /**
     * Activates the pilot
     */
    public void activate() {
        this.pilotStatus = pilotStatus.activate();
    }

    /**
     * Deactivates the pilot
     */
    public void deactivate() {
        this.pilotStatus = pilotStatus.deactivate();
    }

    /**
     * Returns pilot certification
     *
     * @return pilot certification
     */
    public Certification certification() {
        return certification;
    }

    /**
     * Returns pilot companyIATACode
     *
     * @return pilot companyIATACode
     */
    public IATACode companyIATACode() {
        return companyIATACode;
    }

    public EmailAddress email() {
        return email;
    }

    public PhoneNumber phoneNumber() {
        return phoneNumber;
    }

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    @Override
    public int hashCode() {
        return DomainEntities.hashCode(this);
    }
}

