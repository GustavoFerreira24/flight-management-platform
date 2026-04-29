package isep.aisafe.enginemodel.domain;

import eapli.framework.domain.model.AggregateRoot;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import java.io.Serializable;

@Entity
public class EngineModel implements AggregateRoot<EngineModelDesignator>, Serializable {

    @Id
    @GeneratedValue
    private Long pk;

    @Version
    private Long version;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "DESIGNATOR", nullable = false, unique = true))
    private EngineModelDesignator designator;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "ENGINE_TYPE"))
    private EngineType engineType;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "FUEL_TYPE"))
    private FuelType fuelType;

    private double power;
    private double thrust;

    private double tsfc;

    protected EngineModel() {
    }

    public EngineModel(
            final EngineModelDesignator designator,
            final EngineType engineType,
            final FuelType fuelType,
            final double power,
            final double thrust,
            final double tsfc) {

        if (designator == null) {
            throw new IllegalArgumentException("Engine model designator cannot be null.");
        }

        if (engineType == null) {
            throw new IllegalArgumentException("Engine type cannot be null.");
        }

        if (fuelType == null) {
            throw new IllegalArgumentException("Fuel type cannot be null.");
        }

        if (power <= 0) {
            throw new IllegalArgumentException("Engine power must be greater than zero.");
        }

        if (thrust <= 0) {
            throw new IllegalArgumentException("Engine thrust must be greater than zero.");
        }

        if (tsfc <= 0) {
            throw new IllegalArgumentException("Engine TSFC must be greater than zero.");
        }

        this.designator = designator;
        this.engineType = engineType;
        this.fuelType = fuelType;
        this.power = power;
        this.thrust = thrust;
        this.tsfc = tsfc;
    }

    @Override
    public EngineModelDesignator identity() {
        return designator;
    }

    @Override
    public boolean sameAs(final Object other) {
        if (!(other instanceof EngineModel)) {
            return false;
        }

        final EngineModel that = (EngineModel) other;
        return this.identity().equals(that.identity());
    }

    public EngineModelDesignator designator() {
        return designator;
    }

    public EngineType engineType() {
        return engineType;
    }

    public FuelType fuelType() {
        return fuelType;
    }

    public double power() {
        return power;
    }

    public double thrust() {
        return thrust;
    }

    public double tsfc() {
        return tsfc;
    }
}
