package isep.aisafe.enginemodel.domain;

import eapli.framework.domain.model.AggregateRoot;
import eapli.framework.validations.Preconditions;
import isep.aisafe.manufacturer.domain.Manufacturer;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import java.io.Serializable;
import java.util.Locale;

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

    @ManyToOne(optional = false)
    private Manufacturer manufacturer;

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

        this(designator, EngineModelManufacturer.from(designator), engineType, fuelType, power, thrust, tsfc);
    }

    public EngineModel(
            final EngineModelDesignator designator,
            final Manufacturer manufacturer,
            final EngineType engineType,
            final FuelType fuelType,
            final double power,
            final double thrust,
            final double tsfc) {

        Preconditions.nonNull(designator, "Engine model designator cannot be null.");
        Preconditions.nonNull(manufacturer, "Engine model manufacturer cannot be null.");
        Preconditions.ensure(
                manufacturer.canManufactureEngineModels(),
                "Manufacturer is not certified as an engine manufacturer."
        );
        Preconditions.nonNull(engineType, "Engine type cannot be null.");
        Preconditions.nonNull(fuelType, "Fuel type cannot be null.");
        Preconditions.ensure(power > 0, "Engine power must be greater than zero.");
        Preconditions.ensure(thrust > 0, "Engine thrust must be greater than zero.");
        Preconditions.ensure(tsfc > 0, "Engine TSFC must be greater than zero.");

        this.designator = designator;
        this.manufacturer = manufacturer;
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

    public Manufacturer manufacturer() {
        return manufacturer;
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

    /**
     * Returns a short technical description suitable for selection lists.
     *
     * @return engine model summary
     */
    public String description() {
        return String.format(
                Locale.ROOT,
                "Type: %s; fuel: %s; power: %.2f; thrust: %.2f; TSFC: %.3f",
                engineType,
                fuelType,
                power,
                thrust,
                tsfc
        );
    }

    private static final class EngineModelManufacturer {

        private EngineModelManufacturer() {
        }

        private static Manufacturer from(final EngineModelDesignator designator) {
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
