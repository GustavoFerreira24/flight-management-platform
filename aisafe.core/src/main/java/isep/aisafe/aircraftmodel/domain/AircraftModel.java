package isep.aisafe.aircraftmodel.domain;

import eapli.framework.domain.model.AggregateRoot;
import eapli.framework.validations.Preconditions;
import isep.aisafe.enginemodel.domain.EngineModel;
import isep.aisafe.enginemodel.domain.EngineModelDesignator;
import isep.aisafe.manufacturer.domain.Manufacturer;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Entity
public class AircraftModel implements AggregateRoot<AircraftModelDesignator>, Serializable {

    @Id
    @GeneratedValue
    private Long pk;

    @Version
    private Long version;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "DESIGNATOR", nullable = false, unique = true))
    private AircraftModelDesignator designator;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "AIRCRAFT_TYPE"))
    private AircraftModelType type;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "MOTORIZATION"))
    private AircraftModelMotorization motorization;

    @ManyToOne(optional = false)
    private Manufacturer manufacturer;

    private double maxRange;
    private double emptyWeight;
    private double mtow;
    private double mzfw;
    private double maxFuelCapacity;
    private double serviceCeiling;
    private double cruiseSpeed;
    private double wingArea;
    private double dragCoefficient;
    private double liftCoefficient;
    private int passengerCapacity;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AircraftVariant> certifiedVariants = new HashSet<>();

    protected AircraftModel() {
    }

    public AircraftModel(
            final AircraftModelDesignator designator,
            final AircraftModelType type,
            final AircraftModelMotorization motorization,
            final double maxRange,
            final double emptyWeight,
            final double mtow,
            final double mzfw,
            final double maxFuelCapacity,
            final double serviceCeiling,
            final double cruiseSpeed,
            final double wingArea,
            final double dragCoefficient,
            final double liftCoefficient,
            final int passengerCapacity,
            final Collection<EngineModel> certifiedEngineModels) {

        this(
                designator,
                AircraftModelManufacturer.from(designator),
                type,
                motorization,
                maxRange,
                emptyWeight,
                mtow,
                mzfw,
                maxFuelCapacity,
                serviceCeiling,
                cruiseSpeed,
                wingArea,
                dragCoefficient,
                liftCoefficient,
                passengerCapacity,
                certifiedEngineModels
        );
    }

    public AircraftModel(
            final AircraftModelDesignator designator,
            final Manufacturer manufacturer,
            final AircraftModelType type,
            final AircraftModelMotorization motorization,
            final double maxRange,
            final double emptyWeight,
            final double mtow,
            final double mzfw,
            final double maxFuelCapacity,
            final double serviceCeiling,
            final double cruiseSpeed,
            final double wingArea,
            final double dragCoefficient,
            final double liftCoefficient,
            final int passengerCapacity,
            final Collection<EngineModel> certifiedEngineModels) {

        Preconditions.nonNull(designator, "Aircraft model designator cannot be null.");
        Preconditions.nonNull(manufacturer, "Aircraft model manufacturer cannot be null.");
        Preconditions.ensure(
                manufacturer.canManufactureAircraftModels(),
                "Manufacturer is not certified as an aircraft manufacturer."
        );
        Preconditions.nonNull(type, "Aircraft model type cannot be null.");
        Preconditions.nonNull(motorization, "Aircraft model motorization cannot be null.");
        ensurePositive(maxRange, "Maximum range");
        ensurePositive(emptyWeight, "Empty weight");
        ensurePositive(mtow, "MTOW");
        ensurePositive(mzfw, "MZFW");
        ensurePositive(maxFuelCapacity, "Maximum fuel capacity");
        ensurePositive(serviceCeiling, "Service ceiling");
        ensurePositive(cruiseSpeed, "Cruise speed");
        ensurePositive(wingArea, "Wing area");
        ensurePositive(dragCoefficient, "Drag coefficient");
        ensurePositive(liftCoefficient, "Lift coefficient");
        Preconditions.isPositive(passengerCapacity, "Passenger capacity must be greater than zero.");

        this.designator = designator;
        this.manufacturer = manufacturer;
        this.type = type;
        this.motorization = motorization;
        this.maxRange = maxRange;
        this.emptyWeight = emptyWeight;
        this.mtow = mtow;
        this.mzfw = mzfw;
        this.maxFuelCapacity = maxFuelCapacity;
        this.serviceCeiling = serviceCeiling;
        this.cruiseSpeed = cruiseSpeed;
        this.wingArea = wingArea;
        this.dragCoefficient = dragCoefficient;
        this.liftCoefficient = liftCoefficient;
        this.passengerCapacity = passengerCapacity;

        Preconditions.ensure(
                certifiedEngineModels != null && !certifiedEngineModels.isEmpty(),
                "Aircraft model must have at least one certified engine model."
        );

        for (EngineModel engineModel : certifiedEngineModels) {
            addCertifiedVariant(engineModel);
        }
    }

    public void addCertifiedVariant(final EngineModel engineModel) {
        Preconditions.nonNull(engineModel, "Engine model cannot be null.");

        ensureEngineModelNotDuplicated(engineModel.identity());
        ensureEngineTypeCompatibility(engineModel);

        certifiedVariants.add(AircraftVariant.createFrom(engineModel));
    }

    private void ensureEngineModelNotDuplicated(final EngineModelDesignator engineDesignator) {
        final boolean exists = certifiedVariants
                .stream()
                .anyMatch(variant -> variant.hasEngineModel(engineDesignator));

        if (exists) {
            throw new IllegalArgumentException("Engine model already certified for this aircraft model.");
        }
    }

    private void ensureEngineTypeCompatibility(final EngineModel engineModel) {
        if (!motorization.isCompatibleWith(engineModel.engineType().toString())) {
            throw new IllegalArgumentException("Engine type is not compatible with aircraft model motorization.");
        }
    }

    private void ensurePositive(final double value, final String fieldName) {
        Preconditions.ensure(value > 0, fieldName + " must be greater than zero.");
    }

    @Override
    public AircraftModelDesignator identity() {
        return designator;
    }

    @Override
    public boolean sameAs(final Object other) {
        if (!(other instanceof AircraftModel)) {
            return false;
        }

        final AircraftModel that = (AircraftModel) other;
        return this.identity().equals(that.identity());
    }

    public AircraftModelDesignator designator() {
        return designator;
    }

    public Manufacturer manufacturer() {
        return manufacturer;
    }

    public int passengerCapacity() {
        return passengerCapacity;
    }

    /**
     * Returns a short operational description suitable for selection lists.
     *
     * @return aircraft model summary
     */
    public String description() {
        return String.format(
                Locale.ROOT,
                "Type: %s; motorization: %s; range: %.0f km; capacity: %d passengers; cruise: %.0f km/h; certified engines: %d",
                type,
                motorization,
                maxRange,
                passengerCapacity,
                cruiseSpeed,
                certifiedVariants.size()
        );
    }

    /**
     * Returns the engine models certified for this aircraft model.
     *
     * @return certified engine models
     */
    public List<EngineModel> certifiedEngineModels() {
        final List<EngineModel> certifiedEngineModels = new ArrayList<>();

        for (final AircraftVariant variant : certifiedVariants) {
            certifiedEngineModels.add(variant.engineModel());
        }

        return certifiedEngineModels;
    }


    /**
     * Checks if an engine model is certified for this aircraft model.
     *
     * @param engineModel the engine model to check
     * @return true if the engine model is certified for this aircraft model
     */
    public boolean hasCertifiedEngineModel(final EngineModel engineModel) {
        if (engineModel == null) {
            return false;
        }

        final EngineModelDesignator engineDesignator = engineModel.identity();

        for (final AircraftVariant variant : certifiedVariants) {
            if (variant.hasEngineModel(engineDesignator)) {
                return true;
            }
        }

        return false;
    }

    private static final class AircraftModelManufacturer {

        private AircraftModelManufacturer() {
        }

        private static Manufacturer from(final AircraftModelDesignator designator) {
            if (designator == null) {
                return Manufacturer.referenceFromText("UNKNOWN");
            }

            final String[] parts = designator.toString().split("\\s*\\|\\s*", 2);
            if (parts.length == 2) {
                return Manufacturer.referenceFromText(parts[1]);
            }
            return Manufacturer.referenceFromText("UNKNOWN");
        }
    }
}
