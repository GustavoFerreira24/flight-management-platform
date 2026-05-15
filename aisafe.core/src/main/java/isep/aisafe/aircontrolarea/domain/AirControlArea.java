package isep.aisafe.aircontrolarea.domain;

import eapli.framework.domain.model.AggregateRoot;
import eapli.framework.validations.Preconditions;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import java.io.Serializable;

/**
 * Represents an air control area.
 */
@Entity
public class AirControlArea implements AggregateRoot<AirControlAreaCode>, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long pk;

    @Version
    private Long version;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "AREA_CODE", nullable = false, unique = true))
    private AirControlAreaCode code;

    @Column(nullable = false)
    private String name;

    @Embedded
    private GeographicBoundary geographicBoundary;

    @Embedded
    private MinimumFuelRequirement minimumFuelRequirement;

    protected AirControlArea() {
    }

    /**
     * Creates an air control area with only code and name.
     *
     * This constructor is kept for compatibility with existing code.
     *
     * @param code the air control area code
     * @param name the air control area name
     */
    public AirControlArea(final AirControlAreaCode code, final String name) {
        this(
                code,
                name,
                GeographicBoundary.valueOf(-90, 90, -180, 180),
                MinimumFuelRequirement.valueOf(1, FuelUnit.valueOf("KG"))
        );
    }

    /**
     * Creates an air control area.
     *
     * @param code the air control area code
     * @param name the air control area name
     * @param geographicBoundary the geographic boundary
     * @param minimumFuelRequirement the minimum fuel requirement
     */
    public AirControlArea(
            final AirControlAreaCode code,
            final String name,
            final GeographicBoundary geographicBoundary,
            final MinimumFuelRequirement minimumFuelRequirement) {

        Preconditions.nonNull(code, "Air control area code is required.");
        Preconditions.ensure(name != null && !name.isBlank(), "Air control area name is required.");
        Preconditions.nonNull(geographicBoundary, "Geographic boundary is required.");
        Preconditions.nonNull(minimumFuelRequirement, "Minimum fuel requirement is required.");

        this.code = code;
        this.name = name.trim().replaceAll("\\s+", " ");
        this.geographicBoundary = geographicBoundary;
        this.minimumFuelRequirement = minimumFuelRequirement;
    }

    @Override
    public AirControlAreaCode identity() {
        return code;
    }

    @Override
    public boolean sameAs(final Object other) {
        if (!(other instanceof AirControlArea)) {
            return false;
        }

        final AirControlArea that = (AirControlArea) other;
        return identity().equals(that.identity());
    }

    public AirControlAreaCode code() {
        return code;
    }

    public String name() {
        return name;
    }

    public GeographicBoundary geographicBoundary() {
        return geographicBoundary;
    }

    public MinimumFuelRequirement minimumFuelRequirement() {
        return minimumFuelRequirement;
    }
}
