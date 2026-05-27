package isep.aisafe.manufacturer.domain;

import eapli.framework.domain.model.AggregateRoot;
import eapli.framework.validations.Preconditions;
import isep.aisafe.airtransportcompany.domain.Country;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

import java.io.Serializable;

@Entity
public class Manufacturer implements AggregateRoot<ManufacturerDesignator>, Serializable {

    @Id
    @GeneratedValue
    private Long pk;

    @Version
    private Long version;

    @Column(nullable = false)
    private String name;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "DESIGNATOR", nullable = false, unique = true))
    private ManufacturerDesignator designator;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "COUNTRY", nullable = false))
    private Country country;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "MANUFACTURER_TYPE", nullable = false))
    private ManufacturerType type;

    protected Manufacturer() {
    }

    public Manufacturer(
            final String name,
            final ManufacturerDesignator designator,
            final Country country,
            final ManufacturerType type) {

        Preconditions.ensure(name != null && !name.trim().isEmpty(), "Manufacturer name cannot be empty.");
        Preconditions.nonNull(designator, "Manufacturer designator cannot be null.");
        Preconditions.nonNull(country, "Manufacturer country cannot be null.");
        Preconditions.nonNull(type, "Manufacturer type cannot be null.");

        this.name = name.trim().replaceAll("\\s+", " ");
        this.designator = designator;
        this.country = country;
        this.type = type;
    }

    public static Manufacturer referenceFromText(final String manufacturer) {
        return new Manufacturer(
                manufacturer,
                ManufacturerDesignator.valueOf(manufacturer),
                Country.valueOf("Unknown"),
                ManufacturerType.valueOf("AIRCRAFT_AND_ENGINE")
        );
    }

    @Override
    public ManufacturerDesignator identity() {
        return designator;
    }

    @Override
    public boolean sameAs(final Object other) {
        if (!(other instanceof Manufacturer)) {
            return false;
        }

        final Manufacturer that = (Manufacturer) other;
        return identity().equals(that.identity());
    }

    public String name() {
        return name;
    }

    public ManufacturerDesignator designator() {
        return designator;
    }

    public Country country() {
        return country;
    }

    public ManufacturerType type() {
        return type;
    }

    public boolean canManufactureAircraftModels() {
        return type.supportsAircraftModels();
    }

    public boolean canManufactureEngineModels() {
        return type.supportsEngineModels();
    }
}
