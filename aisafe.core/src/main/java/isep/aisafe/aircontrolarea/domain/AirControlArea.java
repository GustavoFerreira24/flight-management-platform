package isep.aisafe.aircontrolarea.domain;

import eapli.framework.domain.model.AggregateRoot;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

import java.io.Serializable;

/**
 * Represents an air control area that can own customer collaborators.
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

    protected AirControlArea() {
    }

    public AirControlArea(final AirControlAreaCode code, final String name) {
        if (code == null) {
            throw new IllegalArgumentException("Air control area code is required.");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Air control area name is required.");
        }

        this.code = code;
        this.name = name.trim();
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
}