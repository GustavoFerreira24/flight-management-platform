package isep.aisafe.aircraft.domain;

import eapli.framework.domain.model.AggregateRoot;
import isep.aisafe.aircraftmodel.domain.AircraftModel;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.enginemodel.domain.EngineModel;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import java.io.Serializable;

/**
 * Represents an aircraft registered in an air transport company's fleet.
 */
@Entity
public class Aircraft implements AggregateRoot<AircraftRegistration>, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long pk;

    @Version
    private Long version;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "REGISTRATION", nullable = false, unique = true))
    private AircraftRegistration registration;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "REGISTRATION_COUNTRY", nullable = false))
    private AircraftRegistrationCountry registrationCountry;

    @Embedded
    private CabinConfiguration cabinConfiguration;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "OPERATIONAL_STATUS", nullable = false))
    private AircraftOperationalStatus operationalStatus;

    @Column(nullable = false)
    private int crewSize;

    @ManyToOne(optional = false)
    private AirTransportCompany company;

    @ManyToOne(optional = false)
    private AircraftModel aircraftModel;

    @ManyToOne(optional = false)
    private EngineModel engineModel;

    /**
     * Required by ORM.
     */
    protected Aircraft() {
    }

    /**
     * Creates a new aircraft.
     *
     * @param registration the aircraft registration
     * @param registrationCountry the aircraft registration country
     * @param cabinConfiguration the cabin seat configuration
     * @param operationalStatus the aircraft operational status
     * @param crewSize the aircraft crew size
     * @param company the owning air transport company
     * @param aircraftModel the aircraft model
     * @param engineModel the engine model installed in the aircraft
     */
    public Aircraft(
            final AircraftRegistration registration,
            final AircraftRegistrationCountry registrationCountry,
            final CabinConfiguration cabinConfiguration,
            final AircraftOperationalStatus operationalStatus,
            final int crewSize,
            final AirTransportCompany company,
            final AircraftModel aircraftModel,
            final EngineModel engineModel) {

        if (registration == null) {
            throw new IllegalArgumentException("Aircraft registration cannot be null.");
        }

        if (registrationCountry == null) {
            throw new IllegalArgumentException("Aircraft registration country cannot be null.");
        }

        if (cabinConfiguration == null) {
            throw new IllegalArgumentException("Cabin configuration cannot be null.");
        }

        if (operationalStatus == null) {
            throw new IllegalArgumentException("Aircraft operational status cannot be null.");
        }

        if (crewSize <= 0) {
            throw new IllegalArgumentException("Crew size must be greater than zero.");
        }

        if (company == null) {
            throw new IllegalArgumentException("Air transport company cannot be null.");
        }

        if (aircraftModel == null) {
            throw new IllegalArgumentException("Aircraft model cannot be null.");
        }

        if (engineModel == null) {
            throw new IllegalArgumentException("Engine model cannot be null.");
        }

        if (cabinConfiguration.totalSeats() > aircraftModel.passengerCapacity()) {
            throw new IllegalArgumentException(
                    "Total number of seats cannot exceed aircraft model passenger capacity."
            );
        }

        if (!aircraftModel.hasCertifiedEngineModel(engineModel)) {
            throw new IllegalArgumentException("Engine model is not certified for this aircraft model.");
        }

        this.registration = registration;
        this.registrationCountry = registrationCountry;
        this.cabinConfiguration = cabinConfiguration;
        this.operationalStatus = operationalStatus;
        this.crewSize = crewSize;
        this.company = company;
        this.aircraftModel = aircraftModel;
        this.engineModel = engineModel;
    }

    @Override
    public AircraftRegistration identity() {
        return registration;
    }

    @Override
    public boolean sameAs(final Object other) {
        if (!(other instanceof Aircraft)) {
            return false;
        }

        final Aircraft that = (Aircraft) other;
        return this.identity().equals(that.identity());
    }

    public AircraftRegistration registration() {
        return registration;
    }

    public AircraftRegistrationCountry registrationCountry() {
        return registrationCountry;
    }

    public CabinConfiguration cabinConfiguration() {
        return cabinConfiguration;
    }

    public AircraftOperationalStatus operationalStatus() {
        return operationalStatus;
    }

    /**
     * Retires this aircraft from operational use.
     */
    public void decommission() {
        if (isDecommissioned()) {
            throw new IllegalStateException("Aircraft is already decommissioned.");
        }

        operationalStatus = AircraftOperationalStatus.decommissioned();
    }

    /**
     * Checks whether this aircraft has been retired.
     *
     * @return true if the aircraft is decommissioned
     */
    public boolean isDecommissioned() {
        return operationalStatus.isDecommissioned();
    }

    /**
     * Checks whether this aircraft can be assigned to new flights.
     *
     * @return true if the aircraft is available for flight assignment
     */
    public boolean canBeUsedForFlights() {
        return !isDecommissioned();
    }

    public int crewSize() {
        return crewSize;
    }

    public AirTransportCompany company() {
        return company;
    }

    public AircraftModel aircraftModel() {
        return aircraftModel;
    }

    public EngineModel engineModel() {
        return engineModel;
    }
}
