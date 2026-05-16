package isep.aisafe.aircraft.domain;

import eapli.framework.domain.model.AggregateRoot;
import eapli.framework.validations.Preconditions;
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
import java.time.Year;

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

    @Column(nullable = false)
    private int manufactureYear;

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

        this(
                registration,
                registrationCountry,
                cabinConfiguration,
                operationalStatus,
                crewSize,
                company,
                aircraftModel,
                engineModel,
                Year.now().getValue()
        );
    }

    /**
     * Creates a new aircraft with its manufacture year.
     *
     * @param registration the aircraft registration
     * @param registrationCountry the aircraft registration country
     * @param cabinConfiguration the cabin seat configuration
     * @param operationalStatus the aircraft operational status
     * @param crewSize the aircraft crew size
     * @param company the owning air transport company
     * @param aircraftModel the aircraft model
     * @param engineModel the engine model installed in the aircraft
     * @param manufactureYear the aircraft manufacture year
     */
    public Aircraft(
            final AircraftRegistration registration,
            final AircraftRegistrationCountry registrationCountry,
            final CabinConfiguration cabinConfiguration,
            final AircraftOperationalStatus operationalStatus,
            final int crewSize,
            final AirTransportCompany company,
            final AircraftModel aircraftModel,
            final EngineModel engineModel,
            final int manufactureYear) {

        Preconditions.nonNull(registration, "Aircraft registration cannot be null.");
        Preconditions.nonNull(registrationCountry, "Aircraft registration country cannot be null.");
        Preconditions.nonNull(cabinConfiguration, "Cabin configuration cannot be null.");
        Preconditions.nonNull(operationalStatus, "Aircraft operational status cannot be null.");
        Preconditions.isPositive(crewSize, "Crew size must be greater than zero.");
        Preconditions.nonNull(company, "Air transport company cannot be null.");
        Preconditions.nonNull(aircraftModel, "Aircraft model cannot be null.");
        Preconditions.nonNull(engineModel, "Engine model cannot be null.");
        Preconditions.ensure(manufactureYear >= 1903, "Manufacture year cannot be before 1903.");
        Preconditions.ensure(
                manufactureYear <= Year.now().getValue(),
                "Manufacture year cannot be in the future."
        );
        Preconditions.ensure(
                cabinConfiguration.totalSeats() <= aircraftModel.passengerCapacity(),
                "Total number of seats cannot exceed aircraft model passenger capacity."
        );

        this.registration = registration;
        this.registrationCountry = registrationCountry;
        this.cabinConfiguration = cabinConfiguration;
        this.operationalStatus = operationalStatus;
        this.crewSize = crewSize;
        this.company = company;
        this.aircraftModel = aircraftModel;
        this.engineModel = engineModel;
        this.manufactureYear = manufactureYear;
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

    public int manufactureYear() {
        return manufactureYear;
    }

    public int ageInYears() {
        return Year.now().getValue() - manufactureYear;
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
