package isep.aisafe.pilot.domain;

import eapli.framework.domain.model.AggregateRoot;
import eapli.framework.domain.model.DomainEntities;
import eapli.framework.general.domain.model.EmailAddress;
import isep.aisafe.aircraftmodel.domain.AircraftModel;
import isep.aisafe.aircraftmodel.domain.AircraftModelDesignator;
import isep.aisafe.airtransportcompany.domain.IATACode;
import jakarta.persistence.*;
import org.eclipse.persistence.internal.jpa.parsing.GreaterThanEqualToNode;

import javax.management.relation.Role;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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
    @AttributeOverride(name = "value", column = @Column(name = "CompanyIATACode", nullable = true))
    private IATACode companyIATACode;

    @ElementCollection
    @CollectionTable(
            name = "PilotCertifiedAircraftModels",
            joinColumns = @JoinColumn(
                    name = "PilotID",
                    referencedColumnName = "PilotID"
            )
    )
    @AttributeOverride(name = "value", column = @Column(name = "Designator", nullable = false))
    private Set<AircraftModelDesignator> certifiedAircraftModels = new HashSet<>();

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
            final String lastName,
            final Set<AircraftModelDesignator> aircraftModelsDesignators) {

        if (id == null) {
            throw new IllegalArgumentException("Pilot ID cannot be null.");
        }

        if (pilotStatus == null) {
            throw new IllegalArgumentException("Pilot status cannot be null.");
        }

        if (certification == null) {
            throw new IllegalArgumentException("Pilot certification cannot be null.");
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

        if (aircraftModelsDesignators == null) {
            throw new IllegalArgumentException("Aircraft models designators cannot be null.");
        }

        this.id = id;
        this.pilotStatus = pilotStatus;
        this.certification = certification;
        this.companyIATACode = companyIATACode;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.certifiedAircraftModels = new HashSet<>();
        aircraftModelsDesignators.forEach(this.certifiedAircraftModels::add);

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
        return this.pilotStatus;
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
        return this.certification;
    }

    /**
     * Returns pilot companyIATACode
     *
     * @return pilot companyIATACode
     */
    public IATACode companyIATACode() {
        return this.companyIATACode;
    }

    public EmailAddress email() {
        return this.email;
    }

    public PhoneNumber phoneNumber() {
        return this.phoneNumber;
    }

    public String firstName() {
        return this.firstName;
    }

    public String lastName() {
        return this.lastName;
    }

    public Set<AircraftModelDesignator> certifiedAircraftModelsDesignators() {
        return certifiedAircraftModels;
    }

    @Override
    public int hashCode() {
        return DomainEntities.hashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return DomainEntities.areEqual(this, obj);
    }

    public String toString() {

        String certifiedAircraftModelsString = "";

        if (!certifiedAircraftModels.isEmpty()) {

            for (AircraftModelDesignator aircraftModelDesignator : certifiedAircraftModels) {
                certifiedAircraftModelsString += aircraftModelDesignator.toString() + "|| ";
            }

            if (certifiedAircraftModelsString.endsWith("|| ")) {
                certifiedAircraftModelsString = certifiedAircraftModelsString.substring(0, certifiedAircraftModelsString.length() - 3);
            }
        }

        return "Pilot " + firstName.toUpperCase() + " " + lastName.toUpperCase() +
                "\nPilot ID: " + id +
                "\nEmail: " + email +
                "\nPhone Number: " + phoneNumber +
                "\nCertification: " + certification +
                "\nPilot Status: " + pilotStatus.toString() +
                "\nCompany IATA Code: " + companyIATACode +
                "\nCertified Aircraft Models Designators: " + certifiedAircraftModelsString;
    }

    public void assignToCompany(IATACode newCompanyIataCode) {

        if (newCompanyIataCode == null) {
            throw new IllegalArgumentException("Company IATA code cannot be null.");
        }

        if (!pilotStatus.isValid() ) {
            throw new IllegalArgumentException("Pilot status must be valid to assign to a company.");
        }

        IATACode oldCompanyIataCode = this.companyIATACode;
        this.companyIATACode = newCompanyIataCode;
    }
}

