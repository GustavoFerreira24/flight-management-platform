package isep.aisafe.aircraftmodel.domain;

import eapli.framework.domain.model.AggregateRoot;
import isep.aisafe.enginemodel.domain.EngineModel;
import isep.aisafe.enginemodel.domain.EngineModelDesignator;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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

        if (designator == null) {
            throw new IllegalArgumentException("Aircraft model designator cannot be null.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Aircraft model type cannot be null.");
        }
        if (motorization == null) {
            throw new IllegalArgumentException("Aircraft model motorization cannot be null.");
        }
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
        if (passengerCapacity <= 0) {
            throw new IllegalArgumentException("Passenger capacity must be greater than zero.");
        }

        this.designator = designator;
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

        if (certifiedEngineModels == null || certifiedEngineModels.isEmpty()) {
            throw new IllegalArgumentException("Aircraft model must have at least one certified engine model.");
        }

        for (EngineModel engineModel : certifiedEngineModels) {
            addCertifiedVariant(engineModel);
        }
    }

    public void addCertifiedVariant(final EngineModel engineModel) {
        if (engineModel == null) {
            throw new IllegalArgumentException("Engine model cannot be null.");
        }

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
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be greater than zero.");
        }
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

    public int passengerCapacity() {
        return passengerCapacity;
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
}
