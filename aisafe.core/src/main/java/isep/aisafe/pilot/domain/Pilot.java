package isep.aisafe.pilot.domain;

import eapli.framework.domain.model.AggregateRoot;
import eapli.framework.domain.model.DomainEntities;
import jakarta.persistence.*;

import java.io.Serializable;

public class Pilot implements AggregateRoot<PilotID>, Serializable {

    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "PilotID", nullable = false))
    private PilotID id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "PilotStatus", nullable = false))
    private PilotStatus pilotStatus;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "issuedAt", column = @Column(name = "CertificationIssuedAt", nullable = false)),
            @AttributeOverride(name = "validUntil", column = @Column(name = "CertificationValidUntil", nullable = false))
    })
    private Certification certification;

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
            final PilotStatus pilotStatus) {

        if (id == null) {
            throw new IllegalArgumentException("Pilot ID cannot be null.");
        }

        if (pilotStatus == null) {
            throw new IllegalArgumentException("Pilot status cannot be null.");
        }

        if (certification == null) {
            throw new IllegalArgumentException("Pilot certification cannot be null.");
        }

        this.id = id;
        this.pilotStatus = pilotStatus;
        this.certification = certification;
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

    @Override
    public int hashCode() {
        return DomainEntities.hashCode(this);
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
}

